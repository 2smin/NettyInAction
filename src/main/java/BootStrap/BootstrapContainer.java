package BootStrap;

import io.netty.channel.Channel;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class BootstrapContainer {

    private static Map<String,Channel> channelMap = new HashMap<>();
    private BootstrapContainer() {
    }

    private static class holder {
        private static BootstrapContainer INSTANCE = new BootstrapContainer();
    }

    public static BootstrapContainer getInstance(){
        return holder.INSTANCE;
    }

    public void save(String channelName, Channel channel){
        channelMap.put(channelName,channel);
        System.out.println("save port with name :" + channelName);
    }

    public Channel get(String channelName){
        if(channelMap.containsKey(channelName)){
            return channelMap.get(channelName);
        }else{
            System.out.println(
                    channelMap.entrySet()
            );
            throw new InvalidParameterException("no such channel exist: " + channelName);

        }
    }
}
