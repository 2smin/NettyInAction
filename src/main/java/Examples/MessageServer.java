package Examples;

import BootStrap.ServerBootStrapManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetSocketAddress;

public class MessageServer {

    public static void main(String[] args) {
        runMessageServer();
    }

    //GlobalEventExecutor.INSTANCE : channel group의 모든 channel을 단일 스레드로 이벤트 처리한다. NioEventLoopGroup을 1로 두면 동일한 효과?
    static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static void runMessageServer(){
        ServerBootstrap server = new ServerBootstrap();
        server.group(new NioEventLoopGroup(1));
        server.channel(NioServerSocketChannel.class);

        server.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeLine = ch.pipeline();
                pipeLine.addLast(new HttpServerCodec());
                pipeLine.addLast(new HttpObjectAggregator(512*1024));
                pipeLine.addLast(new HttpWebsocketRequestHandler());
                channelGroup.add(ch);

            }
        });

        try{
            server.bind(new InetSocketAddress(33335)).sync();
        }catch (Exception e){
            e.printStackTrace();
        }



    }
}
