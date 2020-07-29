package ai.sangmado.gbserver.jt808.server.application.domain.handler.jt1078;

import ai.sangmado.gbprotocol.jt1078.protocol.enums.JT1078MessageId;
import ai.sangmado.gbprotocol.jt1078.protocol.message.content.JT1078_Message_Content_0x1206;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808PlatformCommonReplyResult;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808ProtocolVersion;
import ai.sangmado.gbprotocol.jt808.protocol.exceptions.UnsupportedJT808ProtocolVersionException;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessageAssembler;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808MessageContent;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x8001;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeader;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeaderFactory;
import ai.sangmado.gbserver.jt808.server.JT808MessageHandlerContext;
import ai.sangmado.gbserver.jt808.server.application.domain.IJT808MessageHandler;
import ai.sangmado.gbserver.jt808.server.utils.GlobalSerialNumberIssuer;
import ai.sangmado.gbserver.jt808.server.utils.Jackson;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 终端文件上传完成通知
 */
@Slf4j
@SuppressWarnings({"SameParameterValue"})
public class JT1078_Message_Handler_0x1206 implements IJT808MessageHandler<JT808Message, JT808Message> {
    public static final JT1078MessageId MESSAGE_ID = JT1078MessageId.JT1078_Message_0x1206;

    private final ISpecificationContext ctx;

    public JT1078_Message_Handler_0x1206(ISpecificationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public JT808MessageId getMessageId() {
        return MESSAGE_ID;
    }

    @Override
    public void handle(JT808MessageHandlerContext ctx, JT808Message message) {
        String json = Jackson.toJsonPrettyString(message);
        log.info("从设备接收到消息, 消息ID[{}], 消息名称[{}], 协议版本[{}], 连接ID[{}]{}{}",
                message.getHeader().getMessageId().getName(),
                message.getHeader().getMessageId().getDescription(),
                message.getHeader().getProtocolVersion().getName(),
                ctx.getConnection().getConnectionId(),
                System.lineSeparator(), json);

        JT808ProtocolVersion protocolVersion = message.getProtocolVersion();
        JT1078_Message_Content_0x1206 content = (JT1078_Message_Content_0x1206) message.getContent();

        if (JT808ProtocolVersion.V2011.equals(protocolVersion)) {
            log.info("终端文件上传完成通知, 协议版本[{}], 上传结果[{}]", protocolVersion, content.getResult());
        } else if (JT808ProtocolVersion.V2013.equals(protocolVersion)) {
            log.info("终端文件上传完成通知, 协议版本[{}], 上传结果[{}]", protocolVersion, content.getResult());
        } else if (JT808ProtocolVersion.V2019.equals(protocolVersion)) {
            log.info("终端文件上传完成通知, 协议版本[{}], 上传结果[{}]", protocolVersion, content.getResult());
        } else {
            throw new UnsupportedJT808ProtocolVersionException(protocolVersion);
        }

        JT808Message response = create_JT808_Message_0x8001_packet(
                buildCoordinatedContext(protocolVersion),
                message.getHeader().getPhoneNumber(),
                message.getMessageId(),
                message.getHeader().getSerialNumber(),
                JT808PlatformCommonReplyResult.Success);
        ctx.getConnection().writeAndFlush(response);
        log.info("终端文件上传完成通知, 协议版本[{}], 回复成功[{}]", protocolVersion, response.getMessageId());
    }

    // 创建新的协议上下文 - 保持与请求协议版本一致
    private JT808ProtocolSpecificationContext buildCoordinatedContext(JT808ProtocolVersion protocolVersion) {
        JT808ProtocolSpecificationContext newContext = new JT808ProtocolSpecificationContext();
        newContext.setProtocolVersion(protocolVersion);
        newContext.setByteOrder(this.ctx.getByteOrder());
        newContext.setCharset(this.ctx.getCharset());
        newContext.setBufferPool(this.ctx.getBufferPool());
        return newContext;
    }

    // 平台通用应答
    private static JT808Message create_JT808_Message_0x8001_packet(
            ISpecificationContext ctx,
            String phoneNumber,
            JT808MessageId ackId,
            int ackSerialNumber,
            JT808PlatformCommonReplyResult result) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x8001;
        int serialNumber = GlobalSerialNumberIssuer.next();

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT808_Message_Content_0x8001.builder()
                .ackSerialNumber(ackSerialNumber)
                .ackId(ackId)
                .result(result)
                .build();

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }
}
