package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.jt808.server.dispatch.JT808MessageDispatcher;
import ai.sangmado.gbserver.jt808.server.dispatch.JT808MessageDispatcherContext;
import ai.sangmado.gbserver.jt808.server.utils.Jackson;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * JT808 业务消息处理器
 */
@Slf4j
@Sharable
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT808MessageProcessor extends MessageToMessageDecoder<JT808Message> {

    private final JT808ConnectionHandler connectionHandler;
    private final JT808MessageDispatcher messageDispatcher;

    public JT808MessageProcessor(JT808ConnectionHandler connectionHandler, JT808MessageDispatcher messageDispatcher) {
        this.connectionHandler = connectionHandler;
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, JT808Message msg, List<Object> out) throws Exception {
        String connectionId = ctx.channel().id().asLongText();
        Connection<JT808Message, JT808Message> connection = connectionHandler.getEstablishedConnection(connectionId);

        // 恰巧收到消息后连接已断开, 消息丢弃
        if (connection == null) {
            String json = Jackson.toJsonPrettyString(msg);
            log.warn("从设备接收到消息, 但设备已断开连接, 消息丢弃, 消息ID[{}], 消息名称[{}], 协议版本[{}], 连接ID[{}]{}{}",
                    msg.getHeader().getMessageId().getName(),
                    msg.getHeader().getMessageId().getDescription(),
                    msg.getHeader().getProtocolVersion().getName(),
                    connectionId,
                    System.lineSeparator(), json);
            return;
        }

        // 分发消息至业务域
        messageDispatcher.dispatch(new JT808MessageDispatcherContext(connection), msg);
    }
}