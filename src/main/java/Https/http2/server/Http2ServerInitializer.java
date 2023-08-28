package Https.http2.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.*;
import javax.net.ssl.SSLException;
import java.io.File;

public class Http2ServerInitializer extends ChannelInitializer {

    private SslContext getCertificate() throws SSLException {
        SslContext sslContext = null;

        try{
            File certChainFile = new File("src/main/resources/ssl/netty.crt");
            File keyFile = new File("src/main/resources/ssl/prtKey_pkcs8.pem");

            sslContext = SslContextBuilder.forServer(certChainFile, keyFile,"1234")
                    .protocols(SslProtocols.TLS_v1_3)
                    .applicationProtocolConfig(new ApplicationProtocolConfig(
                            ApplicationProtocolConfig.Protocol.ALPN,
                            ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                            ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                            ApplicationProtocolNames.HTTP_2))
                    .build();


        }catch (SSLException e){
            e.printStackTrace();
        }
        return sslContext;
    }

    private SslContext sslContext;

    public Http2ServerInitializer() {
        try {
            sslContext = getCertificate();
        } catch (SSLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {

        Http2Connection connection = new DefaultHttp2Connection(true);
        Http2ConnectionHandler connectionHandler = new HttpToHttp2ConnectionHandlerBuilder()
                .httpScheme(HttpScheme.HTTP)
                .frameLogger(new Http2FrameLogger(LogLevel.INFO))
                .connection(connection) //개별 connection 관리용 객체 설정
                .frameListener(new DelegatingDecompressorFrameListener( //frame 압축해제, 개별 frame 타입 마다 다양한 동작 가능
                        connection,
                        //http2.0 을 다시 http1.1로 변환, application 레벨에서 쉽게 사용하도록 한다
                        new Http2SimpleHandlerBuilder().build()))
                .build();

        if(sslContext != null){
            System.out.println("SslContext has been set");
            ch.pipeline().addLast(sslContext.newHandler(ch.alloc()), new HttpServerUpgradeNegotiator(ApplicationProtocolNames.HTTP_2, connectionHandler));
        }else{
            System.out.println("SslContext has not been set");
            ChannelPipeline p = ch.pipeline();
            HttpServerCodec sourceCodec = new HttpServerCodec();

            //upgrade 요청을 처리하는 핸들러
            HttpServerUpgradeHandler upgradeHandler = new HttpServerUpgradeHandler(sourceCodec, new HttpServerUpgradeHandler.UpgradeCodecFactory() {
                @Override
                public HttpServerUpgradeHandler.UpgradeCodec newUpgradeCodec(CharSequence protocol) {
                    if(protocol.equals(Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME)){
                        System.out.println("Server Configured to HTTP/2");
                        return new Http2ServerUpgradeCodec(new Http2SimpleHandlerBuilder().build());
                    }else{
                        System.out.println("Server Configured to : " + protocol);
                        return null;
                    }
                }}
            );




            CleartextHttp2ServerUpgradeHandler h2cUpgradeHandler
                    = new CleartextHttp2ServerUpgradeHandler(sourceCodec, upgradeHandler, new Http2SimpleHandlerBuilder().build());

            //connection handler 사용할지, 자체 frame listener 사용할지 결정
            p.addLast(h2cUpgradeHandler);
//            p.addLast(connectionHandler);

            //Http 2 Frame으로 오는 경우는 h2cUpgradeHandler에서 처리하고, upgrade 실패인 경우 아래의 핸들러에서 HttpMessage 로 read되어 Http1SimpleHandler에서 처리한다.
            p.addLast(new SimpleChannelInboundHandler<HttpMessage>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) throws Exception {
                    System.out.println("On using HTTP/1.1");
                    System.out.println("msg = " + msg);

                    ctx.pipeline().addAfter(ctx.name(), null, new Http1SimpleHandler());
                    ctx.pipeline().replace(this, null, new HttpObjectAggregator(65536));

                }
            });
        }



    }
}