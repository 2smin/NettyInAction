package Handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class IdleStateInitializer extends ChannelInitializer {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new IdleStateHandler(0,0,60, TimeUnit.SECONDS));
        ch.pipeline().addLast(new HeartbeatHandler());
    }

    private static final class HeartbeatHandler extends ChannelInboundHandlerAdapter {

        private static final ByteBuf HEARTBEAT =
                Unpooled.unreleasableBuffer(
                        Unpooled.copiedBuffer("HEARTBEAT", Charset.defaultCharset())
                );

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if(evt instanceof IdleStateEvent){
                ctx.writeAndFlush(HEARTBEAT.duplicate())
                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }else{
                super.userEventTriggered(ctx,evt);
            }
        }
    }
}
