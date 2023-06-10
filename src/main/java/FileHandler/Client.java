package FileHandler;

import BootStrap.ClinetBootStrapManager;
import BootStrap.ServerBootStrapManager;
import Codec.Decoder.LastInboundHandler;
import Handler.CustomLineBasedDecoder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;

public class Client {

    public static void main(String[] args) {
        File file = new File("src/main/java/FileHandler/TestFile.txt");
        fileRegionTest(file);
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
}
