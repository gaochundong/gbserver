package ai.sangmado.gbserver.jt808.server.dispatch;

import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.jt808.server.JT808MessageHandlerContext;

/**
 * 消息处理上下文
 */
public class JT808MessageDispatcherContext implements JT808MessageHandlerContext {
    private final Connection<JT808Message, JT808Message> connection;

    public JT808MessageDispatcherContext(Connection<JT808Message, JT808Message> connection) {
        this.connection = connection;
    }

    @Override
    public Connection<JT808Message, JT808Message> getConnection() {
        return connection;
    }
}