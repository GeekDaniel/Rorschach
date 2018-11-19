package top.dannystone.cors.client;

import top.dannystone.cors.client.domain.WebPublishMessageContext;
import top.dannystone.cors.client.domain.WebRegisterContext;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-20
 * Time: 上午12:21
 */
public interface ClientService {
    void register(WebRegisterContext webRegisterContext);

    void sendMessage(WebPublishMessageContext webPublishMessageContext);
}
