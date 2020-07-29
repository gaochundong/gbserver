package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbserver.common.channel.Connection;

/**
 * 消息处理上下文
 */
public interface JT808MessageHandlerContext {

    /**
     * 获取通道连接
     *
     * @return 通道连接
     */
    Connection<JT808Message, JT808Message> getConnection();
}