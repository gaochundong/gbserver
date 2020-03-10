package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbserver.common.protocol.RequestHandler;
import ai.sangmado.gbserver.common.server.ConnectionBasedServerBuilder;
import io.netty.bootstrap.ServerBootstrap;

/**
 * JT808 业务服务器构造器
 *
 * @param <I> 读取连接通道的业务对象
 * @param <O> 写入连接通道的业务对象
 */
public class JT808ServerBuilder<I, O> extends ConnectionBasedServerBuilder<I, O, JT808ServerBuilder<I, O>> {

    public JT808ServerBuilder(int port, RequestHandler<I, O> requestHandler) {
        super(port, new JT808ConnectionHandler<>(requestHandler));
    }

    public JT808ServerBuilder(int port, RequestHandler<I, O> requestHandler, ServerBootstrap bootstrap) {
        super(port, new JT808ConnectionHandler<>(requestHandler), bootstrap);
    }

    @Override
    protected JT808Server<I, O> createServer() {
        return new JT808Server<>(serverBootstrap, port, connectionHandler, eventExecutorGroup, pipelineConfigurator);
    }

    @Override
    public JT808Server<I, O> build() {
        return (JT808Server<I, O>) super.build();
    }
}