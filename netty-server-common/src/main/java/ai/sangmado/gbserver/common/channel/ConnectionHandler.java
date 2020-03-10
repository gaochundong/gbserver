package ai.sangmado.gbserver.common.channel;

/**
 * 连接处理器
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
public interface ConnectionHandler<I, O> {

    void handle(Connection<I, O> newConnection);
}
