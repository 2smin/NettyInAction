package Https.http2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http2.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLException;
import java.io.File;

public class Http2ServerInitializer extends ChannelInitializer {

    private SslContext getCertificate() throws SSLException {
        SslContext sslContext = null;

        try{
            File certChainFile = new File("src/main/resources/ssl/netty.crt");
            File keyFile = new File("src/main/resources/ssl/prtKey_pkcs8.pem");

            sslContext = SslContextBuilder.forServer(certChainFile, keyFile,"1234").build();

        }catch (SSLException e){
            e.printStackTrace();
        }

        return sslContext;
    }
    @Override
    protected void initChannel(Channel ch) throws Exception {


        Http2FrameCodecBuilder http2CodecBuilder = Http2FrameCodecBuilder.forServer(); //server용 codec builder

        Http2FrameCodec http2Codec = http2CodecBuilder
                .frameLogger(new Http2FrameLogger(LogLevel.INFO))
                .initialSettings(Http2Settings.defaultSettings())
                .build();
        //builder를 통해서 ack 조절, http setting 하여 설정 가능

        Http2MultiplexHandler handler = new Http2MultiplexHandler(new CustomHttp2ClientHandler());
        //multiflexing 기능하에 동작해야한다. custom hadnler를 multiflexing 핸들러에 탑재한다/

        SslContext sslContext = getCertificate();
        if(sslContext != null) {
            ch.pipeline().addLast(sslContext.newHandler(ch.alloc()));
            System.out.println("ssl context added.");
        }
        ch.pipeline().addLast(http2Codec);
        ch.pipeline().addLast(handler);

    }
}