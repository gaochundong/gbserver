package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.common.server.ConnectionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * JT808 连接管理器
 */
@Slf4j
@SuppressWarnings("FieldCanBeLocal")
public class JT808ConnectionHandler<I extends JT808MessagePacket, O extends JT808MessagePacket> implements ConnectionHandler<I, O> {
    private final ISpecificationContext ctx;
    private final JT808MessageProcessor<I, O> subscriber;

    public JT808ConnectionHandler(ISpecificationContext ctx, JT808MessageProcessor<I, O> subscriber) {
        this.ctx = ctx;
        this.subscriber = subscriber;
    }

    @Override
    public void fireConnectionConnected(Connection<I, O> connection) {
        log.info("连接已建立, connectionId[{}]", connection.getConnectionId());
        subscriber.notifyConnectionConnected(connection);
    }

    @Override
    public void fireConnectionClosed(Connection<I, O> connection) {
        log.info("连接已关闭, connectionId[{}]", connection.getConnectionId());
        subscriber.notifyConnectionClosed(connection);
    }
}
