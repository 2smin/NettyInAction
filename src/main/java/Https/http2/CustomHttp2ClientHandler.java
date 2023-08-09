package Https.http2;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http2.DefaultHttp2FrameWriter;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2Headers;

public class CustomHttp2ClientHandler extends ChannelDuplexHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("CustomHttp2ClientHandler received message from Server");

        if(msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            HttpHeaders headers = request.headers();
            System.out.println("headers: " + headers);
        }

        else {
            System.out.println("msg: " + msg);
        }
    }



    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

            System.out.println("CustomHttp2ClientHandler write message to Server");

            Http2Headers headers = new DefaultHttp2Headers().method("GET").authority("localhost").path("/hey");


            DefaultHttp2FrameWriter frameWriter = new DefaultHttp2FrameWriter();
            frameWriter.writeHeaders(ctx, 55, headers, 0, true, promise);

            ctx.flush();
            System.out.println("CustomHttp2ClientHandler write message to Server done");
    }
}
