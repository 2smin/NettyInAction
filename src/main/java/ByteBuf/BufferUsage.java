package ByteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufProcessor;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

public class BufferUsage {

    public static void main(String[] args) {
        slice();
    }

    public static void copyBuffer(){
        ByteBuf buf1 = Unpooled.buffer(20);

        buf1.writeBytes("test data".getBytes());

        ByteBuf buf2 = Unpooled.buffer(3);

        System.out.println("readble 1: " + buf2.readerIndex() + " writable 2: " + buf2.writerIndex());


        //buf2에 복사하는 작업이다. buf1 은 readerIndex 증가 , buf2는 writerIndex 증가
        buf1.readBytes(buf2);
        System.out.println("readble 1: " + buf1.readerIndex() + " writable 2: " + buf2.writerIndex());

    }

    public static void searchIndex() {

        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes("abcdefg".getBytes());
        byte search = 'c';
        System.out.println("index: " + buf.indexOf(0, buf.readerIndex(), search));
    }

    public static void managingIndex() {

        ByteBuf buf = Unpooled.buffer();

        //discardReadBytes() -> 읽은 index 폐기 후 readerIndex 0으로 돌림 (메모리 복사가 일어난다)

        System.out.println("readble : " + buf.readerIndex() + " writable : " + buf.writableBytes());
        buf.writeBytes("test data".getBytes());

        System.out.println("readble : " + buf.readerIndex() + " writable : " + buf.writableBytes());
        buf.readBytes(4);

        System.out.println("readble : " + buf.readerIndex() + " writable : " + buf.writableBytes());

        buf.discardReadBytes();

        System.out.println("readble : " + buf.readerIndex() + " writable : " + buf.writableBytes());


        //clear() -> 전체 인덱스를 재설정한다. 다만 메모리 복사 없이 인덱스만 조정하므로 리소스가 적게 든다
    }


    public static void slice(){

        //slice() 는 원본 인스턴스와 공유되는 copy 생성 (shallow copy)
        ByteBuf buf = Unpooled.copiedBuffer("slice copy test",Charset.defaultCharset());
        ByteBuf slicedBuf = buf.slice(0,10);
        System.out.println(slicedBuf.toString(Charset.defaultCharset()));
        buf.setByte(0,(byte)'t');
        System.out.println((char)buf.getByte(0));
        System.out.println((char)slicedBuf.getByte(0));


    }
}
