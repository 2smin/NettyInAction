package Https.http2.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Server {

    public static void main(String[] args) {
        try {
            initServer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void initServer(){

        try{
            ServerBootstrap server = new ServerBootstrap();
            server.option(ChannelOption.SO_BACKLOG, 1024);
            server.group(new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new Http2ServerInitializer());

            Channel ch = server.bind(33335).sync().channel();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
