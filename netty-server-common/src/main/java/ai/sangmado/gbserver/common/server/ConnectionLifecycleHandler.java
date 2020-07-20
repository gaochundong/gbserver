package ai.sangmado.gbserver.common.server;

import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.common.channel.ConnectionFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * 连接生命周期管理器
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
@Slf4j
public class ConnectionLifecycleHandler<I, O> extends ChannelInboundHandlerAdapter {

    private final ConnectionHandler<I, O> connectionHandler;
    private final ConnectionFactory<I, O> connectionFactory;

    public ConnectionLifecycleHandler(
            ConnectionHandler<I, O> connectionHandler,
            ConnectionFactory<I, O> connectionFactory) {
        this.connectionHandler = connectionHandler;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().pipeline().get(SslHandler.class) == null) {
            Connection<I, O> connection = connectionFactory.newConnection(ctx.channel());
            super.channelActive(ctx);
            fireConnectionConnected(connection);
        } else {
            super.channelActive(ctx);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof SslHandshakeCompletionEvent) {
            Connection<I, O> connection = connectionFactory.newConnection(ctx.channel());
            fireConnectionConnected(connection);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        try {
            super.channelUnregistered(ctx);
        } finally {
            Connection<I, O> closedConnection = connectionFactory.wrapClosedConnection(ctx.channel());
            fireConnectionClosed(closedConnection);
        }
    }

    private void fireConnectionConnected(Connection<I, O> connection) {
        log.info("通道连接建立, connectionId[{}]", connection.getConnectionId());
        connectionHandler.fireConnectionConnected(connection);
    }

    private void fireConnectionClosed(Connection<I, O> connection) {
        log.info("通道连接关闭, connectionId[{}]", connection.getConnectionId());
        connectionHandler.fireConnectionClosed(connection);
    }
}