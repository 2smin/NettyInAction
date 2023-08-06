package Https.http2;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

public class CustomHttp2ClientHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("CustomHttp2ClientHandler received message from Server");

        if(msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            HttpHeaders headers = request.headers();
            System.out.println("headers: " + headers);
        }
    }



    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

            System.out.println("CustomHttp2ClientHandler write message to Server");

            if(msg instanceof FullHttpRequest) {
                FullHttpRequest request = (FullHttpRequest) msg;
                HttpHeaders headers = request.headers();
                System.out.println("headers: " + headers);
            }
            super.write(ctx, msg, promise);
    }
}
