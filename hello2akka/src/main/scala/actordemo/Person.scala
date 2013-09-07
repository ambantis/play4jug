package actordemo

import akka.actor.{ActorRef, Actor}
import scala.util.Random

/**
 * hello2akka
 *
 * User: Alexandros Bantis
 * Date: 9/7/13
 * Time: 10:41 AM
 */
class Person(bankOfAkka: ActorRef) extends Actor {
  import Person._
  import BankAccount._
  def receive: Actor.Receive = {

    case Shop =>
        val price = Random.nextInt(20)
        println(s"${self.path.name} says, 'I want to buy something that costs $price dollars, " +
            "but first I'll have a coffee'")
        Thread.sleep(Random.nextInt(1000))
        bankOfAkka ! Withdraw(price)

    case ChaChing =>
      context.parent ! BoughtSomething

    case NotSufficientFunds =>
      self ! Shop

    case ZeroBalance =>
      context.parent ! ZeroBalance
  }

  def shopTillYouDrop(): Unit = {

  }
}


object Person {

  object Seppuku
  object Shop
  object BoughtSomething
}


