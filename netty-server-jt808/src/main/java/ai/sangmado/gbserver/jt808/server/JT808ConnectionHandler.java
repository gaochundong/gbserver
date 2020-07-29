package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.common.server.ConnectionHandler;
import ai.sangmado.gbserver.common.server.event.ConnectionStatus;
import ai.sangmado.gbserver.common.server.event.ConnectionStatusChangedEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SubmissionPublisher;

/**
 * JT808 连接管理器
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT808ConnectionHandler
        extends SubmissionPublisher<ConnectionStatusChangedEvent<JT808Message, JT808Message>>
        implements ConnectionHandler<JT808Message, JT808Message> {

    private final Map<String, Connection<JT808Message, JT808Message>> establishedConnections = new ConcurrentHashMap<>(64);

    public JT808ConnectionHandler() {
        super();
    }

    @Override
    public Map<String, Connection<JT808Message, JT808Message>> getEstablishedConnections() {
        return establishedConnections;
    }

    @Override
    public Connection<JT808Message, JT808Message> getEstablishedConnection(String connectionId) {
        if (connectionId == null || connectionId.length() == 0)
            throw new IllegalArgumentException("connectionId");
        return establishedConnections.get(connectionId);
    }

    @Override
    public void fireConnectionConnected(Connection<JT808Message, JT808Message> connection) {
        log.info("连接已建立, connectionId[{}]", connection.getConnectionId());
        establishedConnections.put(connection.getConnectionId(), connection);
        onConnectionConnected(connection);
    }

    @Override
    public void fireConnectionClosed(Connection<JT808Message, JT808Message> closedConnection) {
        log.info("连接已关闭, connectionId[{}]", closedConnection.getConnectionId());
        Connection<JT808Message, JT808Message> existingConnection = establishedConnections.remove(closedConnection.getConnectionId());
        onConnectionClosed(existingConnection != null ? existingConnection : closedConnection);
    }

    private void onConnectionConnected(Connection<JT808Message, JT808Message> connection) {
        // 发送连接状态变更事件
        submit(new ConnectionStatusChangedEvent<>(connection, ConnectionStatus.Connected));
    }

    private void onConnectionClosed(Connection<JT808Message, JT808Message> connection) {
        // 发送连接状态变更事件
        submit(new ConnectionStatusChangedEvent<>(connection, ConnectionStatus.Closed));
    }
}
