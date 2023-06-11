package FileHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;

import java.io.FileOutputStream;

public class HttpChunkHandler extends ChannelInboundHandlerAdapter {

    private String saveFilePath;
    private FileOutputStream fileOutputStream;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        setFilePath("src/main/java/FileHandler/save/ChunkedTestVideo.mp4");
        fileOutputStream = new FileOutputStream(saveFilePath);
    }

    public void setFilePath(String filepath){
        this.saveFilePath = filepath;
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //how to use chunkedFile instance ????
        System.out.println("is not chunkedFile");
        ByteBuf contentBuf = (ByteBuf)msg;

        //readableByte is not same each time. need to handle this
        System.out.println("readable byte = " + contentBuf.readableBytes());
        fileOutputStream.getChannel().write(contentBuf.nioBuffer()); //nioBuffer ?
        contentBuf.release();

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }
}

