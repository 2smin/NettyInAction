package BootStrap;

import io.netty.util.AttributeKey;

public class ChannelAttr {

    public static AttributeKey<Integer> CHANNEL_ID = AttributeKey.valueOf("channelId");
    public static AttributeKey<Integer> CLIENT_PORT = AttributeKey.valueOf("clientPort");
}
