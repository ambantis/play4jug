package futuredemo

import futuredemo.EulerProblem012._

import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent._
import ExecutionContext.Implicits.global

object Boot extends App {

  val fiveHundred = 500
  implicit val timeout = Timeout(5 seconds)

  println("this is before the expensive computation")

  val futureResult: Future[(Int, Double)] = future {
    getNaturalNumbersWith(divisors = fiveHundred)
  }

  futureResult.map { x =>
    println(s"first triangle # with $fiveHundred divisors = ${x._1} with a time of ${x._2} secs")
  }

  println("this is after the expensive computation")
}
