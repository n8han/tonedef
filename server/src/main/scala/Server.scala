package tones

import unfiltered.request._
import unfiltered.response._

import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel.{ChannelFuture, ChannelFutureListener}
import org.jboss.netty.channel.group.{DefaultChannelGroup, ChannelGroupFuture}
import org.jboss.netty.handler.codec.http.{HttpHeaders,DefaultHttpChunk}
import org.clapper.avsl.Logger

/** unfiltered plan */
class App extends unfiltered.netty.channel.Plan {
  import QParams._

  val logger = Logger(classOf[App])
  private val clients = new DefaultChannelGroup

  def intent = {
    case req @ Path(Seg("channel" :: channel :: Nil)) => req match {
      case GET(_) => 
        logger.debug("GET channel %s" format channel)
        val initial = req.underlying.defaultResponse(ChunkedJson)
        val ch = req.underlying.event.getChannel
        ch.write(initial).addListener { () =>
          clients.add(ch)
        }

      case POST(_) =>
        logger.debug("POST channel %s" format channel)
        clients.write(new DefaultHttpChunk(
          ChannelBuffers.copiedBuffer(
            req.underlying.request.getContent,
            ChannelBuffers.copiedBuffer("\n".getBytes("utf-8"))
          )
        ))
        req.underlying.respond(Ok)
      case _ => Pass
    }
  }
  val ChunkedJson =
    unfiltered.response.Connection(HttpHeaders.Values.CLOSE) ~>
    TransferEncoding(HttpHeaders.Values.CHUNKED) ~>
    JsonContent

  implicit def block2listener[T](block: () => T): ChannelFutureListener =
    new ChannelFutureListener {
      def operationComplete(future: ChannelFuture) { block() }
    }
}

/** embedded server */
object Server {
  val logger = Logger(Server.getClass)
  
  def main(args: Array[String]) {
    logger.info("starting unfiltered app at localhost on port %s" format 8080)
    unfiltered.netty.Http(8080).handler(new App).run
  }
}
