package ai.sangmado.gbserver.common.channel;

import io.netty.channel.Channel;

public interface ConnectionFactory<I, O> {

    Connection<I, O> newConnection(Channel channel);
}