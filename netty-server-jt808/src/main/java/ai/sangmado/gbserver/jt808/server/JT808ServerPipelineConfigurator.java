package ai.sangmado.gbserver.jt808.server;

import ai.sangmado.gbcodec.jt808.codec.JT808DelimiterBasedFrameDecoder;
import ai.sangmado.gbcodec.jt808.codec.JT808MessageCodec;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbserver.common.pipeline.PipelineConfigurator;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;

/**
 * JT808 服务器管道配置
 */
@Slf4j
public class JT808ServerPipelineConfigurator implements PipelineConfigurator<JT808Message, JT808Message> {
    private final ISpecificationContext ctx;
    private final JT808MessageProcessor messageProcessor;

    public JT808ServerPipelineConfigurator(ISpecificationContext ctx, JT808MessageProcessor messageProcessor) {
        this.ctx = ctx;
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void configureNewPipeline(ChannelPipeline pipeline) {
        pipeline.addLast("JT808消息分割器", new JT808DelimiterBasedFrameDecoder());
        pipeline.addLast("JT808消息编解码器", new JT808MessageCodec<>(ctx, JT808Message::new));
        pipeline.addLast("JT808消息处理器", messageProcessor);
    }
}
