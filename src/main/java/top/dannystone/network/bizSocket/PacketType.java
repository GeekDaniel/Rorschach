package top.dannystone.network.bizSocket;

/**
 * 命令枚举
 */
public enum PacketType {
    HEARTBEAT(100,"心跳"),
    BIZ_PACKACT(200, "消息传输")
    ;

    private int value;
    private String desc;

    PacketType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return this.value;
    }

    public String getDesc() {
        return desc;
    }

    public static PacketType fromValue(int value) {
        for (PacketType PacketType : values()) {
            if (PacketType.getValue() == value) {
                return PacketType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "PacketType{" +
                "value=" + value +
                ", desc='" + desc + '\'' +
                '}';
    }
}


