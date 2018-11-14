package top.dannystone.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-06
 * Time: 下午7:08
 */
@Data
@AllArgsConstructor
public class NodeConfig {
    private String ip;
    private int port;
}
