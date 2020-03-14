package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;

/**
 * JT808 业务请求处理器
 *
 * @param <I> 读取 JT808 业务消息
 * @param <O> 写入 JT808 业务消息
 */
public class JT808MessageHandler<I, O> implements IMessageHandler<I, O> {
    private final ISpecificationContext ctx;

    public JT808MessageHandler(ISpecificationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void handle(I request, O response) {

    }
}