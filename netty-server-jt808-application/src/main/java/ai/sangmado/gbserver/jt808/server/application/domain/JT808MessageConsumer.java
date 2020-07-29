package ai.sangmado.gbserver.jt808.server.application.domain;

import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
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
public class JT808MessageConsumer implements Flow.Subscriber<JT808MessageReceivedEvent> {

    private final Map<JT808MessageId, IJT808MessageHandler<JT808Message, JT808Message>> handlerMapping;

    public JT808MessageConsumer(Map<JT808MessageId, IJT808MessageHandler<JT808Message, JT808Message>> handlerMapping) {
        super();
        this.handlerMapping = handlerMapping;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        // 无需流控, 订阅全部消息
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(JT808MessageReceivedEvent event) {
        JT808MessageId messageId = event.getMessage().getHeader().getMessageId();

        if (!handlerMapping.containsKey(messageId)) {
            String json = Jackson.toJsonPrettyString(event.getMessage());
            log.error("从设备接收到消息, 但服务端暂未实现该消息的处理逻辑, 消息ID[{}], 消息名称[{}], 协议版本[{}], 连接ID[{}]{}{}",
                    event.getMessage().getHeader().getMessageId().getName(),
                    event.getMessage().getHeader().getMessageId().getDescription(),
                    event.getMessage().getHeader().getProtocolVersion().getName(),
                    event.getContext().getConnection().getConnectionId(),
                    System.lineSeparator(), json);
            return;
        }

        try {
            handlerMapping.get(messageId).handle(event.getContext(), event.getMessage());
        } catch (Exception ex) {
            String json = Jackson.toJsonPrettyString(event.getMessage());
            log.error(String.format("设备消息处理失败, 消息ID[%s], 消息名称[%s], 协议版本[%s], 连接ID[%s], 错误内容[%s]%s%s",
                    event.getMessage().getHeader().getMessageId().getName(),
                    event.getMessage().getHeader().getMessageId().getDescription(),
                    event.getMessage().getHeader().getProtocolVersion().getName(),
                    event.getContext().getConnection().getConnectionId(),
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
