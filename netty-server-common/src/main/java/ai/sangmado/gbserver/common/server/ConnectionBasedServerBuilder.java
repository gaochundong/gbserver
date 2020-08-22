package ai.sangmado.gbserver.common.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 面向连接的服务器构造器
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 * @param <B> 面向连接的服务器构造器
 */
@SuppressWarnings({"rawtypes", "UnusedReturnValue"})
public abstract class ConnectionBasedServerBuilder<I, O, B extends ConnectionBasedServerBuilder>
        extends AbstractServerBuilder<I, O, ServerChannel, ServerBootstrap, B, ConnectionBasedServer<I, O>> {

    protected ConnectionBasedServerBuilder(int port, ConnectionHandler<I, O> connectionHandler) {
        this(port, connectionHandler, new ServerBootstrap());
    }

    protected ConnectionBasedServerBuilder(int port, ConnectionHandler<I, O> connectionHandler, ServerBootstrap bootstrap) {
        super(port, bootstrap, connectionHandler);
    }

    @Override
    public B defaultChannelOptions() {
        childChannelOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        return super.defaultChannelOptions();
    }

    public B eventLoops(EventLoopGroup acceptorGroup, EventLoopGroup workerGroup) {
        serverBootstrap.group(acceptorGroup, workerGroup);
        return returnBuilder();
    }

    public <T> B childChannelOption(ChannelOption<T> option, T value) {
        serverBootstrap.childOption(option, value);
        return returnBuilder();
    }

    @Override
    protected void configureDefaultEventLoopGroup() {
        EventLoopGroup acceptor = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        eventLoops(acceptor, worker);
    }

    @Override
    protected Class<? extends ServerChannel> defaultServerChannelClass() {
        return NioServerSocketChannel.class;
    }
}