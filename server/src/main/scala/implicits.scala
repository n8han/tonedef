package tonedef

import org.jboss.netty.util.{Timeout,TimerTask}
import org.jboss.netty.channel.{ChannelFuture, ChannelFutureListener}

trait Implicits {
  implicit def block2listener[T](block: () => T): ChannelFutureListener =
    new ChannelFutureListener {
      def operationComplete(future: ChannelFuture) { block() }
    }
  implicit def block2tt(block: Timeout => Any) =
    new TimerTask {
      def run(timeout: Timeout) { 
        block(timeout)
      }
    }
}
