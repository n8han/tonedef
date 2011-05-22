package tonedef

import unfiltered.request._
import unfiltered.response._

import org.clapper.avsl.Logger

/** unfiltered plan */
object Chunked extends unfiltered.netty.channel.Plan {
  val logger = Logger(this.getClass)

  def intent = {
    case req @ Path(Seg("channel" :: name :: Nil)) => 
      val channel = Channels(name)
      req match {
        case GET(_) => 
          logger.debug("GET channel %s" format name)
          channel.get(req.underlying)

        case POST(_) =>
          logger.debug("POST channel %s" format name)
          channel.post(req.underlying)
        case _ => Pass
      }
  }
}
object WebSockets {
  import unfiltered.websockets._
  import org.jboss.netty.channel.{ChannelHandlerContext, MessageEvent}
  val logger = Logger(this.getClass)

  object Handler extends unfiltered.websockets.WebSocketHandler(
    "/", {
      case Message(socket, Text(text)) if text.endsWith(":open") =>
        val Array(name, _) = text.split(":", 2)
        logger.debug("OPEN channel %s" format name)
        Channels(name).open(socket)
      case Message(socket, Text(text)) =>
        val Array(name, payload) = text.split(":", 2)
        logger.debug("TEXT channel %s" format name)    
        Channels(name).send(payload)
    }, { _.sendUpstream(_) }
  )
}

/** embedded server */
object Server {
  val logger = Logger(Server.getClass)
  
  def main(args: Array[String]) {
    logger.info("starting tonedef server on port %s" format 8080)
    unfiltered.netty.Http(8080)
      .handler(WebSockets.Handler)
      .handler(Chunked)
      .beforeStop {
        Channels.shutdown()
      }
      .run
  }
}
