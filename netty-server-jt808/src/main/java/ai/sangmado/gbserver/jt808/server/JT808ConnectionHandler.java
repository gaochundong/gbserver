package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
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
public class JT808ConnectionHandler<I extends JT808MessagePacket, O extends JT808MessagePacket>
        extends SubmissionPublisher<ConnectionStatusChangedEvent<I, O>>
        implements ConnectionHandler<I, O> {

    private final Map<String, Connection<I, O>> establishedConnections = new ConcurrentHashMap<>(64);

    public JT808ConnectionHandler() {
        super();
    }

    @Override
    public Map<String, Connection<I, O>> getEstablishedConnections() {
        return establishedConnections;
    }

    @Override
    public Connection<I, O> getEstablishedConnection(String connectionId) {
        if (connectionId == null || connectionId.length() == 0)
            throw new IllegalArgumentException("connectionId");
        return establishedConnections.get(connectionId);
    }

    @Override
    public void fireConnectionConnected(Connection<I, O> connection) {
        log.info("连接已建立, connectionId[{}]", connection.getConnectionId());
        establishedConnections.put(connection.getConnectionId(), connection);
        onConnectionConnected(connection);
    }

    @Override
    public void fireConnectionClosed(Connection<I, O> closedConnection) {
        log.info("连接已关闭, connectionId[{}]", closedConnection.getConnectionId());
        Connection<I, O> existingConnection = establishedConnections.remove(closedConnection.getConnectionId());
        onConnectionClosed(existingConnection != null ? existingConnection : closedConnection);
    }

    private void onConnectionConnected(Connection<I, O> connection) {
        // 发送连接状态变更事件
        submit(new ConnectionStatusChangedEvent<>(connection, ConnectionStatus.Connected));
    }

    private void onConnectionClosed(Connection<I, O> connection) {
        // 发送连接状态变更事件
        submit(new ConnectionStatusChangedEvent<>(connection, ConnectionStatus.Closed));
    }
}
