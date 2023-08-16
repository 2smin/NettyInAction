package Https.http2;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;

public class Http2ClientInitializer extends ChannelInitializer {

    SslContext sslCtx;
    SslProvider provider = OpenSsl.isAlpnSupported() ? SslProvider.OPENSSL : SslProvider.JDK;

    private HttpToHttp2ConnectionHandler  connectionHandler;

    private void createSslCtx() throws SSLException {
        sslCtx = SslContextBuilder.forClient().sslProvider(provider)
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .applicationProtocolConfig(new ApplicationProtocolConfig(
                        ApplicationProtocolConfig.Protocol.ALPN,
                        ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                        ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                        ApplicationProtocolNames.HTTP_2))
                .build();
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {

        // 개별 연결마다 connection 관리 객체를 만든다
        Http2Connection connection = new DefaultHttp2Connection(false);

        connectionHandler = new HttpToHttp2ConnectionHandlerBuilder()
                .httpScheme(HttpScheme.HTTP)
                .frameLogger(new Http2FrameLogger(LogLevel.INFO))
                .connection(connection) //개별 connection 관리용 객체 설정
                .frameListener(new DelegatingDecompressorFrameListener( //frame 압축해제, 개별 frame 타입 마다 다양한 동작 가능
                        connection,
                        //http2.0 을 다시 http1.1로 변환 application 레벨에서 쉽게 사용하도록 한다
        new InboundHttp2ToHttpAdapterBuilder(connection)
                .maxContentLength(100000)
                .propagateSettings(true)
                .build()))
                .build();

        /*
            SslHandler의 handshake가 완료되면 ApplicationProtocolNegotiationHandler를 통해
            설정된 protocol에 따라 ChannelPipeline을 재구성한다.
         */

        createSslCtx();
        if(sslCtx != null){
            h2Configuration(ch);
        }else{
            h2cConfiguration(ch);
        }
    }

    private void h2Configuration(Channel ch){

        Http2FrameCodecBuilder http2CodecBuilder = Http2FrameCodecBuilder.forClient(); //server용 codec builder

        //unstable api
//        Http2FrameCodec http2Codec = http2CodecBuilder
//                .frameLogger(new Http2FrameLogger(LogLevel.TRACE))
//                .initialSettings(Http2Settings.defaultSettings())
//                .build();
        //builder를 통해서 ack 조절, http setting 하여 설정 가능

        //multiflexing 기능하에 동작해야한다. custom hadnler를 multiflexing 핸들러에 탑재한다/
        Http2MultiplexHandler handler = new Http2MultiplexHandler(new CustomHttp2ClientHandler());

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        pipeline.addLast(new ApplicationProtocolNegotiationHandler(/*fallback protocol*/ApplicationProtocolNames.HTTP_1_1) {
            @Override
            protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {

                //HTTP/2가 선택되면 Http2FrameCodec을 ChannelPipeline에 추가한다.
                if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                    System.out.println("HTTP/2 handler configured");
                    ChannelPipeline pipeline = ctx.pipeline();
                    pipeline.addLast(connectionHandler);
                    pipeline.addLast(new HttpRequestExecutor());
                    return;
                    //HTTP/1.1이 선택되면 기본 Http 핸들러만 추가한다.
                }else if (ApplicationProtocolNames.HTTP_1_1.equals(protocol)) {
                    System.out.println("HTTP/1.1 handler configured");
                    ChannelPipeline pipeline = ctx.pipeline();
                    pipeline.addLast(new HttpClientCodec())
                            .addLast(new HttpObjectAggregator(65536))
                            .addLast(new HttpContentDecompressor());
//                    pipeline.addLast(handler);
                    pipeline.addLast(new HttpRequestExecutor());
                    return;
                }else{
                    System.out.println("unknown protocol: " + protocol);
                }
                ctx.close();
                throw new IllegalStateException("unknown protocol: " + protocol);
            }
        });

    }

    private void h2cConfiguration(Channel ch){
        ChannelPipeline pipeline = ch.pipeline();

        HttpClientCodec sourceCodec = new HttpClientCodec();
        Http2ClientUpgradeCodec upgradeCodec = new Http2ClientUpgradeCodec(connectionHandler);
        HttpClientUpgradeHandler upgradeHandler = new HttpClientUpgradeHandler(sourceCodec, upgradeCodec, 65536);

        pipeline.addLast(sourceCodec, upgradeHandler, new Http2InitializeRequestHandler());

    }

    public class Http2InitializeRequestHandler extends ChannelInboundHandlerAdapter {


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

            //ssl을 제외할 경우 여기서 upgrade Request를 날린다.
            System.out.println("http2 init request channel active");
            DefaultFullHttpRequest upgradeRequest =
                    new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/", Unpooled.EMPTY_BUFFER);
            upgradeRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE)
                    .set(HttpHeaderNames.UPGRADE, HttpVersion.valueOf("HTTP/2.0"));

            InetSocketAddress remote = (InetSocketAddress) ctx.channel().remoteAddress();
            String hostString = remote.getHostString();
            if (hostString == null) {
                hostString = remote.getAddress().getHostAddress();
            }

            upgradeRequest.headers().set(HttpHeaderNames.HOST, hostString + ':' + remote.getPort());

            ctx.writeAndFlush(upgradeRequest);
            ctx.fireChannelActive();

            final Http2FrameCodec http2FrameCodec = Http2FrameCodecBuilder.forClient().build();

            ctx.pipeline().addLast(new HttpRequestExecutor());
            ctx.pipeline().remove(this);

        }
    }
}
