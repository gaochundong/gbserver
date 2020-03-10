package ai.sangmado.gbserver.common.server;

import ai.sangmado.gbserver.common.channel.ConnectionHandler;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * 服务器构造器抽象类
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 * @param <C> Netty通道
 * @param <T> Netty服务引导器
 * @param <B> 面向连接的服务器构造器
 * @param <S> 抽象业务服务器
 */
@SuppressWarnings({"rawtypes", "UnusedReturnValue"})
public abstract class AbstractServerBuilder<
        I,
        O,
        C extends Channel,
        T extends AbstractBootstrap<T, C>,
        B extends AbstractServerBuilder,
        S extends AbstractServer<I, O, T, C, S>> {

    protected final int port;
    protected final T serverBootstrap;
    protected final ConnectionHandler<I, O> connectionHandler;

    protected Class<? extends C> serverChannelClass;
    protected PipelineConfigurator<I, O> pipelineConfigurator;
    protected EventExecutorGroup eventExecutorGroup;

    protected AbstractServerBuilder(int port, T bootstrap, ConnectionHandler<I, O> connectionHandler) {
        if (connectionHandler == null) throw new IllegalArgumentException("Connection handler can not be null");
        if (bootstrap == null) throw new IllegalArgumentException("Server bootstrap can not be null");
        this.port = port;
        serverBootstrap = bootstrap;
        this.connectionHandler = connectionHandler;
        defaultChannelOptions();
    }

    protected abstract void configureDefaultEventLoopGroup();

    protected abstract Class<? extends C> defaultServerChannelClass();

    protected abstract S createServer();

    public B defaultChannelOptions() {
        channelOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return returnBuilder();
    }

    public <P> B channelOption(ChannelOption<P> option, P value) {
        serverBootstrap.option(option, value);
        return returnBuilder();
    }

    public B eventLoop(EventLoopGroup singleGroup) {
        serverBootstrap.group(singleGroup);
        return returnBuilder();
    }

    public B pipelineConfigurator(PipelineConfigurator<I, O> pipelineConfigurator) {
        this.pipelineConfigurator = pipelineConfigurator;
        return returnBuilder();
    }

    public B withEventExecutorGroup(EventExecutorGroup eventExecutorGroup) {
        this.eventExecutorGroup = eventExecutorGroup;
        return returnBuilder();
    }

    public B channel(Class<? extends C> serverChannelClass) {
        this.serverChannelClass = serverChannelClass;
        return returnBuilder();
    }

    @SuppressWarnings("unchecked")
    protected B returnBuilder() {
        return (B) this;
    }

    public PipelineConfigurator<I, O> getPipelineConfigurator() {
        return pipelineConfigurator;
    }

    public S build() {
        if (serverChannelClass == null) {
            serverChannelClass = defaultServerChannelClass();
            EventLoopGroup acceptorGroup = serverBootstrap.config().group();
            if (acceptorGroup == null) {
                configureDefaultEventLoopGroup();
            }
        }

        if (serverBootstrap.config().group() == null) {
            if (defaultServerChannelClass() == serverChannelClass) {
                configureDefaultEventLoopGroup();
            } else {
                throw new IllegalStateException("Specified a channel class but not the event loop group.");
            }
        }

        serverBootstrap.channel(serverChannelClass);

        return createServer();
    }
}
