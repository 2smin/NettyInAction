package Https.http2;

import BootStrap.ClientBootStrapManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;

import static io.netty.handler.codec.http2.HttpConversionUtil.ExtensionHeaderNames.STREAM_ID;

public class Client {

    public static void main(String[] args) {
        try {
            Channel channel = initClient();

            //테스트 request
            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
            request.headers().add(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), 3); //stream id
            request.headers().add(HttpHeaderNames.HOST, "localhost");
            request.headers().add("test-header","qe12g-31");

            channel.writeAndFlush(request);

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public static Channel initClient() throws Exception{

        Bootstrap client = new Bootstrap();
        client.group(new NioEventLoopGroup());
        client.channel(NioSocketChannel.class);
        client.handler(new Http2ClientInitializer());
        client.option(ChannelOption.SO_KEEPALIVE, true);
        client.remoteAddress("localhost", 33335);
        Channel channel = client.connect().syncUninterruptibly().channel();

        return channel;
    }
}
