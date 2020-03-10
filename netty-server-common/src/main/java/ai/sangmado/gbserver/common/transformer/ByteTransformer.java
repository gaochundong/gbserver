package ai.sangmado.gbserver.common.transformer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class ByteTransformer implements ContentTransformer<byte[]> {

    public static final ByteTransformer DEFAULT_INSTANCE = new ByteTransformer();

    @Override
    public ByteBuf apply(byte[] bytes, ByteBufAllocator byteBufAllocator) {
        return byteBufAllocator.buffer(bytes.length).writeBytes(bytes);
    }
}