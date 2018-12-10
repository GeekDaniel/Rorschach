package top.dannystone.message;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-01
 * Time: 下午11:18
 */
@Data
public class Consumer {

    private int id;

    public List<Message> poll(int offSet) {
        List<Message> messages = Lists.newArrayList();

        return messages;
    }

}
