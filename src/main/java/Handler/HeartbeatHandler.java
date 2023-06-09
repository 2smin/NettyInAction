package Handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

import java.nio.charset.Charset;

public class HeartbeatHandler extends ChannelInboundHandlerAdapter{
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

