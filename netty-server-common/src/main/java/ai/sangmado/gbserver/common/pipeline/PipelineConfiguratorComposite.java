package ai.sangmado.gbserver.common.pipeline;

import io.netty.channel.ChannelPipeline;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Netty的管道配置器组合
 *
 * @param <I> 管道输入项
 * @param <O> 管道输出项
 */
@SuppressWarnings("rawtypes")
public class PipelineConfiguratorComposite<I, O> implements PipelineConfigurator<I, O> {

    private static final PipelineConfigurator[] EMPTY_CONFIGURATORS = new PipelineConfigurator[0];

    private final PipelineConfigurator[] configurators;

    public PipelineConfiguratorComposite(PipelineConfigurator... configurators) {
        if (null == configurators) {
            configurators = EMPTY_CONFIGURATORS;
        }
        this.configurators = configurators;
    }

    @Override
    public void configureNewPipeline(ChannelPipeline pipeline) {
        for (PipelineConfigurator configurator : configurators) {
            if (configurator != null) {
                configurator.configureNewPipeline(pipeline);
            }
        }
    }

    public List<PipelineConfigurator> getConstituentConfigurators() {
        return Collections.unmodifiableList(Arrays.asList(configurators));
    }
}