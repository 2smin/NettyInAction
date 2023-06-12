package FileHandler;

import BootStrap.ClientBootStrapManager;
import BootStrap.ServerBootStrapManager;
import Codec.Decoder.ServerInboundHandler;
import Handler.CustomLineBasedDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Client {

    public static void main(String[] args) throws Exception{
        File file = new File("src/main/java/FileHandler/TestVideo.mp4");
        httpChunkTest(file);
        System.out.println("path : " + System.getProperty("user.dir"));

    }

    //only for data transfer, not to data handling
    public static void fileRegionTest(File file){
        try{
            ServerBootStrapManager server = ServerBootStrapManager.ServerBootStrapManagerHolder.instance;
            server.runServerBootstrap();
            server.addPipeLine(new CustomLineBasedDecoder(100), new ServerInboundHandler());
            server.bindServerSocket(33335);

            ClientBootStrapManager bootstrapManager = ClientBootStrapManager.holder.INSTANCE;
            bootstrapManager.runClientBootStrap(33335);
            ChannelFuture channelFuture = bootstrapManager.connectToServer(33335);

            FileInputStream in = new FileInputStream(file);
            FileRegion region = new DefaultFileRegion(in.getChannel(),0, file.length());

            channelFuture.channel().writeAndFlush(region);
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }

    public static void tcpChunkTest(File file){
        try{
            ServerBootStrapManager server = ServerBootStrapManager.ServerBootStrapManagerHolder.instance;
            server.runServerBootstrap();
            server.addPipeLine(new TcpChunkHandler());
            server.bindServerSocket(33335);

            ClientBootStrapManager bootstrapManager = ClientBootStrapManager.holder.INSTANCE;
            bootstrapManager.runClientBootStrap(33335);
            ChannelFuture channelFuture = bootstrapManager.connectToServer(33335);
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

    //TODO : set chunk manually by using httpContent
    public static void httpChunkTest(File file) throws  Exception{
        ServerBootStrapManager server = ServerBootStrapManager.ServerBootStrapManagerHolder.instance;
        server.runServerBootstrap();
        server.addPipeLine(
                new HttpServerCodec(), new HttpObjectAggregator(22222222),new HttpChunkInboundHandler());
        server.bindServerSocket(33335);

        ClientBootStrapManager bootstrapManager = ClientBootStrapManager.holder.INSTANCE;
        bootstrapManager.runClientBootStrap(33335);
        ChannelFuture channelFuture = bootstrapManager.connectToServer(33335);
        channelFuture.channel().pipeline().addLast(new HttpClientCodec());
        channelFuture.channel().pipeline().addLast(new HttpObjectAggregator(22222222));
        channelFuture.channel().pipeline().addLast(new ChunkedWriteHandler(4));

        try{

            //chunk는 DefaultFullHttp를 사용 불가.
            DefaultHttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/upload");

            // 청크 단위로 데이터 전송 request랑 content 따로?????

            ChunkedFile chunkedFile = new ChunkedFile(file);
            System.out.println("file : " + file.length());
            System.out.println("chunkedFile : " + chunkedFile.length());
            HttpChunkedInput httpChunkedInput = new HttpChunkedInput(chunkedFile);

            request.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, chunkedFile.length());
            request.headers().set(HttpHeaderNames.CONTENT_TYPE, "video/mp4");
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

            ByteBuf buf = Unpooled.copiedBuffer("HelloWorld", CharsetUtil.UTF_8);

            //how to use chunkedFile
            FileInputStream contentStream = new FileInputStream(file);

            // Write the content and flush it.

            //ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            channelFuture.channel().writeAndFlush(request);
            channelFuture.channel().writeAndFlush(new HttpChunkedInput(new ChunkedStream(contentStream)));
//            channelFuture.channel().writeAndFlush(httpChunkedInput);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static FullHttpRequest createHttpRequest(File file) throws IOException {

        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/upload");
        try{

            // 청크 단위로 데이터 전송
            request.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);

            RandomAccessFile raf = new RandomAccessFile(file, "r");
            ChunkedFile chunkedFile = new ChunkedFile(file);

            HttpChunkedInput httpChunkedInput = new HttpChunkedInput(chunkedFile);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, chunkedFile.length());
            request.headers().set(HttpHeaderNames.CONTENT_TYPE, "video/mp4");
        }catch (Exception e){
            e.printStackTrace();
        }
        // HTTP 요청 생성


        return request;
    }
}
