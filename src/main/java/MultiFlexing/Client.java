package MultiFlexing;

import BootStrap.ClientBootStrapManager;
import BootStrap.ServerBootStrapManager;
import Codec.Decoder.ServerInboundHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;

public class Client {

    public static void main(String[] args) {
        try{
            ServerBootStrapManager server = ServerBootStrapManager.ServerBootStrapManagerHolder.instance;

            server.runServerBootstrap();
            server.addPipeLine(new ServerInboundHandler());
            server.bindServerSocket(33335);

            ClientBootStrapManager bootstrapManager = ClientBootStrapManager.holder.INSTANCE;
            bootstrapManager.runClientBootStrap(33335);

            ChannelFuture client1ChannelFuture = bootstrapManager.connectToServer(33335);
            ChannelFuture client2ChannelFuture = bootstrapManager.connectToServer(33335);

            ByteBuf buf = Unpooled.buffer();
            buf.writeBytes("hello1".getBytes());
            client1ChannelFuture.channel().writeAndFlush(buf);


            ByteBuf buf2 = Unpooled.buffer();
            buf2.writeBytes("hello2".getBytes());
            client2ChannelFuture.channel().writeAndFlush(buf2);

        }catch (Exception e){
            e.printStackTrace();
        }




    }

}
