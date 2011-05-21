package tonedef

import unfiltered.request._
import unfiltered.response._
import unfiltered.netty.ReceivedMessage

import org.jboss.netty.handler.codec.http.{
  HttpHeaders, DefaultHttpChunk, DefaultHttpChunkTrailer}
import org.jboss.netty.channel.group.{DefaultChannelGroup, ChannelGroupFuture, ChannelGroup}
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel.{ChannelFuture, ChannelFutureListener}
import org.clapper.avsl.Logger

class ToneChannel(name: String) {
  val logger = Logger(classOf[ToneChannel])
  val group = new DefaultChannelGroup
  def shutdown() {
    logger.info("Shutting down %s...".format(name))
    group.write(new DefaultHttpChunkTrailer).await()
  }
  def add(message: ReceivedMessage) {
    val initial = message.defaultResponse(Channels.ChunkedJson)
    val ch = message.event.getChannel
    ch.write(initial).addListener { () =>
      group.add(ch)
    }
  }
  def send(message: ReceivedMessage) {
    group.write(new DefaultHttpChunk(
      ChannelBuffers.copiedBuffer(
        message.request.getContent,
        ChannelBuffers.copiedBuffer("\n".getBytes("utf-8"))
      )
    ))
    message.respond(Ok)
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
