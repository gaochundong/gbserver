package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import ai.sangmado.gbserver.common.server.ConnectionBasedServer;
import ai.sangmado.gbserver.common.server.ConnectionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * JT808 业务服务器
 */
@SuppressWarnings("FieldCanBeLocal")
public class JT808Server<I extends JT808MessagePacket, O extends JT808MessagePacket> extends ConnectionBasedServer<I, O> {
    private final ISpecificationContext ctx;

    public JT808Server(ISpecificationContext ctx, ServerBootstrap bootstrap, int port, ConnectionHandler<I, O> connectionHandler) {
        this(ctx, bootstrap, port, connectionHandler, null, null);
    }

    public JT808Server(ISpecificationContext ctx, ServerBootstrap bootstrap, int port, ConnectionHandler<I, O> connectionHandler, EventExecutorGroup requestProcessingExecutor, PipelineConfigurator<I, O> pipelineConfigurator) {
        super(bootstrap, port, connectionHandler, requestProcessingExecutor, pipelineConfigurator);
        this.ctx = ctx;
    }
}