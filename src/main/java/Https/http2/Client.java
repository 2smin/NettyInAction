package Https.http2;

import BootStrap.ClientBootStrapManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static io.netty.handler.codec.http2.HttpConversionUtil.ExtensionHeaderNames.STREAM_ID;

public class Client {

    public static void main(String[] args) {
        try {
            Channel channel = initClient();

//            테스트 request
            Thread.sleep(3000);

            //Http 1.x message 전송 시 HttpToHttp2ConnectionHandler 에서  http 2.0으로 변환 및 전송
            FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/helloTmax");
            request.headers().set("test-header","aaabbb");
            request.headers().set(HttpHeaderNames.HOST, UUID.randomUUID().toString().substring(0,6));
            request.content().writeBytes("hello world from client".getBytes(StandardCharsets.UTF_8));

            ChannelFuture future = channel.writeAndFlush(request);
            System.out.println("send test httpRequest to server");

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
