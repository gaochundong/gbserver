package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.common.server.ConnectionHandler;

/**
 * JT808 连接管理器
 */
@SuppressWarnings("FieldCanBeLocal")
public class JT808ConnectionHandler<I extends JT808MessagePacket, O extends JT808MessagePacket> implements ConnectionHandler<I, O> {
    private final ISpecificationContext ctx;
    private final JT808MessageHandler<I, O> messageHandler;

    public JT808ConnectionHandler(ISpecificationContext ctx, JT808MessageHandler<I, O> messageHandler) {
        this.ctx = ctx;
        this.messageHandler = messageHandler;
    }

    @Override
    public void fireConnectionConnected(Connection<I, O> connection) {
        messageHandler.notifyConnectionConnected(connection);
    }

    @Override
    public void fireConnectionClosed(Connection<I, O> connection) {
        messageHandler.notifyConnectionClosed(connection);
    }
}
