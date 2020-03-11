package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.gbcommon.memory.IBufferPool;
import ai.sangmado.gbprotocol.gbcommon.memory.PooledByteArrayFactory;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

public class JT808Server_Test {

    private IBufferPool bufferPool = new PooledByteArrayFactory(512, 10);
    private ISpecificationContext ctx = new JT808ProtocolSpecificationContext().withBufferPool(bufferPool);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void when_jt808_server_start_then_should_receive_message() {
        int port = 7200;
        JT808MessageHandler<JT808MessagePacket, JT808MessagePacket> messageHandler = new JT808MessageHandler<>();
        JT808ServerBuilder<JT808MessagePacket, JT808MessagePacket> serverBuilder = new JT808ServerBuilder<>(ctx, port, messageHandler);
        JT808Server<JT808MessagePacket, JT808MessagePacket> server = serverBuilder.build();
        server.startAndWait();
    }
}