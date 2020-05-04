package org.bigtows.component.server.token;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.bigtows.component.server.token.handler.EvernoteTokenHandler;
import org.bigtows.component.server.token.handler.UriHandlerBased;
import org.bigtows.component.server.token.handler.event.EvernoteToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class TokenServer {
    private final int port;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private EvernoteToken evernoteToken;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public TokenServer() {
        this.port = PortUtility.getFreePort();
    }

    public TokenServer(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setEvernoteToken(EvernoteToken evernoteToken) {
        this.evernoteToken = evernoteToken;
    }

    public void startAsync() {
        logger.info("Start server at {} port", port);
        executorService.submit(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ServerInitializer());
                Channel ch = b.bind(port).sync().channel();
                ch.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
    }

    public void stop() {
        executorService.shutdownNow();
    }

    private class ServerInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline p = ch.pipeline();
            p.addLast("decoder", new HttpRequestDecoder());
            p.addLast("encoder", new HttpResponseEncoder());
            p.addLast("handler", new ServerHandler());
        }
    }

    class ServerHandler extends SimpleChannelInboundHandler<Object> {

        private HttpRequest request;
        private final StringBuilder buf = new StringBuilder();
        private final Map<String, UriHandlerBased> handlers = new HashMap<>();

        public ServerHandler() {
            if (handlers.size() == 0) {
                handlers.put("/evernote", new EvernoteTokenHandler(evernoteToken));
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
            UriHandlerBased handler = null;
            if (msg instanceof HttpRequest) {
                HttpRequest request = this.request = (HttpRequest) msg;
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
                buf.setLength(0);
                String context = queryStringDecoder.path();
                handler = handlers.get(context);
                if (handler != null) {
                    handler.process(request, buf);
                }
            }
            if (msg instanceof LastHttpContent) {
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HTTP_1_1,
                        ((LastHttpContent) msg).getDecoderResult().isSuccess() ? OK : BAD_REQUEST,
                        Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8)
                );
                response.headers().set(CONTENT_TYPE, handler != null ? handler.getContentType() : "text/plain; charset=UTF-8");
                response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                response.headers().set(ACCESS_CONTROL_ALLOW_HEADERS, "*");
                if (isKeepAlive(request)) {
                    response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                    response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
                }
                ctx.write(response);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}