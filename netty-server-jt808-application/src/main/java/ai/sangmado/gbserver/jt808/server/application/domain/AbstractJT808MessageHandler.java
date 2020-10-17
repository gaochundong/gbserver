package ai.sangmado.gbserver.jt808.server.application.domain;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808ProtocolVersion;
import ai.sangmado.gbprotocol.jt808.protocol.exceptions.UnsupportedJT808ProtocolVersionException;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbserver.jt808.server.JT808MessageHandlerContext;
import ai.sangmado.gbserver.jt808.server.utils.Jackson;
import lombok.extern.slf4j.Slf4j;

/**
 * JT808 消息业务处理器
 */
@Slf4j
public abstract class AbstractJT808MessageHandler implements IJT808MessageHandler<JT808Message, JT808Message> {

    private final ISpecificationContext ctx;

    protected AbstractJT808MessageHandler(ISpecificationContext ctx) {
        this.ctx = ctx;
    }

    /**
     * 获取协议上下文
     *
     * @return 协议上下文
     */
    protected ISpecificationContext getContext() {
        return this.ctx;
    }

    /**
     * 处理的消息ID
     *
     * @return 消息ID
     */
    @Override
    public abstract JT808MessageId getMessageId();

    /**
     * 处理消息
     *
     * @param ctx     消息处理上下文
     * @param message 消息
     */
    @Override
    public final void handle(JT808MessageHandlerContext ctx, JT808Message message) {
        String json = Jackson.toJsonPrettyString(message);
        log.info("从设备接收到消息, 消息ID[{}], 消息名称[{}], 协议版本[{}], 连接ID[{}]{}{}",
                message.getHeader().getMessageId().getName(),
                message.getHeader().getMessageId().getDescription(),
                message.getHeader().getProtocolVersion().getName(),
                ctx.getConnection().getConnectionId(),
                System.lineSeparator(), json);

        JT808ProtocolVersion protocolVersion = message.getProtocolVersion();
        if (JT808ProtocolVersion.V2011.equals(protocolVersion)) {
            handleV2011Message(ctx, message);
        } else if (JT808ProtocolVersion.V2013.equals(protocolVersion)) {
            handleV2013Message(ctx, message);
        } else if (JT808ProtocolVersion.V2019.equals(protocolVersion)) {
            handleV2019Message(ctx, message);
        } else {
            throw new UnsupportedJT808ProtocolVersionException(protocolVersion);
        }
    }

    /**
     * 处理 JT808 V2011 版本消息
     *
     * @param ctx     消息处理上下文
     * @param message 消息
     */
    protected abstract void handleV2011Message(JT808MessageHandlerContext ctx, JT808Message message);

    /**
     * 处理 JT808 V2013 版本消息
     *
     * @param ctx     消息处理上下文
     * @param message 消息
     */
    protected abstract void handleV2013Message(JT808MessageHandlerContext ctx, JT808Message message);

    /**
     * 处理 JT808 V2019 版本消息
     *
     * @param ctx     消息处理上下文
     * @param message 消息
     */
    protected abstract void handleV2019Message(JT808MessageHandlerContext ctx, JT808Message message);
}
