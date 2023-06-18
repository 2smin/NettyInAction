package Codec.Decoder;

import BootStrap.ChannelAttr;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalAddress;
import io.netty.handler.codec.http.*;

import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class ClientInboundHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        System.out.println("ClientInboundHandler received message");

        //httpObjectAggregator를 쓰지 않아야 chunk로 받을 수 있다.
        if (msg instanceof DefaultHttpContent) {
            System.out.println("http content");
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            System.out.println("readable: " + buf.readableBytes());

            FileOutputStream fileOutputStream = new FileOutputStream("src/main/java/FileHandler/save/ChunkedTestVideo.mp4");
            fileOutputStream.getChannel().write(buf.nioBuffer());

        }else if (msg instanceof LastHttpContent) {
            System.out.println("last http content");
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            System.out.println("readable: " + buf.readableBytes());
        }


    }
}
