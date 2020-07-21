package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbserver.common.server.ConnectionBasedServerBuilder;
import ai.sangmado.gbserver.common.server.ConnectionHandler;
import io.netty.bootstrap.ServerBootstrap;
import lombok.extern.slf4j.Slf4j;

/**
 * JT808 业务服务器构造器
 */
@Slf4j
@SuppressWarnings("FieldCanBeLocal")
public class JT808ServerBuilder<I extends JT808MessagePacket, O extends JT808MessagePacket> extends ConnectionBasedServerBuilder<I, O, JT808ServerBuilder<I, O>> {

    public JT808ServerBuilder(int port, ConnectionHandler<I, O> connectionHandler, PipelineConfigurator<I, O> pipelineConfigurator) {
        super(port, connectionHandler);
        this.pipelineConfigurator(pipelineConfigurator);
    }

    public JT808ServerBuilder(int port, ConnectionHandler<I, O> connectionHandler, PipelineConfigurator<I, O> pipelineConfigurator, ServerBootstrap bootstrap) {
        super(port, connectionHandler, bootstrap);
        this.pipelineConfigurator(pipelineConfigurator);
    }

    @Override
    protected JT808Server<I, O> createServer() {
        return new JT808Server<>(serverBootstrap, port, connectionHandler, eventExecutorGroup, pipelineConfigurator);
    }

    @Override
    public JT808Server<I, O> build() {
        return (JT808Server<I, O>) super.build();
    }
}