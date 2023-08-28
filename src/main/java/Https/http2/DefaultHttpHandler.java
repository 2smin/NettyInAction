package Https.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;

import java.io.InvalidClassException;
import java.nio.charset.StandardCharsets;

public class DefaultHttpHandler extends ChannelDuplexHandler {

    private HttpMessageMappingContainer mappingContainer = new HttpMessageMappingContainer();
    private int streamId = 6;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("HttpResponse received");
        if(msg instanceof DefaultFullHttpResponse){
            DefaultFullHttpResponse httpResponse = (DefaultFullHttpResponse)msg;


            System.out.println("response content : " + httpResponse.content().toString(StandardCharsets.UTF_8));

            String streamId = null;

            //extract streamId from respons header
            if(!httpResponse.headers().contains(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text())){
                streamId = streamId();
            }else{
                streamId = httpResponse.headers().get(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());
                if(streamId == null) streamId = streamId();
            }

            System.out.println("response streamId : " + streamId);
            String host = mappingContainer.checkIfExist(streamId);

            //TODO : host channel 찾아서 writeAndFlush (global static channel map)

        }else if (msg instanceof Http2Settings){
            System.out.println("handshake setting received");
        }

    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(!(msg instanceof DefaultHttpRequest)) throw new InvalidClassException("msg is not FullHttpRequest");
        DefaultHttpRequest httpRequest = (DefaultHttpRequest) msg;

        String streamId = null;

        //check if streamId is already exist (if user set streamId), or create new streamId
        if(!httpRequest.headers().contains(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text())){
            streamId = streamId();
        }else{
            streamId = httpRequest.headers().get(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());
            if(streamId == null) streamId = streamId();
        }

        System.out.println("request streamId : " + streamId);
        mappingContainer.add(streamId, httpRequest.headers().get(HttpHeaderNames.HOST));

        //TODO : host channel 찾아서 writeAndFlush (global static channel map)

        ctx.write(httpRequest);
    }

    private String streamId(){
        System.out.println("increase streamId : " + streamId);
        return String.valueOf(streamId++);
    }
}
