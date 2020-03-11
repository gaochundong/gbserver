package ai.sangmado.gbserver.jt808.server;

/**
 * 业务请求处理器
 *
 * @param <I> 业务请求
 * @param <O> 业务回复
 */
public interface IMessageHandler<I, O> {

    void handle(I request, O response);
}