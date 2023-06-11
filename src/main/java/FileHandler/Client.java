package FileHandler;

import BootStrap.ClinetBootStrapManager;
import BootStrap.ServerBootStrapManager;
import Codec.Decoder.LastInboundHandler;
import Handler.CustomLineBasedDecoder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;

public class Client {

    public static void main(String[] args) {
        File file = new File("src/main/java/FileHandler/TestVideo.mp4");
        chunkTest(file);
        System.out.println("path : " + System.getProperty("user.dir"));

    }

    //only for data transfer, not to data handling
    public static void fileRegionTest(File file){
        try{
            ServerBootStrapManager server = ServerBootStrapManager.ServerBootStrapManagerHolder.instance;
            server.runServerBootstrap();
            server.addPipeLine(new CustomLineBasedDecoder(100), new LastInboundHandler());
            server.bindServerSocket(33335);

            ClinetBootStrapManager client = new ClinetBootStrapManager();
            ChannelFuture channelFuture = client.runClientBootStrap(33335);

            FileInputStream in = new FileInputStream(file);
            FileRegion region = new DefaultFileRegion(in.getChannel(),0, file.length());

            channelFuture.channel().writeAndFlush(region);
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public static void chunkTest(File file){
        try{
            ServerBootStrapManager server = ServerBootStrapManager.ServerBootStrapManagerHolder.instance;
            server.runServerBootstrap();
            server.addPipeLine(new HttpChunkHandler());
            server.bindServerSocket(33335);

            ClinetBootStrapManager client = new ClinetBootStrapManager();
            ChannelFuture channelFuture = client.runClientBootStrap(33335);
            channelFuture.channel().pipeline().addLast(new ChunkedWriteHandler());

            //ChunkedInput is using at httpContent????
            /*
            http의 경우 chunk의 끝을 확인할 수 있지만 (객체에 메서드 있음), tcp의 경우 확인 불가능하다
            (어차피 하나의 스트림으로 보냄) 구분자나 특정 문자열로 구분해야함

            http chunk : 하나의 요청 안에서 개별 tcp 스트림으로 보낸다
            tcp chunk : 하나의 tcp 스트림에서 패킷을 나눈다다             */
            ChunkedFile chunkedFile = new ChunkedFile(file);
            channelFuture.channel().writeAndFlush(chunkedFile);

        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}
