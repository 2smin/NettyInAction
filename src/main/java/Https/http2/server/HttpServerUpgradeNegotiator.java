package Https.http2.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;

public class HttpServerUpgradeNegotiator extends ApplicationProtocolNegotiationHandler {

    /**
     * Creates a new instance with the specified fallback protocol name.
     *
     * @param fallbackProtocol the name of the protocol to use when
     *                         ALPN/NPN negotiation fails or the client does not support ALPN/NPN
     */
    protected HttpServerUpgradeNegotiator(String fallbackProtocol) {
        super(fallbackProtocol);
    }

    @Override
    protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {
        if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
            System.out.println("Server Configured to HTTP/2");
            ctx.pipeline().addLast(new Http2SimpleHandlerBuilder().build());
            return;
        }else if (ApplicationProtocolNames.HTTP_1_1.equals(protocol)) {
            System.out.println("Server Configured to HTTP/1.1");
            ctx.pipeline().addLast(new HttpServerCodec(), new HttpObjectAggregator(65536), new Http1SimpleHandler());
            return;
        }
        throw new IllegalStateException("unknown protocol: " + protocol);
    }
}
