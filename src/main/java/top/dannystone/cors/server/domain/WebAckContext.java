package top.dannystone.cors.server.domain;

import lombok.Data;
import top.dannystone.message.Message;
import top.dannystone.message.NodeConfig;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-20
 * Time: 上午1:09
 */
@Data
public class WebAckContext {
    private NodeConfig nodeConfig;
    private int messageId;
}
