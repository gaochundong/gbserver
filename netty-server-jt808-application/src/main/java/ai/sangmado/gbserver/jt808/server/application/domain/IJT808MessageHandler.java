package ai.sangmado.gbserver.jt808.server.application.domain;

import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbserver.jt808.server.JT808MessageHandlerContext;

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
     * @param ctx     消息处理上下文
     * @param message 消息
     */
    void handle(JT808MessageHandlerContext ctx, I message);
}
