package top.dannystone.network.bizSocket;

import bizsocket.base.Body;
import bizsocket.base.Request;
import io.reactivex.Observable;
import okio.ByteString;
import org.json.JSONObject;

/**
 * Created by tong on 17/6/15.
 */
public interface MessageService {
    @Request(cmd = 200,desc = "消息传输")
    Observable<JSONObject> messageDelivery(@Body String params);


}
