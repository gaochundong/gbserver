package ai.sangmado.gbserver.common.server;

import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.common.channel.ConnectionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;

/**
 * 连接生命周期管理器
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
public class ConnectionLifecycleHandler<I, O> extends ChannelInboundHandlerAdapter {

    private final ConnectionHandler<I, O> connectionHandler;
    private final ConnectionFactory<I, O> connectionFactory;
    private Connection<I, O> connection;

    public ConnectionLifecycleHandler(
            ConnectionHandler<I, O> connectionHandler,
            ConnectionFactory<I, O> connectionFactory) {
        this.connectionHandler = connectionHandler;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (connection != null) {
            connection.close();
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().pipeline().get(SslHandler.class) == null) {
            connection = connectionFactory.newConnection(ctx.channel());
            super.channelActive(ctx);
            handleConnection();
        } else {
            super.channelActive(ctx);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof SslHandshakeCompletionEvent) {
            connection = connectionFactory.newConnection(ctx.channel());
            handleConnection();
        }
    }

    private void handleConnection() {
        try {
            connectionHandler.handle(connection);
        } catch (Exception ex) {
            connection.close();
        }
    }
}