package Https.http2.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static java.net.http.HttpClient.Version.HTTP_1_1;

public class Http1SimpleHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        if (HttpUtil.is100ContinueExpected(msg)) {
            ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER));

            ByteBuf content = ctx.alloc().buffer();
            content.writeBytes("HELLO WORLD FROM HTTP_1.1 HANDLER".getBytes(StandardCharsets.UTF_8));
            ByteBufUtil.writeAscii(content, " - via " + msg.protocolVersion());

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK, content);
            response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());

            ctx.write(response);
        }
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
