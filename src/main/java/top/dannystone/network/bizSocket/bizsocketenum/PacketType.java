package top.dannystone.network.bizSocket.bizsocketenum;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-12-08
 * Time: 下午3:37
 */
public enum PacketType {
    HEARTBEAT(1, "心跳"),
    BIZ_PACKACT(2,"业务包");

    PacketType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
