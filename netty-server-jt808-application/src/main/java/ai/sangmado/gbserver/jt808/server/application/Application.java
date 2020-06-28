package ai.sangmado.gbserver.jt808.server.application;

import ai.sangmado.gbprotocol.gbcommon.memory.PooledByteArrayFactory;
import ai.sangmado.gbprotocol.jt1078.protocol.enums.JT1078MessageId;
import ai.sangmado.gbprotocol.jt1078.protocol.message.content.JT1078_Message_Content_0x9105;
import ai.sangmado.gbprotocol.jt1078.protocol.message.extension.JT1078MessageExtension;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808DeviceRegistrationResult;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808ProtocolVersion;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacketBuilder;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808MessageContent;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x8100;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x8201;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x8204;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeader;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeaderFactory;
import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.jt808.server.*;
import ai.sangmado.gbserver.jt808.server.utils.GlobalSerialNumberIssuer;
import ai.sangmado.gbserver.jt808.server.utils.Jackson;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * JT808 业务服务器应用程序
 */
@Slf4j
@SuppressWarnings("InfiniteLoopStatement")
public class Application {

    public static void main(String[] args) {
        ISpecificationContext ctx = new JT808ProtocolSpecificationContext()
                .withProtocolVersion(JT808ProtocolVersion.V2013)
                .withBufferPool(new PooledByteArrayFactory(512, 10));

        // 加载 JT1078 协议消息扩展
        JT1078MessageExtension.extend();

        int port = 7200;

        JT808MessageHandler<JT808MessagePacket, JT808MessagePacket> messageHandler = new JT808MessageHandler<>(ctx);
        JT808ConnectionHandler<JT808MessagePacket, JT808MessagePacket> connectionHandler = new JT808ConnectionHandler<>(ctx, messageHandler);
        JT808ServerPipelineConfigurator<JT808MessagePacket, JT808MessagePacket> pipelineConfigurator = new JT808ServerPipelineConfigurator<>(ctx, messageHandler);
        JT808ServerBuilder<JT808MessagePacket, JT808MessagePacket> serverBuilder = new JT808ServerBuilder<>(ctx, port, connectionHandler, pipelineConfigurator);
        JT808Server<JT808MessagePacket, JT808MessagePacket> server = serverBuilder.build();

        server.start();
        log.info("服务器已启动");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String inputString = scanner.nextLine();
            log.info("输入参数: " + inputString);
            try {
                Optional<Connection<JT808MessagePacket, JT808MessagePacket>> establishedConnection =
                        messageHandler.getEstablishedConnections().values().stream().findFirst();
                if (!establishedConnection.isPresent()) continue;
                Connection<JT808MessagePacket, JT808MessagePacket> connection = establishedConnection.get();
                JT808MessagePacket packet = null;
                switch (inputString) {
                    case "0x8100": { // 平台终端注册应答
                        packet = create_JT808_Message_0x8100_packet(ctx);
                        break;
                    }
                    case "0x8201": { // 平台位置信息查询
                        packet = create_JT808_Message_0x8201_packet(ctx);
                        break;
                    }
                    case "0x8204": { // 平台终端链路检测指令
                        packet = create_JT808_Message_0x8204_packet(ctx);
                        break;
                    }
                    case "0x9105": { // 平台下发实时音视频传输状态通知 - JT1078
                        packet = create_JT1078_Message_0x9105_packet(ctx);
                        break;
                    }
                }
                if (packet != null && connection.isActive()) {
                    logPacket(connection, packet);
                    connection.writeAndFlush(packet);
                }
            } catch (Exception ex) {
                log.error("向设备发送消息失败", ex);
            }
        }
    }

    private static void logPacket(Connection<JT808MessagePacket, JT808MessagePacket> connection, JT808MessagePacket packet) {
        String json = Jackson.toJsonPrettyString(packet);
        log.info("向设备发送消息, 消息ID[{}], 消息名称[{}], 协议版本[{}], 连接ID[{}]{}{}",
                packet.getMessageId().getName(),
                packet.getMessageId().getDescription(),
                packet.getProtocolVersion().getName(),
                connection.getConnectionId(),
                System.lineSeparator(), json);
    }

    // 平台终端注册应答
    private static JT808MessagePacket create_JT808_Message_0x8100_packet(ISpecificationContext ctx) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x8100;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next();

        JT808DeviceRegistrationResult registrationResult = JT808DeviceRegistrationResult.Success;
        String authCode = "6eaf001e-b543-11ea-a56b-02641672dd7e";
        int ackSerialNumber = GlobalSerialNumberIssuer.next();

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT808_Message_Content_0x8100.builder()
                .registrationResult(registrationResult)
                .authCode(authCode)
                .ackSerialNumber(ackSerialNumber)
                .build();

        List<JT808MessagePacket> packets = JT808MessagePacketBuilder.buildPackets(ctx, header, content);
        return packets.get(0);
    }

    // 平台位置信息查询
    private static JT808MessagePacket create_JT808_Message_0x8201_packet(ISpecificationContext ctx) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x8201;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next();

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT808_Message_Content_0x8201.builder()
                .build();

        List<JT808MessagePacket> packets = JT808MessagePacketBuilder.buildPackets(ctx, header, content);
        return packets.get(0);
    }

    // 平台终端链路检测指令
    private static JT808MessagePacket create_JT808_Message_0x8204_packet(ISpecificationContext ctx) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x8204;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next();

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT808_Message_Content_0x8204.builder()
                .build();

        List<JT808MessagePacket> packets = JT808MessagePacketBuilder.buildPackets(ctx, header, content);
        return packets.get(0);
    }

    // 平台下发实时音视频传输状态通知
    private static JT808MessagePacket create_JT1078_Message_0x9105_packet(ISpecificationContext ctx) {
        JT1078MessageId messageId = JT1078MessageId.JT1078_Message_0x9105;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next();

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT1078_Message_Content_0x9105.builder()
                .logicalChannelNumber(3)
                .packetLossRate(400)
                .build();

        List<JT808MessagePacket> packets = JT808MessagePacketBuilder.buildPackets(ctx, header, content);
        return packets.get(0);
    }
}
