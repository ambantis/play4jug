Intro to Akka Actors and Futures 101
====================================

Setup
-----

To run this application, you will need to have sbt 0.13.0 installed on your machine,
you can download it at http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html

From the command line (at /hello2akka), enter sbt interactive mode and compile project:

```bash
% sbt
Loading /usr/share/java/sbt/bin/sbt-launch-lib-bash
[info] Loading project definition from /home/ambantis/Documents/dev/play/play4jug/hello2akka/project
[info] Set current project to hello2akka (in build file:/home/ambantis/Documents/dev/play/play4jug/hello2akka/)
> compile
[info] Compiling 1 Scala source to /home/ambantis/Documents/dev/play/play4jug/hello2akka/target/scala-2.10/classes...
[success] Total time: 7 s, completed Sep 7, 2013 5:32:43 PM
> gen-idea
```

Note that the command to generate idea files is slightly different than with Play.

Thread-Based Concurrency
------------------------

In a traditional java application, you can create as many threads as you wish. However, there
are many problems with having multiple threads access mutable state. As an illustration, take
a look at the [BankAccount](src/main/java/threads/BankAccount.java) and
[RyanAndMonica](src/main/java/threads/RyanAndMonicaJob.java) classes that are borrowed from the excellent
book [Head First Java](http://shop.oreilly.com/product/9780596009205.do)

From the sbt interactive prompt (assuming you have compiled the application), go ahead and run
this job:

```bash
    > runMain threads.RyanAndMonicaJob
```

Ryan and Monica have $100 in their bank account and go on a shopping spree and each make ten purchases
of $10 each. Each time Ryan and Monica want to make a purchase, the thread checks that there is enough
money in the bank account and if there is, it sleeps for a half-second and then makes the withdrawal.
The result is that Monica and Ryan end up overdrawing their account because the other thread changes
the balance in the middle of the "transaction".

There are many ways to deal with managing concurrent threads, all of them very complicated in a non-trivial
application.

Actor-Based Concurrency
-----------------------

In an actor-based system, (such as Play!), there are fewer concurrency issues to deal with, and there are
no thread-pools to manage. The number of threads you have is based on the number of cores the JVM has access to.

From the sbt interactive prompt, go ahead and run this job:

```bash
> runMain actordemo.Boot
```

This time, we have three actors (not including the [Boot](src/main/scala/actordemo/Boot.scala)):
  1. [ShoppingSpree](src/main/scala/actordemo/ShoppingSpree.scala)
  2. [BankAccount](src/main/scala/actordemo/BankAccount.scala)
  3. [Shopper](src/main/scala/actordemo/Shopper.scala)

The `ShoppingSpree` Actor is a supervisor with two children: Monica and Ryan (instances of the `Shopper` actor).
It is passed an instance of the `BankAccount` actor in the constructor. The `Boot` object is a main method that
starts the application rolling with a message to `ShoppingSpree`:

```scala
shoppingSpree ! PartyTime
```

This syntax says: Send a message to `shoppingSpree`, the message being the object `PartyTime`
(scala objects are singleton classes). Note that the Exclamation Point is syntactic sugar for:

```scala
shoppingSpree.tell(PartyTime)
```

Now, `shoppingSpree` receives the message and responds to it by sending a message to all of its
children (Monica and Ryan) that they should `Shop`.

```scala
context.children.foreach(_ ! Shop)
```

(The underscore removes the need in a java foreach statement to name the individual element. The
java version of this statement might look like this:

```java
for ( ActorRef shopper : context.children) {
    shopper.tell(Shop)
}
```

The `Shopper` (Monica and Ryan) finds an item that costs up to $20, has a coffee (sleeps for up to a second),
then sends a message to the `BankAccount` to make a withdrawal for the purchase. The Bank then processes the
message and responds back to the Shopper based upon the account balance and purchase price:

  1. `balance < 0` means that the bank sends a poison pill to the Shopper which causes it to die (the
      penalty for violating the bank's rules).
  2. `balance == 0` means that the party's over, and the shoppers should leave the mall and go back
      to work. The Bank actor sends a message to the `ShoppingSpree` actor to stop the simulation.
  3. `ChaChing` means that there is enough money in the account and the transaction is approved by the bank.
  4. `NSF` means that the purchase price exceeds the bank balance and the transaction is not approved.

When the `Shopper` receives a `ChaChing` message, it sends itself a message to shop some more. If the
Shopper receives an `NSF` message, then it asks the `Bank` what is the current balance, uses that amount to
reset the ceiling on the purchase price, and then sends itself a message to shop some more.

When the balance eventually reaches zero, and no more purchases can be made, the `Bank` actor sends a
message to the `ShoppingSpree` actor that the `PartyIsOver`, which causes the `ShoppingSpree` actor to take
a `PoisonPill` and then shutdown the system. Basically `PoisonPill` means that the actor should tell all
its children to finish processing whatever messages are in the queue and then die; after processing
whatever messages are in its queue, it too dies.

If `Shopper` were to try to make a purchase while the bank balance is negative, the `BankAccount` would be so angry
that it would send the `Shopper` a message to take a `PoisonPill`, but this never happens. Why?

It turns out that the messages are all passing immutable objects (cannot be altered). The `BankAccount` actor
never exposes any methods or state. All the shoppers can do is send a message to the bank. The bank
processes the messages (the default algorithm is FIFO), thus requests to alter state never occur
concurrently.

Akka Futures
------------

So, when we say that Play! uses Akka as its concurrency framework, it makes sense now why that the system
is more fault tolerant and less susceptible to insidious concurrency bugs, however, it does not explain how
Play! is able to do away with the type of thread pools that exist in Apache Tomcat.

You may have noticed some funny looking code in [Shopper](src/main/scala/actordemo/Shopper.scala)

```scala
case NSF =>
  val futureUpdatedLimit: Future[Int] = (bankOfAkka ? Balance).mapTo[Int]
  futureUpdatedLimit.map { updatedLimit => limit = updatedLimit }
  self ! Shop
```

So, what's going on here? When the Shopper get's an `NSF` message, it realizes that it was trying to spend more money
than it has in the Bank. So, it `asks` the Bank what is its balance. We implicitly pass the execution context and a time limit
of 5 seconds to compute the computation, which is passed off to another actor (behind the scenes). When
the Bank responds to this message with the value (mapped to an Int), we update the value of `limit` to be a new value.

From the sbt interactive prompt (assuming you have compiled the application), go ahead and run
this job:

```bash
> runMain futuredemo.Boot
```

and you should see an output similar to this:

```bash
[info] Running futuredemo.Boot
this is before the expensive computation
this is after the expensive computation
[success] Total time: 0 s, completed Sep 7, 2013 6:06:17 PM
> first triangle number with over 500 divisors = 76576500 with a time of 1.008 secs
```

The scala object [Boot](src/main/scala/futuredemo/Boot.scala) tries to solve Project Euler
[Problem 13](http://projecteuler.net/problem=12). We have a println statement on line 15, then
lines 17-19 make a call to the [EulerProject012](src/main/scala/futuredemo/EulerProblem012.scala) object
to find the solution. Then there is another println statement on line 25.

The output shows that the program completes with a status of `success` in 0 seconds, and then afterwards
appears the output of the expensive computation, paradoxically, after the program successfully
completes. What happened?!?

Let's take a look:

```scala
val futureResult: Future[(Int, Double)] = future {
  getNaturalNumbersWith(divisors = fiveHundred)
}

futureResult.map { x =>
  println(s"first triangle number with over $fiveHundred divisors = ${x._1} with a time of ${x._2} secs")
  }
```

`futureResult` is in fact a placeholder for the results of the computation, when it is completed. Here's
what the scala source code says about `future`:

```scala
/** Starts an asynchronous computation and returns a `Future` object with the result of that computation.
 *
 *  The result becomes available once the asynchronous computation is completed.
 *
 *  @tparam T       the type of the result
 *  @param body     the asynchronous computation
 *  @param execctx  the execution context on which the future is run
 *  @return         the `Future` holding the result of the computation
 */
def future[T](body: =>T)(implicit execctx: ExecutionContext): Future[T] = Future[T](body)
```

So, what happened is that the executing thread encountered the expensive computation, handed off the
job of computing the result, and then kept on going. This is why `"this is after the expensive computation"`
printed before the results were received.

What this means is that a Play application is asynchronus. If the box provides two cores to the JVM, then
Play will run on exactly two threads. When the actor encounters an expensive process, such as a database or
webservice call, it simply hands off the expensive process to another actor and keeps on going.

Now, there is currently one limitation on this, which is that jdbc drivers are synchronous. However,
it is possible to make asynchronous calls to MongoDB, and asynchronous drivers are one the way for
SQL as well (there is an asynchronous postgres driver on github, though I have not tested it).

What does this mean? It means Play! scales well and easily. It is possible to get similar results
with Threaded (synchronous) code, but it can be much more complex, which is why LinkedIn switched to
Play earlier this year: [Play Framework: async I/O without the thread pool and callback hell](http://engineering.linkedin.com/play/play-framework-async-io-without-thread-pool-and-callback-hell).
