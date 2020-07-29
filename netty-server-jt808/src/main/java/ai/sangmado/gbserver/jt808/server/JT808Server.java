package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbserver.common.server.ConnectionBasedServer;
import ai.sangmado.gbserver.common.server.ConnectionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

/**
 * JT808 业务服务器
 */
@Slf4j
@SuppressWarnings("FieldCanBeLocal")
public class JT808Server extends ConnectionBasedServer<JT808Message, JT808Message> {

    public JT808Server(ServerBootstrap bootstrap, int port, ConnectionHandler<JT808Message, JT808Message> connectionHandler) {
        this(bootstrap, port, connectionHandler, null, null);
    }

    public JT808Server(ServerBootstrap bootstrap, int port, ConnectionHandler<JT808Message, JT808Message> connectionHandler, EventExecutorGroup requestProcessingExecutor, PipelineConfigurator<JT808Message, JT808Message> pipelineConfigurator) {
        super(bootstrap, port, connectionHandler, requestProcessingExecutor, pipelineConfigurator);
    }
}