package top.dannystone.cors.server;

import top.dannystone.cors.server.domain.WebAckContext;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-20
 * Time: 上午12:15
 */
public interface ServerService {
    void ack(WebAckContext webAckContext);
}
