package ai.sangmado.gbserver.common.server;

import ai.sangmado.gbserver.common.channel.ConnectionHandler;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbserver.common.channel.ConnectionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 业务服务器配置类
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
public class ServerRequiredConfigurator<I, O> implements PipelineConfigurator<I, O> {

    private final ConnectionHandler<I, O> connectionHandler;
    private final ConnectionFactory<I, O> connectionFactory;
    private final ErrorHandler errorHandler;
    private final EventExecutorGroup connectionHandlingExecutor;

    public ServerRequiredConfigurator(
            ConnectionHandler<I, O> connectionHandler,
            ConnectionFactory<I, O> connectionFactory,
            ErrorHandler errorHandler) {
        this(connectionHandler, connectionFactory, errorHandler, null);
    }

    public ServerRequiredConfigurator(
            ConnectionHandler<I, O> connectionHandler,
            ConnectionFactory<I, O> connectionFactory,
            ErrorHandler errorHandler,
            EventExecutorGroup connectionHandlingExecutor) {
        this.connectionHandler = connectionHandler;
        this.connectionFactory = connectionFactory;
        this.errorHandler = errorHandler;
        this.connectionHandlingExecutor = connectionHandlingExecutor;
    }

    @Override
    public void configureNewPipeline(ChannelPipeline pipeline) {
        ChannelHandler lifecycleHandler = new ConnectionLifecycleHandler<I, O>(connectionHandler, connectionFactory, errorHandler);
        pipeline.addLast(connectionHandlingExecutor, "连接生命周期管理器", lifecycleHandler);
    }
}