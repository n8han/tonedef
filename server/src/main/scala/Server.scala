package tonedef

import unfiltered.request._
import unfiltered.response._

import org.clapper.avsl.Logger

/** unfiltered plan */
object Plan extends unfiltered.netty.channel.Plan {
  val logger = Logger(Plan.getClass)

  def intent = {
    case req @ Path(Seg("channel" :: name :: Nil)) => 
      val channel = Channels(name)
      req match {
        case GET(_) => 
          logger.debug("GET channel %s" format name)
          channel.add(req.underlying)

        case POST(_) =>
          logger.debug("POST channel %s" format name)
          channel.send(req.underlying)
        case _ => Pass
      }
  }
}

/** embedded server */
object Server {
  val logger = Logger(Server.getClass)
  
  def main(args: Array[String]) {
    logger.info("starting tonedef server on port %s" format 8080)
    unfiltered.netty.Http(8080)
      .handler(Plan)
      .beforeStop {
        Channels.shutdown()
      }
      .run
  }
}
