package tonedef

import unfiltered.request._
import unfiltered.response._

import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel.{ChannelFuture, ChannelFutureListener}
import org.jboss.netty.channel.group.{DefaultChannelGroup, ChannelGroupFuture, ChannelGroup}
import org.jboss.netty.handler.codec.http.{
  HttpHeaders,DefaultHttpChunk, DefaultHttpChunkTrailer}
import org.clapper.avsl.Logger

/** unfiltered plan */
object App extends unfiltered.netty.channel.Plan {
  import QParams._

  val logger = Logger(App.getClass)
  @volatile private var channels = Map.empty[String, ChannelGroup]

  def shutdown() {
    logger.info("Shutting down")
    channels.values.foreach { cg =>
      cg.write(new DefaultHttpChunkTrailer).await()
    }
  }

  def channel(name: String) = channels.getOrElse(name, {
    val cg = new DefaultChannelGroup
    this.synchronized {
      channels.getOrElse(name, {
        channels = channels + (name -> cg)
        cg
      })
    }
  })
  def intent = {
    case req @ Path(Seg("channel" :: name :: Nil)) => 
      val listeners = channel(name)
      req match {
        case GET(_) => 
          logger.debug("GET channel %s" format name)
          val initial = req.underlying.defaultResponse(ChunkedJson)
          val ch = req.underlying.event.getChannel
          ch.write(initial).addListener { () =>
            listeners.add(ch)
          }

        case POST(_) =>
          logger.debug("POST channel %s" format name)
          listeners.write(new DefaultHttpChunk(
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
    unfiltered.netty.Http(8080)
      .handler(App)
      .beforeStop {
        App.shutdown()
      }
      .run
  }
}
