package top.dannystone.cors.client.domain;

import lombok.Data;
import top.dannystone.message.NodeConfig;
import top.dannystone.message.Consumer;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-20
 * Time: 上午12:57
 */
@Data
public class WebRegisterContext {
    private NodeConfig nodeConfig;
    private Consumer consumer;
}
