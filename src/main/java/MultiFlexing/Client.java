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
            bootstrapManager.runClientBootStrap(0);

            //동일 port로 multi socket이 생성되어, server측에서 각각을 다르게 인식하는지 테스트
            //client port를 특정해서 생성하면, server측에서는 channel을 여러개 생성할 수 없다. (already in use)
            //channelFuture에서 connect 하면 다음 connect는 새로운 port 번호를 할당받음.
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
