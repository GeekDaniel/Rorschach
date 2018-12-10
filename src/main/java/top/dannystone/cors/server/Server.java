package top.dannystone.cors.server;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import top.dannystone.message.MessageChannel;
import top.dannystone.message.NodeConfig;

import java.util.List;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-12-09
 * Time: 下午7:33
 */
@Slf4j
public class Server {
    public static void main(String[] args) {
        List<NodeConfig> nodeConfigs = Lists.newArrayList(new NodeConfig("127.0.0.1", 56546));
        try {
            Class<?> serverClass = Class.forName(args[0]);
            boolean isAMessageServer = AbstractMessageServer.class.isAssignableFrom(serverClass);
            if (isAMessageServer) {
                try {
                    AbstractMessageServer server = (AbstractMessageServer) serverClass.newInstance();
                    Queue<MessageChannel> messageChannelQueue = server.boot(nodeConfigs);
                    while (true){
                        MessageChannel messageChannel = messageChannelQueue.poll();
                        if(messageChannel!=null){
                            log.info("messageChannel",messageChannel );
                        }
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }


            }
        } catch (ClassNotFoundException e) {
            log.error("不能识别的网络层Server: {}", args[0]);
        }

    }
}
