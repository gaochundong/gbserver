package ai.sangmado.gbserver.common.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * 通道数据写入器
 *
 * @param <O> 写入通道的业务对象
 */
public interface ChannelWriter<O> {

    void write(O msg);

    void writeBytes(ByteBuf msg);

    void writeBytes(byte[] msg);

    void writeString(String msg);

    void writeAndFlush(O msg);

    void writeBytesAndFlush(ByteBuf msg);

    void writeBytesAndFlush(byte[] msg);

    void writeStringAndFlush(String msg);

    void flush();

    void close();

    void close(boolean flush);

    ByteBufAllocator getAllocator();
}
