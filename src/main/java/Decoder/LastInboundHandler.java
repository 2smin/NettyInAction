package Decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

public class LastInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("LastInbound in ");


        ByteBuf read = (ByteBuf) msg;

        System.out.println("readable byte = " + read.readableBytes());
        String charset = (String) read.readCharSequence(read.readableBytes(), Charset.defaultCharset());

        System.out.println(charset);
    }
}
