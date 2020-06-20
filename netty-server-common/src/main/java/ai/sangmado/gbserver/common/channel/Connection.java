package ai.sangmado.gbserver.common.channel;

import io.netty.channel.Channel;

/**
 * 面向连接的抽象
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
@SuppressWarnings("unused")
public class Connection<I, O> extends DefaultChannelWriter<O> {

    protected Connection(Channel nettyChannel) {
        super(nettyChannel);
    }

    public String getConnectionId() {
        return this.getChannel().id().asLongText();
    }

    public boolean isActive() {
        return !this.isCloseIssued() && this.getChannel() != null && this.getChannel().isActive();
    }

    @Override
    public String toString() {
        return getConnectionId();
    }

    public static <I, O> Connection<I, O> create(final Channel channel) {
        return new Connection<>(channel);
    }
}
