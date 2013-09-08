package actordemo

import akka.actor._
import akka.actor.Terminated

/**
 * hello2akka
 *
 * User: Alexandros Bantis
 * Date: 9/7/13
 * Time: 11:21 AM
 */
class ShoppingSpree(bank: ActorRef) extends Actor {
  import ShoppingSpree.{PartyTime, PartyIsOver}
  import Shopper.Shop
  val monica = context.actorOf(Props(new Shopper(bank)), "Monica")
  val ryan = context.actorOf(Props(new Shopper(bank)), "Ryan")
  context.watch(monica)
  context.watch(ryan)

  def receive = {

    // receiving message that one of the children has died
    case Terminated(child) =>
      println(s"${child.path.name} is dead")
      if (context.children.isEmpty) {
        println("all children are dead, proceeding to shutdown simulation")
        self ! PoisonPill
        context.system.shutdown()
      }

    case PartyIsOver =>
      self ! PoisonPill
      context.system.shutdown()

    case PartyTime =>
      println(s"the path of ShoppingSpree = ${context.self.path}")
      context.children.foreach(_ ! Shop)

  }
}

object ShoppingSpree {
  object PartyTime
  object PartyIsOver
}
