package UnitTest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class EncoderUnitTest {

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

    @Test
    public void testFramesDecoded2(){
        ByteBuf buf = Unpooled.buffer();

        for(int i=0; i < 9; i++){
            buf.writeByte(i);
        }

        ByteBuf input = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(3));

        //decoder length인 3이 아직 완성되지 않아서 false 반환
        Assertions.assertFalse(channel.writeInbound(input.readBytes(2)));

        //decoder 완료 (3x3)
        Assertions.assertTrue(channel.writeInbound(input.readBytes(7)));

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

    @Test
    public void testEncoded(){
        ByteBuf buf = Unpooled.buffer();
        for(int i=0; i<10; i++){
            buf.writeInt(i*-1);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        Assertions.assertTrue(channel.writeOutbound(buf));
        Assertions.assertTrue(channel.finish());

        for(int i=0; i<10; i++){
            Assertions.assertEquals(i, (int) channel.readOutbound());
        }

        Assertions.assertNull(channel.readOutbound());
    }

    @Test
    public void chunkDecodeTest(){
        ByteBuf buf = Unpooled.buffer();
        for(int i=0; i<9; i++){
            buf.writeByte(i);
        }

        ByteBuf input = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));

        Assertions.assertTrue(channel.writeInbound(input.readBytes(2))); //frame 생성하면 true?
        try{
            channel.writeInbound(input.readBytes(4));
            Assertions.fail();
        }catch (TooLongFrameException e){
            System.out.println("exception occured");
        }

        Assertions.assertTrue(channel.writeInbound(input.readBytes(3)));
        Assertions.assertTrue(channel.finish());

        ByteBuf read = (ByteBuf) channel.readInbound();
        Assertions.assertEquals(buf.readSlice(2),read);

        read = (ByteBuf) channel.readInbound();
        Assertions.assertEquals(buf.skipBytes(4).readSlice(3),read); //폐기된 4byte도 skip 해줘야하나?
        read.release();
        buf.release();
    }
}
