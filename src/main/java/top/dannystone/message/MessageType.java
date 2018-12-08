package top.dannystone.message;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-04
 * Time: 下午6:29
 */
public enum MessageType {
    HEARTBEAT(1,"心跳"),
    REGISTER(2,"订阅注册"),
    MESSAGE_DELIVER(3,"业务消息"),
    ACK(4,"消息确认")
    ;

    private int code;
    private String dsc;

    private MessageType(int code, String dsc) {
        this.code = code;
        this.dsc = dsc;
    }

    public int getCode() {
        return code;
    }

    public String getDsc() {
        return dsc;
    }
}
