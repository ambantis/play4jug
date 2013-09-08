package actordemo

import akka.actor.{ActorRef, Actor}
import akka.pattern.ask
import akka.util.Timeout
import scala.util.Random
import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * hello2akka
 *
 * User: Alexandros Bantis
 * Date: 9/7/13
 * Time: 10:41 AM
 */
class Shopper(bankOfAkka: ActorRef) extends Actor {
  import Shopper._
  import BankAccount._

  implicit val ec = context.dispatcher
  implicit val timeout = Timeout(5 seconds)


  // look for items that cost up to this much
  var limit: Int = 20
  var price: Int = _

  def receive = {

    case Shop =>
        price = calcPrice()
        println(s"${self.path.name} says, 'I want to buy something that costs $price dollars, " +
            "but first I'll have a coffee'")
        Thread.sleep(Random.nextInt(1000))
        bankOfAkka ! Withdraw(price)

    case ChaChing =>
      self ! Shop

    case NSF =>
      val futureUpdatedLimit: Future[Int] = (bankOfAkka ? Balance).mapTo[Int]
      futureUpdatedLimit.map { updatedLimit => limit = updatedLimit }
      self ! Shop
  }

  def calcPrice(): Int =
    if (limit == 1) 1
    else Random.nextInt(limit - 1) + 1
}

object Shopper {
  object Shop
}


