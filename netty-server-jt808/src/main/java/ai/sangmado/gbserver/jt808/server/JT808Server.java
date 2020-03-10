package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbserver.common.channel.ConnectionHandler;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbserver.common.protocol.RequestHandler;
import ai.sangmado.gbserver.common.server.ConnectionBasedServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * JT808 业务服务器
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
@SuppressWarnings("FieldCanBeLocal")
public class JT808Server<I, O> extends ConnectionBasedServer<I, O> {
    private final ISpecificationContext ctx;

    public JT808Server(ISpecificationContext ctx, ServerBootstrap bootstrap, int port, RequestHandler<I, O> requestHandler) {
        this(ctx, bootstrap, port, requestHandler, null);
    }

    public JT808Server(ISpecificationContext ctx, ServerBootstrap bootstrap, int port, RequestHandler<I, O> requestHandler, EventExecutorGroup requestProcessingExecutor) {
        this(ctx, bootstrap, port, requestHandler, requestProcessingExecutor, null);
    }

    public JT808Server(ISpecificationContext ctx, ServerBootstrap bootstrap, int port, RequestHandler<I, O> requestHandler, EventExecutorGroup requestProcessingExecutor, PipelineConfigurator<I, O> pipelineConfigurator) {
        this(ctx, bootstrap, port, new JT808ConnectionHandler<>(requestHandler), requestProcessingExecutor, pipelineConfigurator);
    }

    protected JT808Server(ISpecificationContext ctx, ServerBootstrap bootstrap, int port, ConnectionHandler<I, O> connectionHandler, EventExecutorGroup requestProcessingExecutor, PipelineConfigurator<I, O> pipelineConfigurator) {
        super(bootstrap, port, connectionHandler, requestProcessingExecutor, pipelineConfigurator);
        this.ctx = ctx;
    }
}