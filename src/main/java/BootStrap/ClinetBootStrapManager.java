package BootStrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
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
        client.group(new NioEventLoopGroup(1))
                .handler(new SimpleChannelInboundHandler() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                        System.out.println("received data");
                    }
                });

        String localHost = InetAddress.getLocalHost().getHostAddress();
        System.out.println(localHost);
        ChannelFuture future = client.connect(new InetSocketAddress("localhost",port));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    System.out.println("connect complete");
                    ByteBuf buf = Unpooled.buffer();
                    buf.writeBytes("hello world".getBytes());
                    future.channel().writeAndFlush(buf);
                }else{
                    System.out.println("connect failed");
                    future.cause().printStackTrace();
                }
            }
        });


        return future.sync();
    }
}
