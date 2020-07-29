package ai.sangmado.gbserver.jt808.server.dispatch;

import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * JT808 消息分发器
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT808MessageDispatcher extends SubmissionPublisher<JT808MessageReceivedEvent> {

    public JT808MessageDispatcher() {
        super();
    }

    public void dispatch(JT808MessageDispatcherContext ctx, JT808Message message) {
        // 通过 Flow 分发消息至业务域
        submit(new JT808MessageReceivedEvent(ctx, message));
    }

    public JT808MessageDispatcher bindSubscriber(Flow.Subscriber<JT808MessageReceivedEvent> subscriber) {
        // 业务域订阅消息分发
        this.subscribe(subscriber);
        return this;
    }
}
