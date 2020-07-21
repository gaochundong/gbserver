package ai.sangmado.gbserver.common.server.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 连接状态
 */
@Getter
public enum ConnectionStatus {
    Closed(0, "已关闭"),
    Connected(1, "已连接"),
    ;

    @JsonInclude
    @JsonValue
    private Integer value;

    @JsonIgnore
    private String description;

    ConnectionStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public String toString() {
        return this.name();
    }

    private static final Map<Integer, ConnectionStatus> mapping = new HashMap<>();

    static {
        for (ConnectionStatus level : values()) {
            mapping.put(level.getValue(), level);
        }
    }

    @JsonCreator
    public static ConnectionStatus cast(int i) {
        ConnectionStatus level = mapping.get(i);
        if (level == null) {
            throw new IllegalArgumentException("Cannot cast integer to enum.");
        }
        return level;
    }

    public static ConnectionStatus parse(String s) {
        if (s == null || s.length() == 0) {
            return null;
        }
        for (ConnectionStatus level : values()) {
            if (level.name().equalsIgnoreCase(s)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Cannot parse string to enum.");
    }
}