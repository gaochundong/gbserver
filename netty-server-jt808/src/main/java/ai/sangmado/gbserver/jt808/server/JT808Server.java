package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbserver.common.protocol.RequestHandler;
import ai.sangmado.gbserver.common.server.ConnectionBasedServer;
import ai.sangmado.gbserver.common.channel.ConnectionHandler;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * JT808 业务服务器
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
public class JT808Server<I, O> extends ConnectionBasedServer<I, O> {

    public JT808Server(ServerBootstrap bootstrap, int port, RequestHandler<I, O> requestHandler) {
        this(bootstrap, port, requestHandler, null);
    }

    public JT808Server(ServerBootstrap bootstrap, int port, RequestHandler<I, O> requestHandler, EventExecutorGroup requestProcessingExecutor) {
        this(bootstrap, port, requestHandler, requestProcessingExecutor, null);
    }

    public JT808Server(ServerBootstrap bootstrap, int port, RequestHandler<I, O> requestHandler, EventExecutorGroup requestProcessingExecutor, PipelineConfigurator<I, O> pipelineConfigurator) {
        this(bootstrap, port, new JT808ConnectionHandler<>(requestHandler), requestProcessingExecutor, pipelineConfigurator);
    }

    protected JT808Server(ServerBootstrap bootstrap, int port, ConnectionHandler<I, O> connectionHandler, EventExecutorGroup requestProcessingExecutor, PipelineConfigurator<I, O> pipelineConfigurator) {
        super(bootstrap, port, connectionHandler, requestProcessingExecutor, pipelineConfigurator);
    }
}