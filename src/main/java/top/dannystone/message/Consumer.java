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
public class Consumer {

    private int id;

    public Consumer(int id){
        this.id=id;
    }

    public int getId(){
        return id;
    }

    public List<Message> poll(int offSet) {
        List<Message> messages = Lists.newArrayList();

        return messages;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Consumer)) {
            return false;
        }
        Consumer consumer = (Consumer) obj;

        return consumer.getId()==this.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

}
