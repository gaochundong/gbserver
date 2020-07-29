package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbserver.common.server.ConnectionBasedServerBuilder;
import ai.sangmado.gbserver.common.server.ConnectionHandler;
import io.netty.bootstrap.ServerBootstrap;
import lombok.extern.slf4j.Slf4j;

/**
 * JT808 业务服务器构造器
 */
@Slf4j
@SuppressWarnings("FieldCanBeLocal")
public class JT808ServerBuilder extends ConnectionBasedServerBuilder<JT808Message, JT808Message, JT808ServerBuilder> {

    public JT808ServerBuilder(int port, ConnectionHandler<JT808Message, JT808Message> connectionHandler, PipelineConfigurator<JT808Message, JT808Message> pipelineConfigurator) {
        super(port, connectionHandler);
        this.pipelineConfigurator(pipelineConfigurator);
    }

    public JT808ServerBuilder(int port, ConnectionHandler<JT808Message, JT808Message> connectionHandler, PipelineConfigurator<JT808Message, JT808Message> pipelineConfigurator, ServerBootstrap bootstrap) {
        super(port, connectionHandler, bootstrap);
        this.pipelineConfigurator(pipelineConfigurator);
    }

    @Override
    protected JT808Server createServer() {
        return new JT808Server(serverBootstrap, port, connectionHandler, eventExecutorGroup, pipelineConfigurator);
    }

    @Override
    public JT808Server build() {
        return (JT808Server) super.build();
    }
}