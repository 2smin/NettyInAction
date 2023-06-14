package Websocket;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUri;
    private static final File INDEX;

    static{
        //handler가 위치한 곳에서 index.html을 찾기 위해 경로를 설정
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            String path = location.toURI() + "Websocket/index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        }catch (Exception e){
            throw new IllegalStateException("unable to locate index.html",e);
        }
    }

    public HttpRequestHandler(String wsUri){
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if(wsUri.equalsIgnoreCase(request.uri())){
            ctx.fireChannelRead(request.retain());
        } else {
            //client 가 server에게 100 continue를 받는다. server가 요청 받을 준비가 되었는 체크 (handshake 느낌)
            if(HttpUtil.is100ContinueExpected(request)){
                send100Continue(ctx);
            }
            RandomAccessFile file = new RandomAccessFile(INDEX,"r"); //파일 읽기 (index.html)
            HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaderNames.CONTENT_TYPE,"text/html; charset=UTF-8");
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if(keepAlive){
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH,file.length());
                response.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response); //header만 전송... 따로 write 해도 되나????

            if(ctx.pipeline().get(SslHandler.class) == null){ //제로카피를 통해 전송 효율 극대화
                ctx.write(new DefaultFileRegion(file.getChannel(),0,file.length()));
            } else { // 암호화 작업은 cpu 사용량과 메모리 사용량이 커서 chunked로 전송하면 암호환 단위를 잘게 쪼개서 오버헤드를 줄일 수 있다.
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }

            ChannelFuture channelFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT); //last content보내서 파일전송 종료 알림. chunk가 아니어도 보내주나?

            if(!keepAlive){
                channelFuture.addListener(ChannelFutureListener.CLOSE); //keepAlive가 아니면 연결 종료
            }

        }


    }
    private static void send100Continue(ChannelHandlerContext ctx){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.CONTINUE); //continnue 100 전송
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
