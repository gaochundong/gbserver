package ai.sangmado.gbserver.jt808.server.dispatch;

import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbserver.jt808.server.JT808MessageHandlerContext;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 接收到JT808消息通知事件
 */
@Getter
@NoArgsConstructor
public class JT808MessageReceivedEvent {

    public JT808MessageReceivedEvent(JT808MessageHandlerContext context, JT808Message message) {
        this.context = context;
        this.message = message;
    }

    private JT808MessageHandlerContext context;

    private JT808Message message;
}