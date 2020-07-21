package ai.sangmado.gbserver.jt808.server.application.handler;

import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;

import java.util.HashMap;
import java.util.Map;

/**
 * JT808 消息业务处理器映射
 */
public class JT808MessageHandlerMapping<I extends JT808MessagePacket, O extends JT808MessagePacket> {
    private final Map<JT808MessageId, IJT808MessageHandler<I, O>> handlers = new HashMap<>(300);

    public void addHandler(IJT808MessageHandler<I, O> handler) {
        handlers.put(handler.getMessageId(), handler);
    }

    public void removeHandler(JT808MessageId messageId) {
        handlers.remove(messageId);
    }

    public boolean containsHandler(JT808MessageId messageId) {
        return handlers.containsKey(messageId);
    }

    public IJT808MessageHandler<I, O> getHandler(JT808MessageId messageId) {
        IJT808MessageHandler<I, O> handler = handlers.get(messageId);
        if (handler == null) {
            throw new UnsupportedOperationException(
                    String.format("暂不支持该消息: %s, %s, %s, %s",
                            messageId.getValue(), messageId.getName(),
                            messageId.getSince(), messageId.getDescription()));
        }
        return handler;
    }

    public Map<JT808MessageId, IJT808MessageHandler<I, O>> getHandlers() {
        return handlers;
    }
}
