package ai.sangmado.gbserver.common.server;

/**
 * 异常处理器
 */
public interface ErrorHandler {

    void handleError(Throwable throwable);
}
