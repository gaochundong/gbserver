package ai.sangmado.gbserver.common.server.event;

import ai.sangmado.gbserver.common.channel.Connection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 连接变更通知事件
 */
@Getter
@Setter
@NoArgsConstructor
public class ConnectionStatusChangedEvent<I, O> {

    public ConnectionStatusChangedEvent(Connection<I, O> connection, ConnectionStatus status) {
        this.setConnection(connection);
        this.setStatus(status);
    }

    private Connection<I, O> connection;

    private ConnectionStatus status;
}
