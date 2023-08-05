package Https.http2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http2.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class Http2ClientInitializer extends ChannelInitializer {

    @Override
    protected void initChannel(Channel ch) throws Exception {

        SslContext sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        Http2FrameCodecBuilder http2CodecBuilder = Http2FrameCodecBuilder.forClient(); //server용 codec builder

        Http2FrameCodec http2Codec = http2CodecBuilder
                .frameLogger(new Http2FrameLogger(LogLevel.INFO))
                .initialSettings(Http2Settings.defaultSettings())
                .build();
        //builder를 통해서 ack 조절, http setting 하여 설정 가능

        Http2MultiplexHandler handler = new Http2MultiplexHandler(new CustomHttp2ClientHandler());
        //multiflexing 기능하에 동작해야한다. custom hadnler를 multiflexing 핸들러에 탑재한다/

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        pipeline.addLast(http2Codec);

        pipeline.addLast(new HttpClientCodec())
                .addLast(new HttpObjectAggregator(65536))
                .addLast(new HttpContentDecompressor());
        ch.pipeline().addLast(handler);

    }
}
