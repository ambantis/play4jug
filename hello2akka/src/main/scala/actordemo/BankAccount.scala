package actordemo

import akka.actor.{PoisonPill, Actor}

/**
 * ${PROJECT_NAME}
 *
 * User: Alexandros Bantis
 * Date: 9/7/13
 * Time: 10:23 AM
 */
object BankAccount {
  case class Withdraw(amount: Int)
  object Balance
  object ChaChing
  object NSF
}

class BankAccount extends Actor {

  val shoppingSpree = context.actorSelection("/user/ShoppingSpree")

  import BankAccount._
  import ShoppingSpree._
  var balance = 100

  def receive: Actor.Receive = {

    case Withdraw(amt) =>
      println(s"Bank says, '${sender.path.name} asks to withdraw $amt'")
      if (balance == 0) {
        println("Bank says, 'The shoppers have a zero balance, time to go back to work.'")
        shoppingSpree ! PartyIsOver
      } else if (balance < 0) {
        println(s"Bank says, '${sender.path.name}, you are overdrawn, prepare to die!'")
        sender ! PoisonPill
      } else if (balance >= amt) {
        balance -= amt
        println(s"Bank says, 'withdrawal of ${sender.path.name} completed, balance = $balance'")
        sender ! ChaChing
      } else {
        println(s"Bank says, 'Not enough for ${sender.path.name} to withdraw $amt, balance is $balance'")
        sender ! NSF
      }

    case Balance =>
      sender ! balance

  }

}
