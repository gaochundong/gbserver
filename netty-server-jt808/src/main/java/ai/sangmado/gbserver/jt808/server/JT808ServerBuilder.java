package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbserver.common.server.ConnectionBasedServerBuilder;
import ai.sangmado.gbserver.common.server.ConnectionHandler;
import io.netty.bootstrap.ServerBootstrap;

/**
 * JT808 业务服务器构造器
 */
@SuppressWarnings("FieldCanBeLocal")
public class JT808ServerBuilder<I extends JT808MessagePacket, O extends JT808MessagePacket> extends ConnectionBasedServerBuilder<I, O, JT808ServerBuilder<I, O>> {
    private final ISpecificationContext ctx;

    public JT808ServerBuilder(ISpecificationContext ctx, int port, ConnectionHandler<I, O> connectionHandler, PipelineConfigurator<I, O> pipelineConfigurator) {
        super(port, connectionHandler);
        this.ctx = ctx;
        this.pipelineConfigurator(pipelineConfigurator);
    }

    public JT808ServerBuilder(ISpecificationContext ctx, int port, ConnectionHandler<I, O> connectionHandler, PipelineConfigurator<I, O> pipelineConfigurator, ServerBootstrap bootstrap) {
        super(port, connectionHandler, bootstrap);
        this.ctx = ctx;
        this.pipelineConfigurator(pipelineConfigurator);
    }

    @Override
    protected JT808Server<I, O> createServer() {
        return new JT808Server<>(this.ctx, serverBootstrap, port, connectionHandler, eventExecutorGroup, pipelineConfigurator);
    }

    @Override
    public JT808Server<I, O> build() {
        return (JT808Server<I, O>) super.build();
    }
}