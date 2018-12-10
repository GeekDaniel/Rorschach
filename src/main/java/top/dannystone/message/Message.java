package top.dannystone.message;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-01
 * Time: 下午11:21
 */
@Data
public class Message {
    private String content;
    private int messageId;

    public Message getAckMessageByMessageId(int messageId) {
        Message ACKMESSAGE = new Message();
        ACKMESSAGE.setContent("");
        ACKMESSAGE.setMessageId(messageId);
        return ACKMESSAGE;
    }
}
