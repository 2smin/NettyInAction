package Https;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpAggregatorInitializer extends ChannelInitializer<Channel> {

    private final boolean isClient;

    public HttpAggregatorInitializer(boolean isClient) {
        this.isClient = isClient;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if(isClient){
            pipeline.addLast("decoder", new HttpClientCodec());
        } else {
            pipeline.addLast("decoder", new HttpServerCodec());
        }
        pipeline.addLast("aggregator", new HttpObjectAggregator(1024 * 1024 * 1024));
    }
}
