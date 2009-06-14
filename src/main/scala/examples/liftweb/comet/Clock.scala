package examples.liftweb.comet

import _root_.java.util.Date
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.js.JsCmds._
import S._
import _root_.net.liftweb.util._
import Helpers._
import scala.actors.Actor._
import _root_.scala.xml._


class Clock extends CometActor {
  override def defaultPrefix = Full("clk")
  
  def render = bind("tock" ->
      SHtml.ajaxButton("Tock!", {
        () => ClockMaster ! Tick
        Noop
      }
    )
  )
  
  override def lowPriority : PartialFunction[Any, Unit] = {
    case Tick => {
      println("Got tick " + new Date());
      partialUpdate(SetHtml("time", Text(timeNow.toString)))
    }
  }
  
  override def localSetup = {
    ClockMaster ! SubscribeClock(this)
    super.localSetup()
  }
  
  override def localShutdown = {
    ClockMaster ! UnsubClock(this)
    super.localShutdown()
  }
}

case object Tick