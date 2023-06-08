package Codec.Decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

public class ToMessageDecoder extends ByteToMessageDecoder {

    private static final int MAX_FRAME_SIZE = 5;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes() >= MAX_FRAME_SIZE){
            System.out.println("too long frame: " + in.readableBytes());
            in.clear();
            throw new TooLongFrameException("too long frame");
        }
        if(in.readableBytes() == 4){
            ByteBuf read = in.readBytes(in.readableBytes());
            read.writeBytes("/".getBytes());
            out.add(read);
        }else{
            System.out.println("need more readable data");
        }
    }
}
