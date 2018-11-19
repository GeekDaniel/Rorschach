package top.dannystone.message;
import top.dannystone.message.MessageType;

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
public class Message {
    private String message;
    private MessageType messageType;
    private int messageId;

    public Message getAckMessageByMessageId(int messageId){
        Message ACKMESSAGE=new Message();
        ACKMESSAGE.setMessage("");
        ACKMESSAGE.setMessageType(MessageType.ACK);
        ACKMESSAGE.setMessageId(messageId);


    }
}
