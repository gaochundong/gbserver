package ai.sangmado.gbserver.common.channel;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

/**
 * 连接构造工厂
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
@Slf4j
public class UnpooledConnectionFactory<I, O> implements ConnectionFactory<I, O> {

    public UnpooledConnectionFactory() {
    }

    @Override
    public Connection<I, O> newConnection(Channel channel) {
        log.info("创建新连接, remoteAddress[{}]", channel.remoteAddress());
        return Connection.create(channel);
    }

    @Override
    public Connection<I, O> wrapClosedConnection(Channel closedChannel) {
        log.info("封装已关闭通道连接, remoteAddress[{}]", closedChannel.remoteAddress());
        return Connection.create(closedChannel);
    }
}