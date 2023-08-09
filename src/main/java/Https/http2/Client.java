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

import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http2.HttpConversionUtil.ExtensionHeaderNames.STREAM_ID;

public class Client {

    public static void main(String[] args) {
        try {
            Channel channel = initClient();

            //테스트 request
            FullHttpRequest upgradeRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
            upgradeRequest.headers().set(HttpHeaderNames.HOST, "localhost");
            upgradeRequest.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE);
            upgradeRequest.headers().set(HttpHeaderNames.UPGRADE, "h2");
            channel.writeAndFlush(upgradeRequest);


            System.out.println("request sent");

            Thread.sleep(3000);

            //테스트 request
            channel.writeAndFlush("hey".getBytes(StandardCharsets.UTF_8));


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
