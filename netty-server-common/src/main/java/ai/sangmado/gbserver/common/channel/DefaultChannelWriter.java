package ai.sangmado.gbserver.common.channel;

import ai.sangmado.gbserver.common.transformer.ByteTransformer;
import ai.sangmado.gbserver.common.transformer.ContentTransformer;
import ai.sangmado.gbserver.common.transformer.RelayTransformer;
import ai.sangmado.gbserver.common.transformer.StringTransformer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import lombok.SneakyThrows;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 通道数据写入器
 *
 * @param <O> 写入通道的业务对象
 */
public class DefaultChannelWriter<O> implements ChannelWriter<O> {

    private final Channel nettyChannel;
    protected final AtomicBoolean closeIssued = new AtomicBoolean();

    protected DefaultChannelWriter(Channel nettyChannel) {
        if (nettyChannel == null) throw new NullPointerException("Channel can not be null.");
        this.nettyChannel = nettyChannel;
    }

    public Channel getChannel() {
        return nettyChannel;
    }

    @SneakyThrows
    protected void writeOnChannel(Object msg) {
        getChannel().write(msg).await();
    }

    private <R> void write(R msg, ContentTransformer<R> transformer) {
        ByteBuf contentBytes = transformer.apply(msg, getAllocator());
        writeOnChannel(contentBytes);
    }

    @Override
    public void write(O msg) {
        writeOnChannel(msg);
    }

    @Override
    public void writeBytes(ByteBuf msg) {
        write(msg, RelayTransformer.DEFAULT_INSTANCE);
    }

    @Override
    public void writeBytes(byte[] msg) {
        write(msg, ByteTransformer.DEFAULT_INSTANCE);
    }

    @Override
    public void writeString(String msg) {
        write(msg, new StringTransformer());
    }

    @Override
    public void writeAndFlush(O msg) {
        write(msg);
        flush();
    }

    @Override
    public void writeBytesAndFlush(ByteBuf msg) {
        writeBytes(msg);
        flush();
    }

    @Override
    public void writeBytesAndFlush(byte[] msg) {
        write(msg, ByteTransformer.DEFAULT_INSTANCE);
        flush();
    }

    @Override
    public void writeStringAndFlush(String msg) {
        write(msg, new StringTransformer());
        flush();
    }

    public boolean isCloseIssued() {
        return closeIssued.get();
    }

    @SneakyThrows
    protected void _close(boolean flush) {
        getChannel().close().await();
    }

    @Override
    public void close() {
        close(false);
    }

    @Override
    public void close(boolean flush) {
        if (closeIssued.compareAndSet(false, true)) {
            _close(flush);
        }
    }

    @Override
    public void flush() {
        nettyChannel.flush();
    }

    @Override
    public ByteBufAllocator getAllocator() {
        return nettyChannel.alloc();
    }
}
