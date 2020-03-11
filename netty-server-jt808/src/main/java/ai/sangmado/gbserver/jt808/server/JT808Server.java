package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbserver.common.server.ConnectionBasedServer;
import ai.sangmado.gbserver.common.server.ConnectionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * JT808 业务服务器
 *
 * @param <I> 读取 JT808 业务消息
 * @param <O> 写入 JT808 业务消息
 */
@SuppressWarnings("FieldCanBeLocal")
public class JT808Server<I, O> extends ConnectionBasedServer<I, O> {
    private final ISpecificationContext ctx;

    public JT808Server(ISpecificationContext ctx, ServerBootstrap bootstrap, int port, IMessageHandler<I, O> messageHandler) {
        this(ctx, bootstrap, port, messageHandler, null);
    }

    public JT808Server(ISpecificationContext ctx, ServerBootstrap bootstrap, int port, IMessageHandler<I, O> messageHandler, EventExecutorGroup requestProcessingExecutor) {
        this(ctx, bootstrap, port, messageHandler, requestProcessingExecutor, null);
    }

    public JT808Server(ISpecificationContext ctx, ServerBootstrap bootstrap, int port, IMessageHandler<I, O> messageHandler, EventExecutorGroup requestProcessingExecutor, PipelineConfigurator<I, O> pipelineConfigurator) {
        this(ctx, bootstrap, port, new JT808ConnectionHandler<>(messageHandler), requestProcessingExecutor, pipelineConfigurator);
    }

    protected JT808Server(ISpecificationContext ctx, ServerBootstrap bootstrap, int port, ConnectionHandler<I, O> connectionHandler, EventExecutorGroup requestProcessingExecutor, PipelineConfigurator<I, O> pipelineConfigurator) {
        super(bootstrap, port, connectionHandler, requestProcessingExecutor, pipelineConfigurator);
        this.ctx = ctx;
    }
}