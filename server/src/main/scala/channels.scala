package tonedef

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty.ReceivedMessage
import unfiltered.websockets.WebSocket

import org.jboss.netty.util.CharsetUtil.UTF_8
import org.jboss.netty.handler.codec.http.{
  HttpHeaders, DefaultHttpChunk, DefaultHttpChunkTrailer}
import org.jboss.netty.handler.codec.http.websocket.DefaultWebSocketFrame
import org.jboss.netty.channel.group.{DefaultChannelGroup, ChannelGroupFuture, ChannelGroup}
import org.jboss.netty.buffer.{ChannelBuffer, ChannelBuffers}
import org.jboss.netty.channel.{ChannelFuture, ChannelFutureListener}
import org.clapper.avsl.Logger

class ToneChannel(name: String) {
  val logger = Logger(classOf[ToneChannel])
  val chunks = new DefaultChannelGroup
  val sockets = new DefaultChannelGroup
  def shutdown() {
    logger.info("Shutting down %s...".format(name))
    chunks.write(new DefaultHttpChunkTrailer).await()
  }
  def get(message: ReceivedMessage) {
    val initial = message.defaultResponse(Channels.ChunkedJson)
    val ch = message.event.getChannel
    ch.write(initial).addListener { () =>
      chunks.add(ch)
    }
  }
  def open(socket: WebSocket) {
    val ch = socket.channel
    sockets.add(ch)
  }
  def post(message: ReceivedMessage) {
    send(message.request.getContent.toString(UTF_8))
    message.respond(Ok)
  }
  def send(payload: String) {
    chunks.write(new DefaultHttpChunk(
      ChannelBuffers.copiedBuffer(
        payload + "\n", UTF_8
      )
    ))
    sockets.write(new DefaultWebSocketFrame(payload))
  }
  implicit def block2listener[T](block: () => T): ChannelFutureListener =
    new ChannelFutureListener {
      def operationComplete(future: ChannelFuture) { block() }
    }
}
object Channels {
  @volatile private var channels = Map.empty[String, ToneChannel]

  def apply(name: String) = channels.getOrElse(name, {
    val cg = new ToneChannel(name)
    this.synchronized {
      channels.getOrElse(name, {
        channels = channels + (name -> cg)
        cg
      })
    }
  })
  def shutdown() {
    channels.values.foreach { _.shutdown }
  }
  val ChunkedJson =
    unfiltered.response.Connection(HttpHeaders.Values.CLOSE) ~>
    TransferEncoding(HttpHeaders.Values.CHUNKED) ~>
    JsonContent
}
