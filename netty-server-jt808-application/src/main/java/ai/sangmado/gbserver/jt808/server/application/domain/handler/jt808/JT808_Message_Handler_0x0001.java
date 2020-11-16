package ai.sangmado.gbserver.jt808.server.application.domain.handler.jt808;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808MessageContent;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeader2011;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeader2013;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeader2019;
import ai.sangmado.gbserver.jt808.server.JT808MessageHandlerContext;
import ai.sangmado.gbserver.jt808.server.application.domain.AbstractJT808MessageHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 终端通用应答
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal"})
public class JT808_Message_Handler_0x0001 extends AbstractJT808MessageHandler {
    public static final JT808MessageId MESSAGE_ID = JT808MessageId.JT808_Message_0x0001;

    public JT808_Message_Handler_0x0001(ISpecificationContext ctx) {
        super(ctx);
    }

    @Override
    public JT808MessageId getMessageId() {
        return MESSAGE_ID;
    }

    @Override
    protected void handleV2011Message(JT808MessageHandlerContext ctx, JT808MessageHeader2011 header, JT808MessageContent content) {
        // 无需处理, 只打日志
    }

    @Override
    protected void handleV2013Message(JT808MessageHandlerContext ctx, JT808MessageHeader2013 header, JT808MessageContent content) {
        // 无需处理, 只打日志
    }

    @Override
    protected void handleV2019Message(JT808MessageHandlerContext ctx, JT808MessageHeader2019 header, JT808MessageContent content) {
        // 无需处理, 只打日志
    }
}
