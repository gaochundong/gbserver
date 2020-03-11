package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.common.server.ConnectionHandler;

/**
 * JT808 连接管理器
 *
 * @param <I> 读取 JT808 业务消息
 * @param <O> 写入 JT808 业务消息
 */
@SuppressWarnings("FieldCanBeLocal")
public class JT808ConnectionHandler<I, O> implements ConnectionHandler<I, O> {

    private final IMessageHandler<I, O> messageHandler;

    public JT808ConnectionHandler(IMessageHandler<I, O> messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void handle(Connection<I, O> newConnection) {
    }
}
