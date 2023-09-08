package Https.http2.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;

public class HttpServerUpgradeNegotiator extends ApplicationProtocolNegotiationHandler {

    private Http2ConnectionHandler http2ConnectionHandler;

    /**
     * Creates a new instance with the specified fallback protocol name.
     *
     * @param fallbackProtocol the name of the protocol to use when
     *                         ALPN/NPN negotiation fails or the client does not support ALPN/NPN
     */
    protected HttpServerUpgradeNegotiator(String fallbackProtocol, Http2ConnectionHandler http2ConnectionHandler) {
        super(fallbackProtocol);
        this.http2ConnectionHandler = http2ConnectionHandler;
    }

    @Override
    protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {
        if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
            System.out.println("Server Configured to HTTP/2");

            //htt2 connection handler (http2 -> http1)
            ctx.pipeline().addLast(http2ConnectionHandler);
            //http1 handler (application level에서 http1 객체를 다룰수 있도록)
            ctx.pipeline().addLast(new Http1SimpleHandler());
            return;
        }else if (ApplicationProtocolNames.HTTP_1_1.equals(protocol)) {
            System.out.println("Server Configured to HTTP/1.1");
            ctx.pipeline().addLast(new HttpServerCodec(), new HttpObjectAggregator(65536), new Http1SimpleHandler());
            return;
        }
        throw new IllegalStateException("unknown protocol: " + protocol);
    }
}
