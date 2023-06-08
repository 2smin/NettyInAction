package Codec.Decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

public class ByteToStringDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

            System.out.println("ByteToStringDecoder in");

            int readableBytes = in.readableBytes();
            System.out.println("readable byte = " + readableBytes);
            String charset = (String) in.readCharSequence(readableBytes, Charset.defaultCharset());

            System.out.println(charset);
    }
}
