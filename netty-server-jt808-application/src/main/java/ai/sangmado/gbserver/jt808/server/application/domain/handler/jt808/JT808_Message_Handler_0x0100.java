package ai.sangmado.gbserver.jt808.server.application.domain.handler.jt808;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808PlatformCommonReplyResult;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808ProtocolVersion;
import ai.sangmado.gbprotocol.jt808.protocol.exceptions.UnsupportedJT808ProtocolVersionException;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessageAssembler;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808MessageContent;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x0100;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x8001;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.*;
import ai.sangmado.gbserver.jt808.server.JT808MessageHandlerContext;
import ai.sangmado.gbserver.jt808.server.application.domain.IJT808MessageHandler;
import ai.sangmado.gbserver.jt808.server.utils.GlobalSerialNumberIssuer;
import ai.sangmado.gbserver.jt808.server.utils.Jackson;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 终端注册
 */
@Slf4j
@SuppressWarnings({"unchecked", "SameParameterValue"})
public class JT808_Message_Handler_0x0100 implements IJT808MessageHandler<JT808Message, JT808Message> {
    public static final JT808MessageId MESSAGE_ID = JT808MessageId.JT808_Message_0x0100;

    private final ISpecificationContext ctx;

    public JT808_Message_Handler_0x0100(ISpecificationContext ctx) {
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

        // 根据协议版本判断消息头和消息体类型
        JT808ProtocolVersion protocolVersion = message.getProtocolVersion();
        if (JT808ProtocolVersion.V2011.equals(protocolVersion)) {
            JT808MessageHeader2011 header = (JT808MessageHeader2011) message.getHeader();
            JT808_Message_Content_0x0100 content = (JT808_Message_Content_0x0100) message.getContent();
            log.info("终端注册, 协议版本[{}], 设备ID[{}], 车牌号[{}]",
                    header.getProtocolVersion(), content.getDeviceId(), content.getPlateNumber());
        } else if (JT808ProtocolVersion.V2013.equals(protocolVersion)) {
            JT808MessageHeader2013 header = (JT808MessageHeader2013) message.getHeader();
            JT808_Message_Content_0x0100 content = (JT808_Message_Content_0x0100) message.getContent();
            log.info("终端注册, 协议版本[{}], 设备ID[{}], 车牌号[{}]",
                    header.getProtocolVersion(), content.getDeviceId(), content.getPlateNumber());
        } else if (JT808ProtocolVersion.V2019.equals(protocolVersion)) {
            JT808MessageHeader2019 header = (JT808MessageHeader2019) message.getHeader();
            JT808_Message_Content_0x0100 content = (JT808_Message_Content_0x0100) message.getContent();
            log.info("终端注册, 协议版本[{}], 版本号[{}], 设备ID[{}], 车牌号[{}]",
                    header.getProtocolVersion(), header.getVersionNumber(), content.getDeviceId(), content.getPlateNumber());
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
        log.info("终端注册, 协议版本[{}], 回复成功[{}]", protocolVersion, response.getMessageId());
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
