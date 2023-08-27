package Https.http2.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http2.Http2Frame;

import java.util.HashMap;
import java.util.Map;

public class Http2FrameContainer {

    private Http2FrameContainer container;

    static class holder {
        private static Http2FrameContainer container = new Http2FrameContainer();
    }

    public Http2FrameContainer getInstance(){
        return holder.container;
    }

    public Map<Integer, ByteBuf> frames = new HashMap<>();

    public Http2Frame mergeFrames(int streamId, boolean endOfStream, ByteBuf byteBuf){
        ByteBuf prevBuf = null;
        if(frames.containsKey(streamId)){
            prevBuf = frames.get(streamId);
            prevBuf.writeBytes(byteBuf);
        }else{
            prevBuf = Unpooled.buffer();
            prevBuf.writeBytes(byteBuf);
        }

        if(endOfStream){
            return (Http2Frame) prevBuf;
        }else{
            //FIXME : do nothing
            return null;
        }

    }
}
