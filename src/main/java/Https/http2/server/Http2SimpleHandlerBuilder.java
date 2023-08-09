package Https.http2.server;

import io.netty.handler.codec.http2.AbstractHttp2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Settings;

public class Http2SimpleHandlerBuilder extends AbstractHttp2ConnectionHandlerBuilder<Http2SimpleHandler, Http2SimpleHandlerBuilder> {


    @Override
    protected Http2SimpleHandler build() {
        return super.build();
    }

    @Override
    protected Http2SimpleHandler build(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings) throws Exception {

        Http2SimpleHandler handler = new Http2SimpleHandler(decoder, encoder, initialSettings);
        frameListener(handler);
        return handler;
    }
}
