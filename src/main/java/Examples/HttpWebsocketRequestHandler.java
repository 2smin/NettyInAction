package Examples;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

public class HttpWebsocketRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {

        System.out.println("incoming http request");
        HttpHeaders headers = request.headers();

        if(headers.contains(HttpHeaderNames.CONNECTION) && headers.contains(HttpHeaderNames.UPGRADE)){
            if (headers.get(HttpHeaderNames.CONNECTION).equalsIgnoreCase(HttpHeaderValues.UPGRADE.toString()) &&
                    headers.get(HttpHeaderNames.UPGRADE).equalsIgnoreCase(HttpHeaderValues.WEBSOCKET.toString())) {
                ctx.pipeline().replace(this, "WebSocketToSapDecoder", new WebSocketInboundHandler());

            }
        }

        String websocketUri = "ws://" + request.headers().get(HttpHeaderNames.HOST) + request.uri();
        System.out.println("websocket connect request from : " + websocketUri);
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                websocketUri, null, false);

        WebSocketServerHandshaker shaker = wsFactory.newHandshaker(request);
        ChannelFuture channelFuture = shaker.handshake(ctx.channel(), request);

    }
}
