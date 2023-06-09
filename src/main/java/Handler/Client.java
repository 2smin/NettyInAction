package Handler;

import BootStrap.ClinetBootStrapManager;
import BootStrap.ServerBootStrapManager;
import Codec.Decoder.LastInboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class Client {

    public static void main(String[] args) throws Exception{
        run3();
    }

    public static void run1() throws Exception{
        ServerBootStrapManager server = ServerBootStrapManager.ServerBootStrapManagerHolder.instance;

        server.runServerBootstrap();
        server.addPipeLine(new IdleStateHandler(0,0,30, TimeUnit.SECONDS),
                new HeartbeatHandler(),
                new LastInboundHandler());
        server.bindServerSocket(33335);

        ClinetBootStrapManager client = new ClinetBootStrapManager();
        ChannelFuture future = client.runClientBootStrap(33335);

    }

    public static void run2() throws Exception{
        ServerBootStrapManager server = ServerBootStrapManager.ServerBootStrapManagerHolder.instance;

        server.runServerBootstrap();
        server.addPipeLine(new CustomLineBasedDecoder(64*1028),
                new LastInboundHandler());
        server.bindServerSocket(33335);

        ClinetBootStrapManager client = new ClinetBootStrapManager();
        ChannelFuture future = client.runClientBootStrap(33335);

        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeBytes("abc\ndc\nefqwfqfq\n1231f".getBytes());
        future.channel().writeAndFlush(sendBuf);
    }

    public static void run3() throws Exception{
        ServerBootStrapManager server = ServerBootStrapManager.ServerBootStrapManagerHolder.instance;

        server.runServerBootstrap();
        server.addPipeLine(new FixedLengthFrameDecoder(8),
                new LastInboundHandler());
        server.bindServerSocket(33335);

        ClinetBootStrapManager client = new ClinetBootStrapManager();
        ChannelFuture future = client.runClientBootStrap(33335);

        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeBytes("abcfefawf3e1231f".getBytes());
        future.channel().writeAndFlush(sendBuf);

        //how to use LengthFieldBasedFrameDecoder
    }
}
