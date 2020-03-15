package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbserver.common.channel.Connection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JT808 业务消息处理器
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT808MessageHandler<I extends JT808MessagePacket, O extends JT808MessagePacket> extends MessageToMessageDecoder<JT808MessagePacket> {
    private final ISpecificationContext ctx;
    private final Map<String, Connection<I, O>> connections = new ConcurrentHashMap<>(1000);

    public JT808MessageHandler(ISpecificationContext ctx) {
        this.ctx = ctx;
    }

    public void notifyConnectionConnected(Connection<I, O> connection) {
        // 新的连接建立
        connections.put(connection.getConnectionId(), connection);
    }

    public void notifyConnectionClosed(Connection<I, O> connection) {
        // 连接已关闭
        connections.remove(connection.getConnectionId());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, JT808MessagePacket msg, List<Object> out) throws Exception {
        String connectionId = ctx.channel().id().asLongText();
        Connection<I, O> connection = connections.get(connectionId);
        System.out.println(connectionId + " 接收到 " + msg.getHeader().getMessageId().getName());
        out.add(msg);
    }
}