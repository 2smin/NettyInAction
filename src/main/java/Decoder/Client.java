package Decoder;

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

        //sync 로 스레드 블로킹 처리 후 container map에서 get
        Channel serverChannel = BootstrapContainer.getInstance().get(String.valueOf(33335));

        serverChannel.pipeline().addLast(new ToMessageDecoder()).addLast(new ToMessageDecoder());

        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes("A".getBytes());

        for(int i=0; i<10; i++){
            ByteBuf copiedBuf = buf.copy();
            clientChannelFuture.channel().writeAndFlush(copiedBuf);
            System.out.println("send: " + i);
            Thread.sleep(1000);
        }


    }

}
