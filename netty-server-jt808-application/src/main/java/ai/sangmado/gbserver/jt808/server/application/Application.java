package ai.sangmado.gbserver.jt808.server.application;

import ai.sangmado.gbprotocol.gbcommon.memory.PooledByteArrayFactory;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808DeviceRegistrationResult;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808ProtocolVersion;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacketBuilder;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808MessageContent;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.JT808_Message_Content_0x8100;
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
                Optional<Connection<JT808MessagePacket, JT808MessagePacket>> establishedConnection
                        = messageHandler.getEstablishedConnections().values().stream().findFirst();
                if (!establishedConnection.isPresent()) {
                    continue;
                }
                Connection<JT808MessagePacket, JT808MessagePacket> connection = establishedConnection.get();

                if (inputString.equals("0x8100")) {
                    JT808MessagePacket packet = create_JT808_Message_0x8100_packet(ctx);
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
        log.info("通过连接 [{}] 向设备发送消息, 协议版本[{}], 消息ID[{}]{}{}",
                connection.getConnectionId(),
                packet.getProtocolVersion().getName(), packet.getMessageId().getName(),
                System.lineSeparator(), json);
    }

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
}
