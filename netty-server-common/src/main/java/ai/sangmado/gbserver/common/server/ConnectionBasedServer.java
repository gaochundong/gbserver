package ai.sangmado.gbserver.common.server;

import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ServerChannel;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 面向连接的服务器
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
public class ConnectionBasedServer<I, O> extends AbstractServer<I, O, ServerBootstrap, ServerChannel, ConnectionBasedServer<I, O>> {

    protected final PipelineConfigurator<I, O> pipelineConfigurator;

    public ConnectionBasedServer(ServerBootstrap bootstrap, int port, ConnectionHandler<I, O> connectionHandler) {
        this(bootstrap, port, connectionHandler, null);
    }

    public ConnectionBasedServer(ServerBootstrap bootstrap, int port, ConnectionHandler<I, O> connectionHandler, EventExecutorGroup eventExecutorGroup) {
        this(bootstrap, port, connectionHandler, eventExecutorGroup, null);
    }

    public ConnectionBasedServer(ServerBootstrap bootstrap, int port, ConnectionHandler<I, O> connectionHandler, EventExecutorGroup eventExecutorGroup, PipelineConfigurator<I, O> pipelineConfigurator) {
        super(bootstrap, port);
        this.pipelineConfigurator = pipelineConfigurator;

        // 配置通道处理器用于响应连接请求
        bootstrap.childHandler(newChannelInitializer(pipelineConfigurator, connectionHandler, eventExecutorGroup));
    }
}