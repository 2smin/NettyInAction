package BootStrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class ClinetBootStrapManager {

    public static void main(String[] args) throws Exception{
    }

    public ChannelFuture runClientBootStrap(int port) throws UnknownHostException, InterruptedException {
        Bootstrap client = new Bootstrap();
        client.channel(NioSocketChannel.class);
        client.group(new NioEventLoopGroup(1)).handler(new ChannelInitializer() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                System.out.println("client channel initialized");
            }
        });

        String localHost = InetAddress.getLocalHost().getHostAddress();
        System.out.println(localHost);
        ChannelFuture future = client.connect(new InetSocketAddress("localhost",port)).sync();
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
