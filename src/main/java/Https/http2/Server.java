package Https.http2;

import BootStrap.ServerBootStrapManager;
import io.netty.bootstrap.ServerBootstrap;

public class Server {

    public static void main(String[] args) {

    }

    public static void initServer(){
        ServerBootStrapManager server = ServerBootStrapManager.ServerBootStrapManagerHolder.instance;
        server.runServerBootstrap();
        server.addPipeLine();
    }
}
