package ai.sangmado.gbserver.common.protocol;

/**
 * 业务请求处理器
 *
 * @param <I> 业务请求
 * @param <O> 业务回复
 */
public interface RequestHandler<I, O> {

    void handle(I request, O response);
}