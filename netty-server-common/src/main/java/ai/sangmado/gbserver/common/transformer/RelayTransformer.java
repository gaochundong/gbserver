package ai.sangmado.gbserver.common.transformer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class RelayTransformer implements ContentTransformer<ByteBuf> {

    public static final RelayTransformer DEFAULT_INSTANCE = new RelayTransformer();

    @Override
    public ByteBuf apply(ByteBuf byteBuf, ByteBufAllocator byteBufAllocator) {
        return byteBuf;
    }
}
