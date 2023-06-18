package FileHandler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.File;

public class HttpServerInboundHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        System.out.println("http server inbound handler");
        if(msg instanceof HttpRequest){
            printHttpObject(msg);
            sendFile(ctx);
        }

    }

    private static void printHttpObject(HttpObject msg){
        System.out.println("------http object------");
        System.out.println(msg.toString());
    }


    private static void sendFile(ChannelHandlerContext ctx) {
        System.out.println("send file");

        File file = new File("src/main/java/FileHandler/TestVideo.mp4");
        System.out.println("file size = " + file.length());

        try {
            //chunk size 조절
            HttpChunkedInput chunkedInput = new HttpChunkedInput(new ChunkedFile(file, 50000));
            //create httpResponse From chunkedFile
            HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "video/mp4");
            httpResponse.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());

            ctx.write(httpResponse);
            ctx.writeAndFlush(chunkedInput);

            //HttpChunkedInput, ChunkedInput은 단순 data 참조를 가져다주는 역할을 한다. 실제 chunk로 나누는 작업은 ChunkedWriteHandler가 한다.


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
