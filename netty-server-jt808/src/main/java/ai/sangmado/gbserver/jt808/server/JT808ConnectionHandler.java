package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.common.channel.ConnectionHandler;
import ai.sangmado.gbserver.common.protocol.RequestHandler;

/**
 * JT808 连接管理器
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
@SuppressWarnings("FieldCanBeLocal")
public class JT808ConnectionHandler<I, O> implements ConnectionHandler<I, O> {

    private final RequestHandler<I, O> requestHandler;

    public JT808ConnectionHandler(RequestHandler<I, O> requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public void handle(Connection<I, O> newConnection) {
    }
}
