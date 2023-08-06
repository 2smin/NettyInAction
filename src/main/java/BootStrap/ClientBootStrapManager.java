package BootStrap;

import Codec.Decoder.ClientInboundHandler;
import Codec.Decoder.ServerInboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http2.DefaultHttp2DataFrame;
import io.netty.handler.codec.http2.DefaultHttp2FrameWriter;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2FrameWriter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static BootStrap.ChannelAttr.CLIENT_PORT;

public class ClientBootStrapManager {


    private Bootstrap client;
    public int channelId = 0;
    private ClientBootStrapManager() {}

    public static class holder {
        public static ClientBootStrapManager INSTANCE = new ClientBootStrapManager();

    }
    public void runClientBootStrap(int clientPort) throws UnknownHostException, InterruptedException {
        client = new Bootstrap();
        client.group(new NioEventLoopGroup(1));
//        client.localAddress(clientPort);

    }

    public void addPipeLine(ChannelInitializer  channelInitializer){
        client.handler(channelInitializer);
        client.channel(NioSocketChannel.class);
    }

    public ChannelFuture connectToServer(int serverPort) {
        String localHost = null;
        try {
            localHost = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        System.out.println(localHost);

        ChannelFuture future = null;
        try {
            future = client.connect(new InetSocketAddress("localhost",serverPort)).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    System.out.println("connect complete");
                }else{
                    System.out.println("connect failed");
                    future.cause().printStackTrace();
                }

            }
        });
        return future;
    }
}
