package Codec.Decoder;

import BootStrap.BootstrapContainer;
import BootStrap.BootstrapManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class Client {

    public static void main(String[] args) throws Exception{

        BootstrapManager bootstrapManager = new BootstrapManager();

        bootstrapManager.runServerBootStrap(33335);
        ChannelFuture clientChannelFuture = bootstrapManager.runClientBootStrap(33335);

        Thread.sleep(1000); // server client thread 분리되어있으므로 containermap에 저장되기 전에 get 시도. thread sleep으로 시간차 둠
        //sync 로 스레드 블로킹 처리 후 container map에서 get
        Channel serverChannel = BootstrapContainer.getInstance().get(String.valueOf(33335));

        serverChannel.pipeline().addLast(new ToMessageDecoder()).addLast(new LastInboundHandler());

        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes("A".getBytes());

        for(int i=0; i<10; i++){
            ByteBuf copiedBuf = buf.copy();
            clientChannelFuture.channel().writeAndFlush(copiedBuf);
            System.out.println("send: " + i);
            Thread.sleep(1000);
        }

        ByteBuf buf2 = Unpooled.buffer();
        buf2.writeBytes("BBB".getBytes());

        clientChannelFuture.channel().writeAndFlush(buf2);
    }

}
