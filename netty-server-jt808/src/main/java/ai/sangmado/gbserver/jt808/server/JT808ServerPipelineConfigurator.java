package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbcodec.jt808.codec.JT808MessageCodec;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import io.netty.channel.ChannelPipeline;

/**
 * JT808 服务器管道配置
 */
public class JT808ServerPipelineConfigurator<I extends JT808MessagePacket, O extends JT808MessagePacket> implements PipelineConfigurator<I, O> {
    private final ISpecificationContext ctx;
    private final JT808MessageHandler<I, O> messageHandler;

    public JT808ServerPipelineConfigurator(ISpecificationContext ctx, JT808MessageHandler<I, O> messageHandler) {
        this.ctx = ctx;
        this.messageHandler = messageHandler;
    }

    @Override
    public void configureNewPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new JT808MessageCodec(ctx));
        pipeline.addLast(messageHandler);
    }
}
