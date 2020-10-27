package ai.sangmado.gbserver.jt808.server.application;

import ai.sangmado.gbprotocol.gbcommon.memory.PooledByteArrayFactory;
import ai.sangmado.gbprotocol.jt1078.protocol.enums.*;
import ai.sangmado.gbprotocol.jt1078.protocol.message.content.JT1078_Message_Content_0x9101;
import ai.sangmado.gbprotocol.jt1078.protocol.message.content.JT1078_Message_Content_0x9102;
import ai.sangmado.gbprotocol.jt1078.protocol.message.content.JT1078_Message_Content_0x9105;
import ai.sangmado.gbprotocol.jt1078.protocol.message.extension.JT1078MessageExtension;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.IVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808DeviceRegistrationResult;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808MessageId;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808PlatformCommonReplyResult;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808ProtocolVersion;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808Message;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessageAssembler;
import ai.sangmado.gbprotocol.jt808.protocol.message.content.*;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeader;
import ai.sangmado.gbprotocol.jt808.protocol.message.header.JT808MessageHeaderFactory;
import ai.sangmado.gbserver.common.channel.Connection;
import ai.sangmado.gbserver.jt808.server.*;
import ai.sangmado.gbserver.jt808.server.application.domain.JT808MessageConsumer;
import ai.sangmado.gbserver.jt808.server.application.domain.JT808MessageHandlerMapping;
import ai.sangmado.gbserver.jt808.server.application.domain.handler.jt1078.JT1078_Message_Handler_0x1206;
import ai.sangmado.gbserver.jt808.server.application.domain.handler.jt808.JT808_Message_Handler_0x0001;
import ai.sangmado.gbserver.jt808.server.application.domain.handler.jt808.JT808_Message_Handler_0x0100;
import ai.sangmado.gbserver.jt808.server.dispatch.JT808MessageDispatcher;
import ai.sangmado.gbserver.jt808.server.utils.GlobalSerialNumberIssuer;
import ai.sangmado.gbserver.jt808.server.utils.Jackson;
import com.google.common.base.Strings;
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
        // 协议上下文仅与协议报文序列化和反序列化过程相关
        ISpecificationContext ctx = JT808ProtocolSpecificationContext.newInstance()
                .withBufferPool(new PooledByteArrayFactory(512, 10));

        // 服务监听端口
        int port = 7200;

        // 加载JT1078协议消息扩展
        JT1078MessageExtension.extend();

        // 注册业务域消息处理器, 此处可应用IoC容器自动发现机制或者类反射扫描机制等进行处理器映射
        JT808MessageHandlerMapping messageHandlerMapping = new JT808MessageHandlerMapping();
        messageHandlerMapping.addHandler(new JT808_Message_Handler_0x0001(ctx));
        messageHandlerMapping.addHandler(new JT808_Message_Handler_0x0100(ctx));
        messageHandlerMapping.addHandler(new JT1078_Message_Handler_0x1206(ctx));

        // 构建服务器对象
        JT808MessageConsumer messageConsumer = new JT808MessageConsumer(messageHandlerMapping.getHandlers());
        JT808MessageDispatcher messageDispatcher = new JT808MessageDispatcher().bindSubscriber(messageConsumer);
        JT808ConnectionHandler connectionHandler = new JT808ConnectionHandler();
        JT808MessageProcessor messageProcessor = new JT808MessageProcessor(connectionHandler, messageDispatcher);
        JT808ServerPipelineConfigurator pipelineConfigurator = new JT808ServerPipelineConfigurator(ctx, messageProcessor);
        JT808ServerBuilder serverBuilder = new JT808ServerBuilder(port, connectionHandler, pipelineConfigurator);
        JT808Server server = serverBuilder.build();

        // 启动服务器
        server.start();
        log.info("服务器已启动");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String inputString = scanner.nextLine();
            log.info("输入参数: " + inputString);
            try {
                Optional<Connection<JT808Message, JT808Message>> establishedConnection =
                        connectionHandler.getEstablishedConnections().values().stream().findFirst();
                if (establishedConnection.isEmpty()) continue;
                Connection<JT808Message, JT808Message> connection = establishedConnection.get();
                IVersionedSpecificationContext newCtx = JT808ProtocolVersionedSpecificationContext.newInstance()
                        .withProtocolVersion(JT808ProtocolVersion.V2013)
                        .withByteOrder(ctx.getByteOrder())
                        .withCharset(ctx.getCharset())
                        .withBufferPool(ctx.getBufferPool());
                JT808Message packet = null;
                switch (inputString) {
                    case "0x8001": { // 平台通用应答
                        packet = create_JT808_Message_0x8001_packet(newCtx);
                        break;
                    }
                    case "0x8100": { // 平台终端注册应答
                        packet = create_JT808_Message_0x8100_packet(newCtx);
                        break;
                    }
                    case "0x8201": { // 平台位置信息查询
                        packet = create_JT808_Message_0x8201_packet(newCtx);
                        break;
                    }
                    case "0x8204": { // 平台终端链路检测指令
                        packet = create_JT808_Message_0x8204_packet(newCtx);
                        break;
                    }
                    case "0x9101": { // 平台下发实时音视频传输请求 - JT1078
                        packet = create_JT1078_Message_0x9101_packet(newCtx);
                        break;
                    }
                    case "0x9102": { // 平台下发音视频实时传输控制 - JT1078
                        packet = create_JT1078_Message_0x9102_packet(newCtx);
                        break;
                    }
                    case "0x9105": { // 平台下发实时音视频传输状态通知 - JT1078
                        packet = create_JT1078_Message_0x9105_packet(newCtx);
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

    private static void logPacket(Connection<JT808Message, JT808Message> connection, JT808Message packet) {
        String json = Jackson.toJsonPrettyString(packet);
        log.info("向设备发送消息, 消息ID[{}], 消息名称[{}], 协议版本[{}], 连接ID[{}]{}{}",
                packet.getMessageId().getName(),
                packet.getMessageId().getDescription(),
                packet.getProtocolVersion().getName(),
                connection.getConnectionId(),
                System.lineSeparator(), json);
    }

    // 平台通用应答
    private static JT808Message create_JT808_Message_0x8001_packet(IVersionedSpecificationContext ctx) {
        JT808MessageId messageId = JT808MessageId.JT808_Message_0x8001;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next();

        // 回复终端成功
        int ackSerialNumber = 2;
        JT808MessageId ackId = JT808MessageId.JT808_Message_0x0102;
        JT808PlatformCommonReplyResult result = JT808PlatformCommonReplyResult.Success;

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT808_Message_Content_0x8001.builder()
                .ackSerialNumber(ackSerialNumber)
                .ackId(ackId)
                .result(result)
                .build();

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 平台终端注册应答
    private static JT808Message create_JT808_Message_0x8100_packet(IVersionedSpecificationContext ctx) {
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

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 平台位置信息查询
    private static JT808Message create_JT808_Message_0x8201_packet(IVersionedSpecificationContext ctx) {
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

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 平台终端链路检测指令
    private static JT808Message create_JT808_Message_0x8204_packet(IVersionedSpecificationContext ctx) {
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

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 平台下发实时音视频传输请求
    private static JT808Message create_JT1078_Message_0x9101_packet(IVersionedSpecificationContext ctx) {
        JT1078MessageId messageId = JT1078MessageId.JT1078_Message_0x9101;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next();

        // 通过环境变量加载服务器参数
        final String ENV_JT1078_STREAM_SERVER_HOST = "JT1078_STREAM_SERVER_HOST";
        final String ENV_JT1078_STREAM_SERVER_TCP_PORT = "JT1078_STREAM_SERVER_TCP_PORT";
        final String ENV_JT1078_STREAM_SERVER_UDP_PORT = "JT1078_STREAM_SERVER_UDP_PORT";

        String envServerIPAddress = System.getenv(ENV_JT1078_STREAM_SERVER_HOST);
        String serverIPAddress = !Strings.isNullOrEmpty(envServerIPAddress) ? envServerIPAddress : "127.0.0.1";
        String envServerTcpPort = System.getenv(ENV_JT1078_STREAM_SERVER_TCP_PORT);
        int serverTcpPort = !Strings.isNullOrEmpty(envServerTcpPort) ? Integer.parseInt(envServerTcpPort) : 0;
        String envServerUdpPort = System.getenv(ENV_JT1078_STREAM_SERVER_UDP_PORT);
        int serverUdpPort = !Strings.isNullOrEmpty(envServerUdpPort) ? Integer.parseInt(envServerUdpPort) : 0;

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT1078_Message_Content_0x9101.builder()
                .streamingServerIPAddressLength(serverIPAddress.length())
                .streamingServerIPAddress(serverIPAddress)
                .streamingServerTcpPort(serverTcpPort)
                .streamingServerUdpPort(serverUdpPort)
                .logicalChannelNumber(LogicalChannelNumber.Channel1)
                .streamingDataType(StreamingDataType.AV)
                .channelStreamType(ChannelStreamType.Main)
                .build();

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 平台下发音视频实时传输控制
    private static JT808Message create_JT1078_Message_0x9102_packet(IVersionedSpecificationContext ctx) {
        JT1078MessageId messageId = JT1078MessageId.JT1078_Message_0x9102;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next();

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT1078_Message_Content_0x9102.builder()
                .logicalChannelNumber(LogicalChannelNumber.Channel1)
                .channelControlCommand(ChannelControlCommand.CloseChannel)
                .channelCloseKind(ChannelCloseKind.CloseChannel)
                .channelSwitchStreamKind(ChannelSwitchStreamKind.Main)
                .build();

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }

    // 平台下发实时音视频传输状态通知
    private static JT808Message create_JT1078_Message_0x9105_packet(IVersionedSpecificationContext ctx) {
        JT1078MessageId messageId = JT1078MessageId.JT1078_Message_0x9105;
        String phoneNumber = "861064602988";
        int serialNumber = GlobalSerialNumberIssuer.next();

        JT808MessageHeader header = JT808MessageHeaderFactory
                .buildWith(ctx)
                .withMessageId(messageId)
                .withPhoneNumber(phoneNumber)
                .withSerialNumber(serialNumber);
        JT808MessageContent content = JT1078_Message_Content_0x9105.builder()
                .logicalChannelNumber(LogicalChannelNumber.Channel1)
                .packetLossRate(400)
                .build();

        List<JT808Message> messages = JT808MessageAssembler.assemble(ctx, header, content);
        return messages.get(0);
    }
}
