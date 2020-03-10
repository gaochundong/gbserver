package ai.sangmado.gbserver.common.transformer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.function.BiFunction;

public interface ContentTransformer<S> extends BiFunction<S, ByteBufAllocator, ByteBuf> {
}