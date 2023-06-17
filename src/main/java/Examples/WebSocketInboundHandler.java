package Examples;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class WebSocketInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("incomming websocket message : " + msg);

        //send message to all clients
        MessageServer.channelGroup.writeAndFlush(msg);
    }
}
