package ai.sangmado.gbserver.jt808.server.application.handler;

import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbserver.common.channel.Connection;

/**
 * JT808 消息业务处理器
 */
public interface IJT808MessageHandler<I, O> {

    /**
     * 处理的消息ID
     *
     * @return 消息ID
     */
    JT808MessageId getMessageId();

    /**
     * 处理消息
     *
     * @param connection 设备连接
     * @param message    消息
     */
    void handle(Connection<I, O> connection, I message);
}
