package Https.http2;

import BootStrap.ServerBootStrapManager;

public class Server {

    public static void main(String[] args) {
        try {
            initServer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void initServer(){
        ServerBootStrapManager server = ServerBootStrapManager.ServerBootStrapManagerHolder.instance;
        server.runServerBootstrap();
        server.addInitializer(new Http2ServerInitializer());
        server.bindServerSocket(33335);
    }
}
