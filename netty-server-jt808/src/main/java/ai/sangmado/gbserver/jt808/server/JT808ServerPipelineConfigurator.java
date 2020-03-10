package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbcodec.jt808.codec.JT808MessageCodec;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import io.netty.channel.ChannelPipeline;

/**
 * JT808 服务器管道配置
 *
 * @param <I> 业务请求
 * @param <O> 业务回复
 */
public class JT808ServerPipelineConfigurator<I, O> implements PipelineConfigurator<I, O> {
    private final ISpecificationContext ctx;

    public JT808ServerPipelineConfigurator(ISpecificationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void configureNewPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new JT808MessageCodec(ctx));
    }
}
