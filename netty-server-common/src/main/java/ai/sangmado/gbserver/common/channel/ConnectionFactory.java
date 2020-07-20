package ai.sangmado.gbserver.common.channel;

import io.netty.channel.Channel;

/**
 * 连接构造工厂
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
public interface ConnectionFactory<I, O> {

    Connection<I, O> newConnection(Channel channel);

    Connection<I, O> wrapClosedConnection(Channel closedChannel);
}