package FileHandler;

import BootStrap.ClientBootStrapManager;
import BootStrap.ServerBootStrapManager;
import Codec.Decoder.ClientInboundHandler;
import Codec.Decoder.ServerInboundHandler;
import Handler.CustomLineBasedDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

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
        ServerBootstrap server = new ServerBootstrap();
        server.group(new NioEventLoopGroup());
        server.channel(NioServerSocketChannel.class);
        server.childHandler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(3000000));
                pipeline.addLast(new ChunkedWriteHandler());
                pipeline.addLast(new HttpServerInboundHandler());

                System.out.println("initialize http server");
            }
        });

        server.bind(33335).sync();
        Bootstrap client = new Bootstrap();
        client.group(new NioEventLoopGroup());
        client.channel(NioSocketChannel.class);
        client.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();

                pipeline.addLast(new HttpClientCodec());
                pipeline.addLast(new HttpResponseDecoder());
                pipeline.addLast(new HttpObjectAggregator(3000000));
                pipeline.addLast(new ChunkedWriteHandler());
                pipeline.addLast(new ClientInboundHandler());
                System.out.println("initialize http client");
            }
        });

        ChannelFuture channelFuture = client.connect(new InetSocketAddress(33335)).sync();
        channelFuture.channel().writeAndFlush(createHttpRequest());

    }

    private static HttpRequest createHttpRequest(){
        HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/getFile");
        request.headers().set(HttpHeaderNames.HOST, "localhost");
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        return request;
    }

    private static FullHttpRequest createHttpRequest(File file) throws IOException {

        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/upload");
        try{

            // 청크 단위로 데이터 전송
            request.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);


            HttpChunkedInput httpChunkedInput = new HttpChunkedInput(new ChunkedFile(file));
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpChunkedInput.length());
            request.headers().set(HttpHeaderNames.CONTENT_TYPE, "video/mp4");

        }catch (Exception e){
            e.printStackTrace();
        }
        // HTTP 요청 생성


        return request;
    }
}
