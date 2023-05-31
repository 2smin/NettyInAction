package ByteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BufferUsage {

    public static void main(String[] args) {


        ByteBuf buf1 = Unpooled.buffer(20);

        buf1.writeBytes("test data".getBytes());

        ByteBuf buf2 = Unpooled.buffer(3);

        System.out.println("readble 1: " + buf2.readerIndex() + " writable 2: " + buf2.writerIndex());


        //buf2에 복사하는 작업이다. buf1 은 readerIndex 증가 , buf2는 writerIndex 증가
        buf1.readBytes(buf2);
        System.out.println("readble 1: " + buf1.readerIndex() + " writable 2: " + buf2.writerIndex());
    }
}
