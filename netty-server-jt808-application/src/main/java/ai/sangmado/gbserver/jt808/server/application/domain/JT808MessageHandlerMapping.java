package ai.sangmado.gbserver.jt808.server.application.domain;

import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;

import java.util.HashMap;
import java.util.Map;

/**
 * JT808 消息业务处理器映射
 */
public class JT808MessageHandlerMapping {
    private final Map<JT808MessageId, IJT808MessageHandler<JT808Message, JT808Message>> handlers = new HashMap<>(300);

    public void addHandler(IJT808MessageHandler<JT808Message, JT808Message> handler) {
        handlers.put(handler.getMessageId(), handler);
    }

    public void removeHandler(JT808MessageId messageId) {
        handlers.remove(messageId);
    }

    public boolean containsHandler(JT808MessageId messageId) {
        return handlers.containsKey(messageId);
    }

    public IJT808MessageHandler<JT808Message, JT808Message> getHandler(JT808MessageId messageId) {
        IJT808MessageHandler<JT808Message, JT808Message> handler = handlers.get(messageId);
        if (handler == null) {
            throw new UnsupportedOperationException(
                    String.format("暂不支持该消息: %s, %s, %s, %s",
                            messageId.getValue(), messageId.getName(),
                            messageId.getSince(), messageId.getDescription()));
        }
        return handler;
    }

    public Map<JT808MessageId, IJT808MessageHandler<JT808Message, JT808Message>> getHandlers() {
        return handlers;
    }
}
