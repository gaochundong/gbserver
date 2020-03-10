package ai.sangmado.gbserver.common.pipeline;

import io.netty.channel.ChannelPipeline;

/**
 * Netty的管道配置器
 *
 * @param <I> 管道输入项
 * @param <O> 管道输出项
 */
public interface PipelineConfigurator<I, O> {

    void configureNewPipeline(ChannelPipeline pipeline);
}