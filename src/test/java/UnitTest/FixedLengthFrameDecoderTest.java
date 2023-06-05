package UnitTest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FixedLengthFrameDecoderTest {

    @Test
    public void testFrameDecodes(){
        ByteBuf buf = Unpooled.buffer();
        for(int i=0; i <9; i++){
            buf.writeByte(i);
        }

        ByteBuf input = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(
                new FixedLengthFrameDecoder(3)
        );

        Assertions.assertTrue(channel.writeInbound(input.retain()));
        Assertions.assertTrue(channel.finish());

        ByteBuf read = (ByteBuf) channel.readInbound();
        Assertions.assertEquals(buf.readSlice(3),read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        Assertions.assertEquals(buf.readSlice(3),read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        Assertions.assertEquals(buf.readSlice(3),read);
        read.release();

        Assertions.assertNull(channel.readInbound());
        buf.release();
    }
}
