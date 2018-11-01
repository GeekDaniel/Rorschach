package top.dannystone.network;

/**
 * 命令枚举
 */
public enum MessageCmd {
    HEARTBEAT(100,"心跳"),
    MESSAGE_TRANSFOR(200, "消息传输")
    ;

    private int value;
    private String desc;

    MessageCmd(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return this.value;
    }

    public String getDesc() {
        return desc;
    }

    public static MessageCmd fromValue(int value) {
        for (MessageCmd MessageCmd : values()) {
            if (MessageCmd.getValue() == value) {
                return MessageCmd;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "MessageCmd{" +
                "value=" + value +
                ", desc='" + desc + '\'' +
                '}';
    }
}


