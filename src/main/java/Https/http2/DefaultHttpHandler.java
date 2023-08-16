package Https.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http2.*;

import java.nio.charset.StandardCharsets;

public class DefaultHttpHandler extends ChannelDuplexHandler {

    Http2FrameWriter frameWriter = new DefaultHttp2FrameWriter();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("client receive response");

        System.out.println("type : " + msg.getClass());

        if(msg instanceof DefaultFullHttpResponse){
            DefaultFullHttpResponse response = (DefaultFullHttpResponse)msg;

            ByteBuf content = response.content();
            System.out.println("by FullHttpResponse : " + content.readCharSequence(content.readableBytes(), StandardCharsets.UTF_8));
        }else if (msg instanceof Http2Settings){
            System.out.println("handshake setting received");
        }

    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {


        if(msg instanceof DefaultHttp2DataFrame){
            System.out.println("HttpRequestExecutor write http2 frame message to Server");
            DefaultHttp2DataFrame dataFrame = (DefaultHttp2DataFrame) msg;
            frameWriter.writeData(ctx,0,dataFrame.content(),1,true, null);
            System.out.println("dataFrame: " + dataFrame);
        }


        if(msg instanceof DefaultHttp2Headers){
            System.out.println("HttpRequestExecutor write http2 header message to Server");
            DefaultHttp2Headers headers = (DefaultHttp2Headers) msg;
            ctx.writeAndFlush(headers);
            System.out.println("headers: " + headers);
        }else if(msg instanceof DefaultHttp2HeadersFrame){
            System.out.println("DefaultHttp2HeadersFrame received ");
            ctx.writeAndFlush(msg);

        }else if(msg instanceof FullHttpRequest){
            System.out.println("FullHttpRequest received ");
            ctx.writeAndFlush(msg);
        }


    }
}
