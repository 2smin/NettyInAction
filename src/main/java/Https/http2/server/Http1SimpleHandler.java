package Https.http2.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.HttpConversionUtil;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

public class Http1SimpleHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        int streamId = 0;

        System.out.println("application http1 handler received message");

        HttpHeaders headers = msg.headers();

        System.out.println("httpMethod: " + msg.method());
        System.out.println("uri: " + msg.uri());
        System.out.println("protocolVersion: " + msg.protocolVersion());

        for(String name : headers.names()){
            System.out.println("header key : " + name + ", value: " + headers.get(name));
            if(name.contains("stream") && name.contains("id")){
                streamId = Integer.parseInt(headers.get(name));
            }
        }

        ByteBuf contentBuf = msg.content();
        String content = String.valueOf(contentBuf.readCharSequence(contentBuf.readableBytes(), StandardCharsets.UTF_8));
        System.out.println("content: " + content);

        sendResponse(ctx, streamId);
    }

    private void sendResponse(ChannelHandlerContext ctx, int streamId){

        ByteBuf content = Unpooled.buffer();
        content.writeBytes("HELLO WORLD FROM HTTP_1 HANDLER".getBytes(StandardCharsets.UTF_8));

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK, content);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), streamId);

        ctx.writeAndFlush(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
