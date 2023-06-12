package FileHandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

public class HttpChunkInboundHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        if(msg instanceof HttpRequest){
            System.out.println("http request");
            System.out.println(((HttpRequest) msg).toString());
        }

        if(msg instanceof HttpContent){
            System.out.println("http content");
            System.out.println(((HttpContent) msg).toString());

            if(msg instanceof LastHttpContent){
                System.out.println("last http content");
                System.out.println(((LastHttpContent) msg).toString());
            }
        }

        System.out.println("http chunk inbound handler");
    }
}
