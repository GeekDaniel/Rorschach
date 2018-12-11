package top.dannystone.message;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-01
 * Time: 下午11:21
 */
@Data
@AllArgsConstructor
public class Message {
    private int messageId;
    private String content;

    public Message getAckMessageByMessageId(int messageId) {
        Message ACKMESSAGE = new Message(messageId,"");
        return ACKMESSAGE;
    }
}
