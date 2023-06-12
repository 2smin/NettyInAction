package Codec.Decoder;

import BootStrap.ClientBootStrapManager;
import BootStrap.ServerBootStrapManager;
import Codec.CombinedByteStringCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;

public class Client2 {

    public static void main(String[] args) throws Exception{

        ServerBootStrapManager serverBootStrapManager = ServerBootStrapManager.ServerBootStrapManagerHolder.instance;

        serverBootStrapManager.runServerBootstrap();
        serverBootStrapManager.addPipeLine(
                new CombinedByteStringCodec()
        );
        serverBootStrapManager.bindServerSocket(33335);

        ClientBootStrapManager bootstrapManager = ClientBootStrapManager.holder.INSTANCE;
        bootstrapManager.runClientBootStrap(33335);
        ChannelFuture clientChannelFuture = bootstrapManager.connectToServer(33335);

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
