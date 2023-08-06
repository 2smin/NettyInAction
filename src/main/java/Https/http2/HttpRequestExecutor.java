package Https.http2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2FrameWriter;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2FrameWriter;

public class HttpRequestExecutor extends ChannelOutboundHandlerAdapter {

    Http2FrameWriter frameWriter = new DefaultHttp2FrameWriter();

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
//            frameWriter.writeHeaders(ctx,0,headers,1,false,null);
            ctx.writeAndFlush(headers);
            System.out.println("headers: " + headers);
        }else{
            DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
            request.headers().add(HttpHeaderNames.HOST, "localhost");
            request.headers().add("x-client-id","qe12g-31");

            System.out.println("HttpRequestExecutor write default message to Server");
            ctx.writeAndFlush(request);

        }


    }
}
