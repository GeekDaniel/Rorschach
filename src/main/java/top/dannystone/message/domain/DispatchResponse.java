package top.dannystone.message.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import top.dannystone.message.Operation;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-12-11
 * Time: 下午5:13
 */
@Data
@AllArgsConstructor
public class DispatchResponse {
    private Operation operation;
    private Object response;
}
