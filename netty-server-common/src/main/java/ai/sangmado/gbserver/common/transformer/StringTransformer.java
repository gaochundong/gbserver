package ai.sangmado.gbserver.common.transformer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringTransformer implements ContentTransformer<String> {

    public static final StringTransformer DEFAULT_INSTANCE = new StringTransformer();

    private final Charset charset;

    public StringTransformer() {
        this(StandardCharsets.UTF_8);
    }

    public StringTransformer(Charset charset) {
        this.charset = charset;
    }

    @Override
    public ByteBuf apply(String s, ByteBufAllocator byteBufAllocator) {
        byte[] contentAsBytes = s.getBytes(charset);
        return byteBufAllocator.buffer(contentAsBytes.length).writeBytes(contentAsBytes);
    }
}
