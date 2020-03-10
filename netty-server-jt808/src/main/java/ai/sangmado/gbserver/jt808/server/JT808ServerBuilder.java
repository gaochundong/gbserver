package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbserver.common.protocol.RequestHandler;
import ai.sangmado.gbserver.common.server.ConnectionBasedServerBuilder;
import io.netty.bootstrap.ServerBootstrap;

/**
 * JT808 业务服务器构造器
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
@SuppressWarnings("FieldCanBeLocal")
public class JT808ServerBuilder<I, O> extends ConnectionBasedServerBuilder<I, O, JT808ServerBuilder<I, O>> {
    private final ISpecificationContext ctx;

    public JT808ServerBuilder(ISpecificationContext ctx, int port, RequestHandler<I, O> requestHandler) {
        super(port, new JT808ConnectionHandler<>(requestHandler));
        this.ctx = ctx;
    }

    public JT808ServerBuilder(ISpecificationContext ctx, int port, RequestHandler<I, O> requestHandler, ServerBootstrap bootstrap) {
        super(port, new JT808ConnectionHandler<>(requestHandler), bootstrap);
        this.ctx = ctx;
    }

    @Override
    protected JT808Server<I, O> createServer() {
        this.pipelineConfigurator(new JT808ServerPipelineConfigurator<>(this.ctx));
        return new JT808Server<>(this.ctx, serverBootstrap, port, connectionHandler, eventExecutorGroup, pipelineConfigurator);
    }

    @Override
    public JT808Server<I, O> build() {
        return (JT808Server<I, O>) super.build();
    }
}