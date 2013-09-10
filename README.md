Play! with Java @ LAJUG 9/10/13
==============================

Intro
-----

This is going to be a hands-on demonstration, so I really encourage everyone to
bring a laptop and pairup. I will do a brief amount of power-pointish stuff,
but primarily my goal is to demo Play! and give you a chance to experience a
bit of it first-hand.

Setup
-----

I'll be using linux with Intellij 12. First, if you don't have IntelliJ 12, you
can download free 30-day trial at http://www.jetbrains.com. You will want to 
install the `Play 2.0` plugin and maybe scala plugin as well.

Next, you may want to have Scala installed on your computer because we'll do a
tiny bit of playing with it at the command line, here http://www.scala-lang.org. 
If you are having any difficulty with this, I wouldn't worry about it too much 
as the Play! application has a scala repl you can use.

Next, you'll want to have play installed http://www.playframework.com/.

Play! uses [sbt](http://www.scala-sbt.org/) as the default build system. You will want
to install sbt as well if you would like to view sources from within IntelliJ IDEA.
Afer you install sbt, add with the following contents shown below to `~/.sbt/plugins`
and `~/.sbt/plugins/0.13/plugins`

```bash
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.5.1")
```

Creating A Hello World Application
----------------------------------
Play is a "full-stack" framework. It includes a built-in server (netty) and
build system (sbt). It is possible to create stand-alone war files to deploy
to other servers and different build tools, such as gradle or maven. However,
if you have play installed, then you have everything you need. Netty handles
really well, and although sbt has a reputation for being complex, within the
context of a Play! application, it is pretty simple to use.

At the command line and go to the location where you wish to create your
play application, then type:

    % play new hello

You will get a dialog box asking you the name of the application and whether it
should be a java or scala application. Your application file structure should
look something like this:
    
    % tree
      .
      ├── app
      │   ├── controllers
      │   │   └── Application.java
      │   └── views
      │       ├── index.scala.html
      │       └── main.scala.html
      ├── conf
      │   ├── application.conf
      │   └── routes
      ├── project
      │   ├── build.properties
      │   ├── Build.scala
      │   └── plugins.sbt
      ├── public
      │   ├── images
      │   │   └── favicon.png
      │   ├── javascripts
      │   │   └── jquery-1.9.0.min.js
      │   └── stylesheets
      │       └── main.css
      ├── README
      └── test
          ├── ApplicationTest.java
          └── IntegrationTest.java

The `conf/routes` file maps http verb/uri combinations to specific controller
functions. The `project/Build.scala` is an sbt configuration where you will
declare your managed dependencies. The rest should be self-explanatory at this
point. Now, cd into the directory and do

```bash
% play compile
```

If you installed sbt (see above) and wish to view sources from within IntelliJ,
then do

```bash
% sbt "gen-idea no-sbt-build-module"
```

otherwise, you can have play create the IntelliJ idea files:

```bash
% play "idea no-sbt-build-module" 
```

Now, do

```bash
% play run
```

Give it a moment to start up, and if you point your browser to localhost:9000,
you should see the basic hello-world application running. Now, a bit of
explanation about these commands.

* reload: you will only need to do this if you make changes to Build.scala
  and wish to make play aware of those changes while in interactive mode.
* update: retrives new dependencies you've specified in Build.scala to a local
  repository.

If you make these sbt actions from the command line, you can specify multiple
steps, but in interactive-mode, you can only do one at a time.

Next, startup IntelliJ Idea. You should already have the play 2.0 and scala
plugins installed (you won't need the scala plugin if you will not be creating any
scala classes). Now open the project. If you go to File -> Project Settings,
you should see 2 errors within a red bar on the bottom. If you click on the
text `2 errors found`, it will tell you:

* Library 'Scala 2.9.2' is not used
* Library 'Scala 2.10.0' is not used

To the right, you should see a little light bulb for each error. Clicking on it
will present 2 choices:

* Add to Dependencies
* Remove Library

Go ahead and remove the libraries (scala 2.9.2 is used by the version of sbt that is
built into Play 2.1.3, and you won't need scala 2.10.0 unless you'll be writing scala
classes/objects).

Now, within the project tree, directories should be marked as Sources/Test/Excluded
as follows (you may need to delete some of the assignments the plugin made):

* Sources: /app, /conf, target/scala-2.10/src_managed
* Excluded: /.idea, .idea_modules, /target/resolution-cache,
  /target/scala-2.10/cache, /target/scala-2.10/classes,
  /target/scala-2.10/classes_managed, /target/streams
* Test: /test

Congrats!, You've built your first Play! application and managed to get
IntelliJ to understand your project. You're off to a great start!

Outline for the Presentation
----------------------------

For the presentation, we'll use the first couple of minutes to make sure that
everyone was able to get Play installed, run the `hello` project, and open it
within Intellij. Next we'll review the first two benefits of Play (Actors and
Asynchronous). Then we'll demo a Play application.

Play! has several benefits:
  * Actor-based Concurrency
  * Asynchronous
  * Compiled (everything, view html templates are really scala functions)
  * Fun

To highlight what is Actor-Based Concurrency, we'll take a look at
[actordemo](hello2akka/src/main/scala/actordemo) and compare and examine how
actors simplify dealing with concurrency.

To highlight what is Asynchronous, we'll take a look at
[futuredemo](hello2akka/src/main/scala/futuredemo)

Then, we'll do some live coding with Play!
