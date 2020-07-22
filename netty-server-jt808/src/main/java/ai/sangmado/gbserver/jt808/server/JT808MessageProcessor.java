package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.common.server.ConnectionHandler;
import ai.sangmado.gbserver.jt808.server.dispatch.JT808MessageDispatcher;
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
@SuppressWarnings({"FieldCanBeLocal", "unused", "unchecked"})
public class JT808MessageProcessor<I extends JT808MessagePacket, O extends JT808MessagePacket>
        extends MessageToMessageDecoder<JT808MessagePacket> {

    private final ConnectionHandler<I, O> connectionHandler;
    private final JT808MessageDispatcher<I, O> messageDispatcher;

    public JT808MessageProcessor(ConnectionHandler<I, O> connectionHandler, JT808MessageDispatcher<I, O> messageDispatcher) {
        this.connectionHandler = connectionHandler;
        this.messageDispatcher = messageDispatcher;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, JT808MessagePacket msg, List<Object> out) throws Exception {
        String connectionId = ctx.channel().id().asLongText();
        Connection<I, O> connection = connectionHandler.getEstablishedConnection(connectionId);

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
        messageDispatcher.dispatch(connection, (I) msg);
    }
}