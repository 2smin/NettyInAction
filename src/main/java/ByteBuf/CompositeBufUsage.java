package ByteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

public class CompositeBufUsage {

    public static void main(String[] args) {
        composite();
    }

    public static void composite(){

        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();

        ByteBuf heapBuf = Unpooled.buffer();
        ByteBuf directBuf = Unpooled.directBuffer();

        heapBuf.writeBytes("fromHeap".getBytes());
        directBuf.writeBytes("fromDirect".getBytes());

        compositeByteBuf.addComponents(heapBuf,directBuf);

        for(ByteBuf buf : compositeByteBuf){
            System.out.println(buf.toString());
        }

        //directBuffer와 heapBuffer 데이터 접근법 차이는 없나??? 현재는 동일한듯. 버젼업 되면서 direct buf 개선된것일수도
        for(ByteBuf buf : compositeByteBuf){
            int length = buf.readableBytes();
            CharSequence read = buf.readCharSequence(length, Charset.defaultCharset());
            System.out.println(read);
        }
    }
}
