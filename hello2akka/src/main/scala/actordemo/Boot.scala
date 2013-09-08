package actordemo

import akka.actor.{Props, ActorSystem}

/**
 * hello2akka
 *
 * User: Alexandros Bantis
 * Date: 9/7/13
 * Time: 10:14 AM
 */
object Boot extends App {
  import ShoppingSpree._

  implicit val system = ActorSystem("ActorDemo")

  val bank = system.actorOf(Props(new BankAccount()), "bankOfAkka")
  val shoppingSpree = system.actorOf(Props(new ShoppingSpree(bank)), "ShoppingSpree")

  shoppingSpree ! PartyTime
}
