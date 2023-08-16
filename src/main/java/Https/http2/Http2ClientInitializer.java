package Https.http2;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
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
                        //http2.0 을 다시 http1.1로 변환, application 레벨에서 쉽게 사용하도록 한다
        new InboundHttp2ToHttpAdapterBuilder(connection)
                .maxContentLength(100000)
                .propagateSettings(true)
                .build()))
                .build();

        createSslCtx();
        if(sslCtx != null){
            h2Configuration(ch);
        }else{
            h2cConfiguration(ch);
        }
    }

    private void h2Configuration(Channel ch){

        ChannelPipeline pipeline = ch.pipeline();
        //sslhandler 추가
        pipeline.addLast(sslCtx.newHandler(ch.alloc()));

        // Server에서 보낸 ALPN을 확인하고 설정하는 negotiation handler
        pipeline.addLast(new ApplicationProtocolNegotiationHandler(/*fallback protocol*/ApplicationProtocolNames.HTTP_1_1) {
            @Override
            protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {

                //server가 h2를 반환하면 Http2Connection handler와 기본 Http 핸들러를 ChannelPipeline에 추가한다.
                if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                    System.out.println("HTTP/2 handler configured");
                    ChannelPipeline pipeline = ctx.pipeline();
                    pipeline.addLast(connectionHandler); //connection handler는 http 2.0 frame을 사용하기 쉽게 http 1.x 객체로 변환한다.
                    pipeline.addLast(new DefaultHttpHandler()); // 변환된 http 1.x 객체를 사용 가능
                    return;
                //server가 h2를 반환하지 않으면 http1.x를 사용하게 되며, 기본 Http 핸들러와 server 코덱을 추가한다.
                }else if (ApplicationProtocolNames.HTTP_1_1.equals(protocol)) {
                    System.out.println("HTTP/1.1 handler configured");
                    ChannelPipeline pipeline = ctx.pipeline();
                    pipeline.addLast(new HttpClientCodec())
                            .addLast(new HttpObjectAggregator(65536))
                            .addLast(new HttpContentDecompressor());
//                    pipeline.addLast(handler);
                    pipeline.addLast(new DefaultHttpHandler());
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

        //h2c는 ALPN이 없으므로 직접 upgrade handler를 설정해준다.
        HttpClientCodec sourceCodec = new HttpClientCodec();

        //Http 2로 upgrade 하는 codec
        Http2ClientUpgradeCodec upgradeCodec = new Http2ClientUpgradeCodec(connectionHandler);

        /*
            http를 upgrade 하는 handler, http 를 upgrade하도록 하며,
            param으로 주어진 upgradeCodec을 보고 어떤 protocol로 upgrade할지 header를 넣는다.
            upgrade가 가능하면 sourceCodec을 pipeline에서 지우고 upgradeCodec이 가진 connection handler를 pipeline에 추가한다.
         */
        HttpClientUpgradeHandler upgradeHandler = new HttpClientUpgradeHandler(
                /* pipeline에 setting할 동일 srcCodec 객체를 넣어야 함 */ sourceCodec,
                upgradeCodec, 65536);

        pipeline.addLast(
                /*최초 요청에 대해 처리하는 httpCodec */ sourceCodec,
                /* upgrade 헤더 추가 및 upgrade를 수행하는 handler */ upgradeHandler,
                /* 최초 요청을 보낼 handler */ new Http2InitializeRequestHandler());

    }

    public class Http2InitializeRequestHandler extends ChannelInboundHandlerAdapter {


        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

            //ssl을 제외할 경우 여기서 upgrade Request를 날린다.
            System.out.println("http2 init request channel active");
            DefaultFullHttpRequest upgradeRequest =
                    new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/", Unpooled.EMPTY_BUFFER);

            //upgrade handler에서 최초 request에 대해서 upgrade header를 자동으로 달아준다.
//            upgradeRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE)
//                    .set(HttpHeaderNames.UPGRADE, HttpVersion.valueOf("HTTP/2.0"));

            InetSocketAddress remote = (InetSocketAddress) ctx.channel().remoteAddress();
            String hostString = remote.getHostString();
            if (hostString == null) {
                hostString = remote.getAddress().getHostAddress();
            }

            upgradeRequest.headers().set(HttpHeaderNames.HOST, hostString + ':' + remote.getPort());

            ctx.writeAndFlush(upgradeRequest);

            ctx.pipeline().addLast(new DefaultHttpHandler());
            ctx.pipeline().remove(this);

        }
    }
}
