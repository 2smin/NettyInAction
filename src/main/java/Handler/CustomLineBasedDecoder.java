package Handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.nio.charset.Charset;

public class CustomLineBasedDecoder extends LineBasedFrameDecoder {

    final byte SPACE = (byte) ' ';

    public CustomLineBasedDecoder(int maxLength) {
        super(maxLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx,buffer);
        if(frame == null){
            return null;
        }

        ByteBuf sendFrame = frame.copy();

        System.out.println("line based decoder readableByte : " + frame.readableBytes());
        String readFrame = (String) frame.readCharSequence(frame.readableBytes(), Charset.defaultCharset());
        System.out.println("line based decoder  read : " + readFrame);

        return sendFrame;
    }
}
