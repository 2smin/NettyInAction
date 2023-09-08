package Https.http2.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.*;

import java.nio.charset.StandardCharsets;

public class Http2SimpleHandler extends Http2ConnectionHandler implements Http2FrameListener {


    public Http2SimpleHandler(Http2ConnectionDecoder decoder, Http2ConnectionEncoder encoder, Http2Settings initialSettings) {
        super(decoder, encoder, initialSettings);
    }

    private void sendResponse(ChannelHandlerContext ctx, int streamId, ByteBuf payload) {
        Http2Headers headers = new DefaultHttp2Headers().status("200");
        payload.writeBytes("RESPONSE FROM NETTY SERVER".getBytes(StandardCharsets.UTF_8));

        encoder().writeHeaders(ctx, streamId, headers, 0, false, ctx.newPromise());
        encoder().writeData(ctx, streamId, payload, 0, true, ctx.newPromise());
        ctx.flush();
    }


    @Override
    public int onDataRead(ChannelHandlerContext ctx, int streamId, ByteBuf data, int padding, boolean endOfStream) throws Http2Exception {
        int processed = data.readableBytes() + padding;
        if (endOfStream) {
            System.out.println("onDataRead  end of stream, streamId: " + streamId);
            sendResponse(ctx, streamId, data.retain());
        }else{
            System.out.println("onDataRead not end of stream, streamId: " + streamId);
        }


        return processed;
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int padding, boolean endOfStream) throws Http2Exception {
        if (endOfStream) {

            System.out.println("onHeadersRead this is end of stream, streamId: " + streamId);
            ByteBuf content = ctx.alloc().buffer();
            content.writeBytes("HEADER_READ FROM HTTP2 SIMPLE HANDLER".getBytes());
            ByteBufUtil.writeAscii(content, " - via HTTP/2");
            sendResponse(ctx, streamId, content);
        }else{
            System.out.println("onHeadersRead not end of stream, streamId: " + streamId);
        }
    }

    @Override
    public void onHeadersRead(ChannelHandlerContext ctx, int streamId, Http2Headers headers, int streamDependency, short weight, boolean exclusive, int padding, boolean endOfStream) throws Http2Exception {
        onHeadersRead(ctx, streamId, headers, padding, endOfStream);
    }

    @Override
    public void onPriorityRead(ChannelHandlerContext ctx, int streamId, int streamDependency, short weight, boolean exclusive) throws Http2Exception {

    }

    @Override
    public void onRstStreamRead(ChannelHandlerContext ctx, int streamId, long errorCode) throws Http2Exception {

    }

    @Override
    public void onSettingsAckRead(ChannelHandlerContext ctx) throws Http2Exception {

    }

    @Override
    public void onSettingsRead(ChannelHandlerContext ctx, Http2Settings settings) throws Http2Exception {

    }

    @Override
    public void onPingRead(ChannelHandlerContext ctx, long data) throws Http2Exception {

    }

    @Override
    public void onPingAckRead(ChannelHandlerContext ctx, long data) throws Http2Exception {

    }

    @Override
    public void onPushPromiseRead(ChannelHandlerContext ctx, int streamId, int promisedStreamId, Http2Headers headers, int padding) throws Http2Exception {

    }

    @Override
    public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData) throws Http2Exception {

    }

    @Override
    public void onWindowUpdateRead(ChannelHandlerContext ctx, int streamId, int windowSizeIncrement) throws Http2Exception {

    }

    @Override
    public void onUnknownFrame(ChannelHandlerContext ctx, byte frameType, int streamId, Http2Flags flags, ByteBuf payload) throws Http2Exception {

    }
}
