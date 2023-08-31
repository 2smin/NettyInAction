package Https.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.Headers;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;

import java.io.InvalidClassException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class DefaultHttpHandler extends ChannelDuplexHandler {

    private HttpMessageMappingContainer mappingContainer = new HttpMessageMappingContainer();

    /**
     * Client should set streamId ad odd Num
     */
    private int streamId = 15;
    private String streamId(){
        return String.valueOf(streamId+=2);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("HttpResponse received");
        if(msg instanceof DefaultFullHttpResponse){
            DefaultFullHttpResponse httpResponse = (DefaultFullHttpResponse)msg;

            String streamId = null;
            HttpHeaders header = httpResponse.headers();
            for(CharSequence name : header.names()){
                System.out.println("header name : " + name + " header value : " + header.get(name));
                if(name.toString().contains("stream") && name.toString().contains("id")){
                    streamId = header.get(name);
                    System.out.println("streamId in response header :" + streamId);
                }

            }
            System.out.println("response content : " + httpResponse.content().toString(StandardCharsets.UTF_8));

            String host = mappingContainer.checkIfExist(streamId);
            System.out.println("http response send to : " + host);
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
            httpRequest.headers().add(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), streamId);
        }else{
            streamId = httpRequest.headers().get(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());
            if(streamId == null){
                streamId = streamId();
                httpRequest.headers().add(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), streamId);
            }
        }

        String host = httpRequest.headers().get(HttpHeaderNames.HOST);
        System.out.println("request streamId : " + streamId + " request host : " + host);
        //TODO : host channel 찾아서 writeAndFlush (global static channel map)
        mappingContainer.add(streamId, host);


        ctx.write(httpRequest);
    }
}
