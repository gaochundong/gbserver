package ai.sangmado.gbserver.jt808.server.application.handler;

import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbserver.jt808.server.dispatch.JT808MessageReceivedEvent;
import ai.sangmado.gbserver.jt808.server.utils.Jackson;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Flow;

/**
 * JT808 消息消费器
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT808MessageConsumer<I extends JT808MessagePacket, O extends JT808MessagePacket>
        implements Flow.Subscriber<JT808MessageReceivedEvent<I, O>> {

    private final Map<JT808MessageId, IJT808MessageHandler<I, O>> handlerMapping;

    public JT808MessageConsumer(Map<JT808MessageId, IJT808MessageHandler<I, O>> handlerMapping) {
        super();
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        // 无需流控, 订阅全部消息
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(JT808MessageReceivedEvent<I, O> item) {
        JT808MessageId messageId = item.getMessage().getHeader().getMessageId();

        if (!handlerMapping.containsKey(messageId)) {
            String json = Jackson.toJsonPrettyString(item.getMessage());
            log.error("从设备接收到消息, 但服务端暂未实现该消息的处理逻辑, 消息ID[{}], 消息名称[{}], 协议版本[{}], 连接ID[{}]{}{}",
                    item.getMessage().getHeader().getMessageId().getName(),
                    item.getMessage().getHeader().getMessageId().getDescription(),
                    item.getMessage().getHeader().getProtocolVersion().getName(),
                    item.getConnection().getConnectionId(),
                    System.lineSeparator(), json);
            return;
        }

        try {
            handlerMapping.get(messageId).handle(item.getConnection(), item.getMessage());
        } catch (Exception ex) {
            String json = Jackson.toJsonPrettyString(item.getMessage());
            log.error(String.format("设备消息处理失败, 消息ID[%s], 消息名称[%s], 协议版本[%s], 连接ID[%s], 错误内容[%s]%s%s",
                    item.getMessage().getHeader().getMessageId().getName(),
                    item.getMessage().getHeader().getMessageId().getDescription(),
                    item.getMessage().getHeader().getProtocolVersion().getName(),
                    item.getConnection().getConnectionId(),
                    ex.getMessage(),
                    System.lineSeparator(), json), ex);
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.error(throwable.getMessage(), throwable);
    }

    @Override
    public void onComplete() {
    }
}
