package ai.sangmado.gbserver.common.channel;

import io.netty.channel.Channel;

/**
 * 连接构造工厂
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
public class UnpooledConnectionFactory<I, O> implements ConnectionFactory<I, O> {

    public UnpooledConnectionFactory() {
    }

    @Override
    public Connection<I, O> newConnection(Channel channel) {
        return Connection.create(channel);
    }
}