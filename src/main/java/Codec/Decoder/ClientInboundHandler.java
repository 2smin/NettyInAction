package Codec.Decoder;

import BootStrap.ChannelAttr;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.Charset;

public class ClientInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ClientInboundHandler received message");

        String id = ctx.channel().attr(ChannelAttr.CHANNEL_ID).get().toString();
        System.out.println("client channel : " + id);

        ByteBuf read = (ByteBuf) msg;

        System.out.println("readable byte = " + read.readableBytes());
        System.out.println("client received : " + read.readCharSequence(read.readableBytes(), Charset.defaultCharset()));
        System.out.println(ctx.channel().toString());
    }
}
