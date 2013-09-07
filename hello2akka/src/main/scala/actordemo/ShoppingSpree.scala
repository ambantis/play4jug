package actordemo

import akka.actor._
import akka.actor.Terminated
import actordemo.BankAccount.ZeroBalance

/**
 * hello2akka
 *
 * User: Alexandros Bantis
 * Date: 9/7/13
 * Time: 11:21 AM
 */
class ShoppingSpree(bank: ActorRef) extends Actor {
  import ShoppingSpree._
  import BankAccount._
  import Person._
  val monica = context.actorOf(Props(new Person(bank)), "Monica")
  val ryan = context.actorOf(Props(new Person(bank)), "Ryan")
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

    case ZeroBalance =>
      self ! PoisonPill
      context.system.shutdown()

    case StartSimulation =>
      context.children.foreach(_ ! Shop)

    case BoughtSomething =>
      sender ! Shop
  }
}

object ShoppingSpree {
  object StartSimulation
  object StopSimulation
}
