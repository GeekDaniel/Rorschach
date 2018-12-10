package top.dannystone.message;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-04
 * Time: 下午6:29
 */
public enum Operation {
    PRODUCE(1,"生产消息"),
    CONSUME(2,"消费消息"),
    REGISTER(3,"注册订阅"),
    ACK(4,"消息确认")
    ;

    private int code;
    private String dsc;

    private Operation(int code, String dsc) {
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
