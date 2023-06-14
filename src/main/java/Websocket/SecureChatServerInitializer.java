package Websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

public class SecureChatServerInitializer extends ChatServerInitializer {

    private SslContext sslContext;

    public SecureChatServerInitializer(ChannelGroup channelGroup, SslContext sslContext) {
        super(channelGroup);
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        super.initChannel(ch);
        SSLEngine engine = sslContext.newEngine(ch.alloc());
        ch.pipeline().addFirst(new SslHandler(engine));
    }
}
