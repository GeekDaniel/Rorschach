package top.dannystone.message;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-11-01
 * Time: 下午9:32
 */
public class Topic {

    private String name;

    public String getName() {
        return name;
    }

    public Topic(String name) {

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Topic)) {
            return false;
        }
        Topic topic = (Topic) obj;

        if (name == null) {
            return topic.getName() == null;
        }
        return topic.getName().equals(this.getName());
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
