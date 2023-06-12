package Codec.Decoder;

import BootStrap.ChannelAttr;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

public class ServerInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String id = ctx.channel().attr(ChannelAttr.CHANNEL_ID).get().toString();
        System.out.println("Server Channel : " + id);

        ByteBuf read = (ByteBuf) msg;

        System.out.println("readable byte = " + read.readableBytes());
        String charset = (String) read.readCharSequence(read.readableBytes(), Charset.defaultCharset());

        System.out.println(charset);

        ByteBuf letter = Unpooled.buffer();
        letter.writeBytes("send from server : ".getBytes());
        letter.writeBytes(id.getBytes());
        ctx.writeAndFlush(letter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
    }
}
