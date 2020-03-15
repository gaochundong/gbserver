package ai.sangmado.gbserver.jt808.server.application;

import ai.sangmado.gbprotocol.gbcommon.memory.IBufferPool;
import ai.sangmado.gbprotocol.gbcommon.memory.PooledByteArrayFactory;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbserver.jt808.server.*;

import java.io.IOException;

/**
 * JT808 业务服务器应用程序
 */
@SuppressWarnings("InfiniteLoopStatement")
public class Application {
    public static void main(String[] args) {
        IBufferPool bufferPool = new PooledByteArrayFactory(512, 10);
        ISpecificationContext ctx = new JT808ProtocolSpecificationContext().withBufferPool(bufferPool);
        int port = 7200;
        JT808MessageHandler<JT808MessagePacket, JT808MessagePacket> messageHandler = new JT808MessageHandler<>(ctx);
        JT808ConnectionHandler<JT808MessagePacket, JT808MessagePacket> connectionHandler = new JT808ConnectionHandler<>(ctx, messageHandler);
        JT808ServerPipelineConfigurator<JT808MessagePacket, JT808MessagePacket> pipelineConfigurator = new JT808ServerPipelineConfigurator<>(ctx, messageHandler);
        JT808ServerBuilder<JT808MessagePacket, JT808MessagePacket> serverBuilder = new JT808ServerBuilder<>(ctx, port, connectionHandler, pipelineConfigurator);
        JT808Server<JT808MessagePacket, JT808MessagePacket> server = serverBuilder.build();
        server.start();
        System.out.println("Server is started.");

        try {
            while (true) {
                int value = System.in.read();
                System.out.println(value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
