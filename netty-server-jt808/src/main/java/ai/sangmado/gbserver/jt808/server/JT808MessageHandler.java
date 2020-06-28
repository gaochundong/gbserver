package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.jt808.server.utils.Jackson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JT808 业务消息处理器
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT808MessageHandler<I extends JT808MessagePacket, O extends JT808MessagePacket> extends MessageToMessageDecoder<JT808MessagePacket> {
    private final ISpecificationContext ctx;
    private final Map<String, Connection<I, O>> establishedConnections = new ConcurrentHashMap<>(64);

    public JT808MessageHandler(ISpecificationContext ctx) {
        this.ctx = ctx;
    }

    public Map<String, Connection<I, O>> getEstablishedConnections() {
        return establishedConnections;
    }

    public void notifyConnectionConnected(Connection<I, O> connection) {
        log.info("设备建立连接, connectionId[{}]", connection.getConnectionId());
        establishedConnections.put(connection.getConnectionId(), connection);
    }

    public void notifyConnectionClosed(String connectionId) {
        log.info("设备关闭连接, connectionId[{}]", connectionId);
        establishedConnections.remove(connectionId);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, JT808MessagePacket msg, List<Object> out) throws Exception {
        String connectionId = ctx.channel().id().asLongText();
        Connection<I, O> connection = establishedConnections.get(connectionId);

        String json = Jackson.toJsonPrettyString(msg);
        log.info("从设备接收到消息, 消息ID[{}], 消息名称[{}], 协议版本[{}], 连接ID[{}]{}{}",
                msg.getHeader().getMessageId().getName(),
                msg.getHeader().getMessageId().getDescription(),
                msg.getHeader().getProtocolVersion().getName(),
                connectionId,
                System.lineSeparator(), json);
    }
}