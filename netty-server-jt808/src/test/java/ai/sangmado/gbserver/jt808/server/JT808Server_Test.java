package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.gbcommon.memory.IBufferPool;
import ai.sangmado.gbprotocol.gbcommon.memory.PooledByteArrayFactory;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808ProtocolVersion;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class JT808Server_Test {

    @Mock
    private ISpecificationContext ctx;

    private IBufferPool bufferPool = new PooledByteArrayFactory(512, 10);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(ctx.getProtocolVersion()).thenReturn(JT808ProtocolVersion.V2019);
        when(ctx.getByteOrder()).thenReturn(ByteOrder.BIG_ENDIAN);
        when(ctx.getCharset()).thenReturn(Charset.forName("GBK"));
        when(ctx.getBufferPool()).thenReturn(bufferPool);
        assertEquals("GBK", ctx.getCharset().name());
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