package Websocket;

import BootStrap.ServerBootStrapManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

import java.net.InetSocketAddress;

public class ChatServer {

    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
    private final EventLoopGroup group = new NioEventLoopGroup();
    private Channel channel;


    public ChannelFuture start(InetSocketAddress address){
        ServerBootstrap server = new ServerBootstrap();
        server.group(group)
                .channel(NioServerSocketChannel.class).childHandler(createInitializer(channelGroup));
        ChannelFuture future = server.bind(address);
        //현재 스레드 블로킹, 다른 스레드에 의해 interrupt가 일어나지 않도록 함
        future.syncUninterruptibly();
        channel = future.channel();
        return future;
    }

    public void destroy(){
        if(channel != null){
            channel.close();
        }
        channelGroup.close();
        group.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception{
        if(args.length != 1){
            System.err.println("please give port as argument");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        final ChatServer endpoint = new ChatServer();
        ChannelFuture future = endpoint.start(new InetSocketAddress(port));
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                endpoint.destroy();
            }
        });

        //main thread는 server가 종료될때까지 blocking 된다. (server를 담당하는 nioEventLoop는 돌아가고 있음)
        future.channel().closeFuture().syncUninterruptibly();
    }

    protected ChannelInitializer createInitializer(ChannelGroup group){
        return new ChatServerInitializer(group);
    }

}
