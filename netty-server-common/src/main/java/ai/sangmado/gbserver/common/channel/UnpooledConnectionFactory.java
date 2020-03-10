package ai.sangmado.gbserver.common.channel;

import io.netty.channel.Channel;

public class UnpooledConnectionFactory<I, O> implements ConnectionFactory<I, O> {

    public UnpooledConnectionFactory() {
    }

    @Override
    public Connection<I, O> newConnection(Channel channel) {
        return Connection.create(channel);
    }
}