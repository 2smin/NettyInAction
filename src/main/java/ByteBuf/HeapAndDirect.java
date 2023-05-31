package ByteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledDirectByteBuf;
import io.netty.buffer.UnpooledHeapByteBuf;

import java.nio.charset.Charset;

public class HeapAndDirect {

    public static void main(String[] args) {

//        ByteBuf byteBuf = Unpooled.buffer();
        ByteBuf byteBuf  = Unpooled.directBuffer();
        if(byteBuf.hasArray()){
            System.out.println("this is heap buffer");
            byte[] array = byteBuf.array();
            int offset = byteBuf.arrayOffset() + byteBuf.readerIndex();
            int length = byteBuf.readableBytes();
            handleByteBuf(array,offset,length);
        }else{
            System.out.println("this is direct buffer");
            //운영체제 native memory에 위치하기 때문에 copy를 떠서 작업해야할수도.... 현재 버젼에서는 heap과 동일하게 바로 접근 가능

            byteBuf.writeBytes("this is direct".getBytes());

            //바로 접근
            int length = byteBuf.readableBytes();
            System.out.println(byteBuf.readCharSequence(length, Charset.defaultCharset()));

            //copy 떠서 접법
//            byte[] array = new byte[length];
//            byteBuf.getBytes(byteBuf.readerIndex(),array); //설정된 index부터 array에 복사
//            handleByteBuf(array, 0, length);
        }


    }


    public static void handleByteBuf(byte[] array, int offset, int length){
        //any logics.....
        for(int i=offset; i<length; i++){
            System.out.println(array[i]);
        }
    }
}
