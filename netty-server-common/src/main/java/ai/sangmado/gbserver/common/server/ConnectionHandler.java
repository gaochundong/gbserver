package ai.sangmado.gbserver.common.server;

import ai.sangmado.gbserver.common.channel.Connection;

import java.util.Map;

/**
 * 连接处理器
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
public interface ConnectionHandler<I, O> {

    Map<String, Connection<I, O>> getEstablishedConnections();

    Connection<I, O> getEstablishedConnection(String connectionId);

    void fireConnectionConnected(Connection<I, O> connection);

    void fireConnectionClosed(Connection<I, O> connection);
}
