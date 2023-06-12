package BootStrap;

import Codec.Decoder.ClientInboundHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class ClientBootStrapManager {

    private Bootstrap client;
    public int channelId = 0;
    private ClientBootStrapManager() {}

    public static class holder {
        public static ClientBootStrapManager INSTANCE = new ClientBootStrapManager();

    }
    public void runClientBootStrap(int port) throws UnknownHostException, InterruptedException {
        client = new Bootstrap();
        client.channel(NioSocketChannel.class);
        client.group(new NioEventLoopGroup(1)).handler(new ChannelInitializer() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                System.out.println("client channel initialized");
                int id = channelId++;
                ch.attr(ChannelAttr.CHANNEL_ID).set(id);
                ch.pipeline().addLast(new ClientInboundHandler());
            }
        });
    }

    public ChannelFuture connectToServer(int port) {
        String localHost = null;
        try {
            localHost = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        System.out.println(localHost);

        ChannelFuture future = null;
        try {
            future = client.connect(new InetSocketAddress("localhost",port)).sync();
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
