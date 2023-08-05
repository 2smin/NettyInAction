package Https.http2;

import BootStrap.ClientBootStrapManager;

public class Client {

    public static void main(String[] args) {
        try {
            initClient();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void initClient() throws Exception{

        ClientBootStrapManager client = ClientBootStrapManager.holder.INSTANCE;
        client.runClientBootStrap(33336);
        client.addPipeLine(new Http2ClientInitializer());
        client.connectToServer(33335);
    }
}
