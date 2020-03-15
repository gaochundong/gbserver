package ai.sangmado.gbserver.common.server;

import ai.sangmado.gbserver.common.channel.ConnectionFactory;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 业务服务器管道配置
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
public class ServerRequiredConfigurator<I, O> implements PipelineConfigurator<I, O> {
    public static final String CONNECTION_LIFECYCLE_HANDLER_NAME = "连接生命周期管理器";

    private final ConnectionHandler<I, O> connectionHandler;
    private final ConnectionFactory<I, O> connectionFactory;
    private final EventExecutorGroup connectionHandlingExecutor;

    public ServerRequiredConfigurator(
            ConnectionHandler<I, O> connectionHandler,
            ConnectionFactory<I, O> connectionFactory) {
        this(connectionHandler, connectionFactory, null);
    }

    public ServerRequiredConfigurator(
            ConnectionHandler<I, O> connectionHandler,
            ConnectionFactory<I, O> connectionFactory,
            EventExecutorGroup connectionHandlingExecutor) {
        this.connectionHandler = connectionHandler;
        this.connectionFactory = connectionFactory;
        this.connectionHandlingExecutor = connectionHandlingExecutor;
    }

    @Override
    public void configureNewPipeline(ChannelPipeline pipeline) {
        ChannelHandler lifecycleHandler = new ConnectionLifecycleHandler<>(connectionHandler, connectionFactory);
        pipeline.addLast(connectionHandlingExecutor, CONNECTION_LIFECYCLE_HANDLER_NAME, lifecycleHandler);
    }
}