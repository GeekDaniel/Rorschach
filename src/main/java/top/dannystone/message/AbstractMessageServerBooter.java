package top.dannystone.message;

import top.dannystone.exception.NodeConfigException;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-06
 * Time: 下午11:36
 */
public abstract class AbstractMessageServerBooter {
    Pattern IP_PATTERN=Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

    boolean validateNodeConfig(List<NodeConfig> nodeConfigs ){
        return !nodeConfigs.stream().anyMatch(e->!checkValid(e));
    }

    boolean checkValid(NodeConfig nodeConfig){
        Matcher matcher=IP_PATTERN.matcher(nodeConfig.getIp());
        int port = nodeConfig.getPort();
        if(matcher.find()&&port>1&&port<65535){
            return true;
        }
        return  false;
    }

    public abstract  void  doBoot(List<NodeConfig> nodeConfigs);

    void boot(List<NodeConfig> nodeConfigs){
        if(!validateNodeConfig(nodeConfigs)){
            throw new NodeConfigException("节点配置信息错误！");
        }
        doBoot(nodeConfigs);

    }


}
