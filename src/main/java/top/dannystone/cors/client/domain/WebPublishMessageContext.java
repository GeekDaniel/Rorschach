package top.dannystone.cors.client.domain;

import lombok.Data;
import top.dannystone.message.NodeConfig;
import top.dannystone.message.Publisher;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-20
 * Time: 上午1:02
 */
@Data
public class WebPublishMessageContext {
    private NodeConfig nodeConfig;
    private Publisher producer;
}
