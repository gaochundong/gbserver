package ai.sangmado.gbserver.jt808.server.application.domain.handler.jt808;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.IVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808PlatformCommonReplyResult;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessageAssembler;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808MessageContent;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x0100;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x8001;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.*;
import ai.sangmado.gbserver.jt808.server.JT808MessageHandlerContext;
import ai.sangmado.gbserver.jt808.server.application.domain.AbstractJT808MessageHandler;
import ai.sangmado.gbserver.jt808.server.utils.GlobalSerialNumberIssuer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 终端注册
 */
@Slf4j
@SuppressWarnings({"SameParameterValue"})
public class JT808_Message_Handler_0x0100 extends AbstractJT808MessageHandler {
    public static final JT808MessageId MESSAGE_ID = JT808MessageId.JT808_Message_0x0100;

    public JT808_Message_Handler_0x0100(ISpecificationContext ctx) {
        super(ctx);
    }

    @Override
    public JT808MessageId getMessageId() {
        return MESSAGE_ID;
    }

    @Override
    protected void handleV2011Message(JT808MessageHandlerContext ctx, JT808MessageHeader2011 header, JT808MessageContent content) {
        JT808_Message_Content_0x0100 instance = (JT808_Message_Content_0x0100) content;
        log.info("终端注册, 协议版本[{}], 设备ID[{}], 车牌号[{}]",
                header.getProtocolVersion(), instance.getDeviceId(), instance.getPlateNumber());

        JT808Message response = create_JT808_Message_0x8001_packet(
                JT808ProtocolVersionedSpecificationContext.buildFrom(header.getProtocolVersion(), this.getContext()),
                header.getPhoneNumber(),
                header.getMessageId(),
                header.getSerialNumber(),
                JT808PlatformCommonReplyResult.Success);
        ctx.getConnection().writeAndFlush(response);
        log.info("终端注册, 协议版本[{}], 回复成功[{}]", header.getProtocolVersion(), response.getMessageId());
    }

    @Override
    protected void handleV2013Message(JT808MessageHandlerContext ctx, JT808MessageHeader2013 header, JT808MessageContent content) {
        JT808_Message_Content_0x0100 instance = (JT808_Message_Content_0x0100) content;
        log.info("终端注册, 协议版本[{}], 设备ID[{}], 车牌号[{}]",
                header.getProtocolVersion(), instance.getDeviceId(), instance.getPlateNumber());

        JT808Message response = create_JT808_Message_0x8001_packet(
                JT808ProtocolVersionedSpecificationContext.buildFrom(header.getProtocolVersion(), this.getContext()),
                header.getPhoneNumber(),
                header.getMessageId(),
                header.getSerialNumber(),
                JT808PlatformCommonReplyResult.Success);
        ctx.getConnection().writeAndFlush(response);
        log.info("终端注册, 协议版本[{}], 回复成功[{}]", header.getProtocolVersion(), response.getMessageId());
    }

    @Override
    protected void handleV2019Message(JT808MessageHandlerContext ctx, JT808MessageHeader2019 header, JT808MessageContent content) {
        JT808_Message_Content_0x0100 instance = (JT808_Message_Content_0x0100) content;
        log.info("终端注册, 协议版本[{}], 版本号[{}], 设备ID[{}], 车牌号[{}]",
                header.getProtocolVersion(), header.getVersionNumber(), instance.getDeviceId(), instance.getPlateNumber());

        JT808Message response = create_JT808_Message_0x8001_packet(
                JT808ProtocolVersionedSpecificationContext.buildFrom(header.getProtocolVersion(), this.getContext()),
                header.getPhoneNumber(),
                header.getMessageId(),
                header.getSerialNumber(),
                JT808PlatformCommonReplyResult.Success);
        ctx.getConnection().writeAndFlush(response);
        log.info("终端注册, 协议版本[{}], 回复成功[{}]", header.getProtocolVersion(), response.getMessageId());
    }

    // 平台通用应答
    private static JT808Message create_JT808_Message_0x8001_packet(
            IVersionedSpecificationContext ctx,
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
