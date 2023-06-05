package Decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ToMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() >= 4){
            ByteBuf read = in.readBytes(in.readableBytes());
            read.writeBytes("/".getBytes());
            out.add(read);
        }else{
            System.out.println("need more readable data");
        }
    }
}
