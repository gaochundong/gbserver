package ai.sangmado.gbserver.jt808.server.application;

import ai.sangmado.gbprotocol.gbcommon.memory.IBufferPool;
import ai.sangmado.gbprotocol.gbcommon.memory.PooledByteArrayFactory;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbserver.jt808.server.JT808MessageHandler;
import ai.sangmado.gbserver.jt808.server.JT808Server;
import ai.sangmado.gbserver.jt808.server.JT808ServerBuilder;

/**
 * JT808 业务服务器应用程序
 */
public class Application {
    public static void main(String[] args) {
        IBufferPool bufferPool = new PooledByteArrayFactory(512, 10);
        ISpecificationContext ctx = new JT808ProtocolSpecificationContext().withBufferPool(bufferPool);
        int port = 7200;
        JT808MessageHandler<JT808MessagePacket, JT808MessagePacket> messageHandler = new JT808MessageHandler<>();
        JT808ServerBuilder<JT808MessagePacket, JT808MessagePacket> serverBuilder = new JT808ServerBuilder<>(ctx, port, messageHandler);
        JT808Server<JT808MessagePacket, JT808MessagePacket> server = serverBuilder.build();
        server.startAndWait();
    }
}
