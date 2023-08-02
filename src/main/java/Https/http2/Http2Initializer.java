package Https.http2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http2.*;
import io.netty.handler.logging.LogLevel;

public class Http2Initializer extends ChannelInitializer {

    @Override
    protected void initChannel(Channel ch) throws Exception {

        Http2FrameCodecBuilder http2CodecBuilder = Http2FrameCodecBuilder.forServer(); //server용 codec builder

        Http2FrameCodec http2Codec = http2CodecBuilder
                .frameLogger(new Http2FrameLogger(LogLevel.INFO))
                .initialSettings(Http2Settings.defaultSettings())
                .build();
        //builder를 통해서 ack 조절, http setting 하여 설정 가능

        Http2MultiplexHandler handler = new Http2MultiplexHandler(new CustomHttp2Handler());
        //multiflexing 기능하에 동작해야한다. custom hadnler를 multiflexing 핸들러에 탑재한다/
        ch.pipeline().addLast(http2Codec);

    }
}