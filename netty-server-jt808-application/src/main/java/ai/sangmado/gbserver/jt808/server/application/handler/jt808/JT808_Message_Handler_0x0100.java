package ai.sangmado.gbserver.jt808.server.application.handler.jt808;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808PlatformCommonReplyResult;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808ProtocolVersion;
import ai.sangmado.gbprotocol.jt808.protocol.exceptions.UnsupportedJT808ProtocolVersionException;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacketBuilder;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808MessageContent;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x0100;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x8001;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeader;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeaderFactory;
import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.jt808.server.application.handler.IJT808MessageHandler;
import ai.sangmado.gbserver.jt808.server.utils.GlobalSerialNumberIssuer;
import ai.sangmado.gbserver.jt808.server.utils.Jackson;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 终端注册
 */
@Slf4j
@SuppressWarnings({"unchecked", "SameParameterValue"})
public class JT808_Message_Handler_0x0100<I extends JT808MessagePacket, O extends JT808MessagePacket>
        implements IJT808MessageHandler<I, O> {
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
    public void handle(Connection<I, O> connection, I message) {
        String json = Jackson.toJsonPrettyString(message);
        log.info("从设备接收到消息, 消息ID[{}], 消息名称[{}], 协议版本[{}], 连接ID[{}]{}{}",
                message.getHeader().getMessageId().getName(),
                message.getHeader().getMessageId().getDescription(),
                message.getHeader().getProtocolVersion().getName(),
                connection.getConnectionId(),
                System.lineSeparator(), json);

        JT808ProtocolVersion protocolVersion = message.getProtocolVersion();
        JT808_Message_Content_0x0100 content = (JT808_Message_Content_0x0100) message.getContent();

        if (JT808ProtocolVersion.V2011.equals(protocolVersion)) {
            log.info("终端注册, 协议版本[{}], 设备ID[{}], 车牌号[{}]", protocolVersion, content.getDeviceId(), content.getPlateNumber());
        } else if (JT808ProtocolVersion.V2013.equals(protocolVersion)) {
            log.info("终端注册, 协议版本[{}], 设备ID[{}], 车牌号[{}]", protocolVersion, content.getDeviceId(), content.getPlateNumber());
        } else if (JT808ProtocolVersion.V2019.equals(protocolVersion)) {
            log.info("终端注册, 协议版本[{}], 设备ID[{}], 车牌号[{}]", protocolVersion, content.getDeviceId(), content.getPlateNumber());
        } else {
            throw new UnsupportedJT808ProtocolVersionException(protocolVersion);
        }

        JT808MessagePacket response = create_JT808_Message_0x8001_packet(
                buildComplianceContext(protocolVersion),
                message.getHeader().getPhoneNumber(),
                message.getMessageId(),
                message.getHeader().getSerialNumber(),
                JT808PlatformCommonReplyResult.Success);
        connection.writeAndFlush((O) response);
        log.info("终端注册, 协议版本[{}], 回复成功[{}]", protocolVersion, response.getMessageId());
    }

    // 创建新的协议上下文 - 保持与请求协议版本一致
    private JT808ProtocolSpecificationContext buildComplianceContext(JT808ProtocolVersion protocolVersion) {
        JT808ProtocolSpecificationContext newContext = new JT808ProtocolSpecificationContext();
        newContext.setProtocolVersion(protocolVersion);
        newContext.setByteOrder(this.ctx.getByteOrder());
        newContext.setCharset(this.ctx.getCharset());
        newContext.setBufferPool(this.ctx.getBufferPool());
        return newContext;
    }

    // 平台通用应答
    private static JT808MessagePacket create_JT808_Message_0x8001_packet(
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

        List<JT808MessagePacket> packets = JT808MessagePacketBuilder.buildPackets(ctx, header, content);
        return packets.get(0);
    }
}
