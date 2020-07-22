package ai.sangmado.gbserver.common.server;

import ai.sangmado.gbserver.common.channel.UnpooledConnectionFactory;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbserver.common.pipeline.PipelineConfiguratorComposite;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 业务服务器抽象类
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 * @param <B> 面向连接的服务器构造器
 * @param <C> Netty通道
 * @param <S> 抽象业务服务器
 */
@Slf4j
@SuppressWarnings({"rawtypes", "ResultOfMethodCallIgnored", "UnusedReturnValue"})
public abstract class AbstractServer<I, O, B extends AbstractBootstrap<B, C>, C extends Channel, S extends AbstractServer> {

    protected enum ServerState {Created, Starting, Started, Shutdown}

    protected final B bootstrap;
    protected final int port;
    protected final AtomicReference<ServerState> state;
    private ChannelFuture bindFuture;

    protected final UnpooledConnectionFactory<I, O> connectionFactory;

    protected AbstractServer(B bootstrap, int port) {
        if (bootstrap == null) throw new NullPointerException("Bootstrap can not be null.");

        this.state = new AtomicReference<>(ServerState.Created);
        this.bootstrap = bootstrap;
        this.port = port;

        this.connectionFactory = new UnpooledConnectionFactory<>();
    }

    @SuppressWarnings("unchecked")
    protected S returnServer() {
        return (S) this;
    }

    public void startAndWait() {
        start();
        try {
            waitTillShutdown();
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }

    public S start() {
        if (!state.compareAndSet(ServerState.Created, ServerState.Starting)) {
            throw new IllegalStateException("服务器已经启动");
        }

        try {
            bindFuture = bootstrap.bind(port).sync();
            if (!bindFuture.isSuccess()) {
                throw new RuntimeException(bindFuture.cause());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        state.set(ServerState.Started);

        return returnServer();
    }

    public void shutdown() throws InterruptedException {
        if (!state.compareAndSet(ServerState.Started, ServerState.Shutdown)) {
            throw new IllegalStateException("服务器已经被关闭");
        } else {
            bindFuture.channel().close().sync();
        }
    }

    public void waitTillShutdown() throws InterruptedException {
        ServerState serverState = state.get();
        switch (serverState) {
            case Created:
            case Starting:
                throw new IllegalStateException("服务器还未启动");
            case Started:
                bindFuture.channel().closeFuture().await();
                break;
            case Shutdown:
                break;
        }
    }

    public void waitTillShutdown(long duration, TimeUnit timeUnit) throws InterruptedException {
        ServerState serverState = state.get();
        switch (serverState) {
            case Created:
            case Starting:
                throw new IllegalStateException("服务器还未启动");
            case Started:
                bindFuture.channel().closeFuture().await(duration, timeUnit);
                break;
            case Shutdown:
                break;
        }
    }

    public int getPort() {
        if (null != bindFuture && bindFuture.isDone()) {
            SocketAddress localAddress = bindFuture.channel().localAddress();
            if (localAddress instanceof InetSocketAddress) {
                return ((InetSocketAddress) localAddress).getPort();
            }
        }
        return port;
    }

    protected ChannelInitializer<Channel> newChannelInitializer(
            final PipelineConfigurator<I, O> pipelineConfigurator,
            final ConnectionHandler<I, O> connectionHandler,
            final EventExecutorGroup connHandlingExecutor) {
        return new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ServerRequiredConfigurator<I, O> serverRequiredConfigurator =
                        new ServerRequiredConfigurator<>(connectionHandler, connectionFactory, connHandlingExecutor);
                PipelineConfigurator<I, O> configurator;
                if (pipelineConfigurator == null) {
                    configurator = serverRequiredConfigurator;
                } else {
                    configurator = new PipelineConfiguratorComposite<>(pipelineConfigurator, serverRequiredConfigurator);
                }
                configurator.configureNewPipeline(ch.pipeline());
            }
        };
    }
}
