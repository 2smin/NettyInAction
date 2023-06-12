package BootStrap;

import Codec.Decoder.ServerInboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class ServerBootStrapManager {

    ServerBootstrap server;
    public int channelId = 0;

    private ServerBootStrapManager(){
    }

    public static final class ServerBootStrapManagerHolder{
        public static final ServerBootStrapManager instance = new ServerBootStrapManager();
    }

    public void runServerBootstrap(){
        server = new ServerBootstrap();
        server.group(new NioEventLoopGroup(1))
                .channel(NioServerSocketChannel.class);
    }

    public void addPipeLine(ChannelHandler... handler){
        server.childHandler(new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                int id = channelId++;
                ch.attr(ChannelAttr.CHANNEL_ID).set(id);
                ch.pipeline().addLast(new ServerInboundHandler());
            }
        });
    }

    public void bindServerSocket(int port){
        ChannelFuture future = server.bind(new InetSocketAddress(port));
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    System.out.println("port open with : " + port);
                }else{
                    System.out.println("error occured.");
                }
            }
        });
    }
}
