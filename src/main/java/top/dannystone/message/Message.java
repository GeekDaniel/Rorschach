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
    private String message;
    private MessageType messageType;
}
