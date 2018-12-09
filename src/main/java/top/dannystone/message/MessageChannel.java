package top.dannystone.message;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-12-09
 * Time: 下午8:40
 */
@Data
public class MessageChannel {
    private Topic topic;
    private Message message;
}
