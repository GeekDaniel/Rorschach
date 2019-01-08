package top.dannystone.common.suger;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**找到最先满足条件的解,如果没有过滤条件，默认任何元素都满足条件
 * Created with IntelliJ IDEA.
 * Description:
 * User: daniel
 * Date: 2018-12-01
 * Time: 下午6:08
 */
public class FirstMeet<T> {
    private Predicate<T> rule=Predicates.alwaysTrue();
    private List<Object> options=Lists.newArrayList();

    private FirstMeet(){

    }

    public static <T> FirstMeet<T> first(T t){
        FirstMeet<T> firstMeet = new FirstMeet<>();
        firstMeet.options.add(t);
        return firstMeet;
    }

    public FirstMeet<T> then(Supplier<T> option){
        options.add(option);
        return this;
    }

    public FirstMeet<T> then(T option){
        options.add(option);
        return this;
    }

    public FirstMeet<T> meet(Predicate<T> rule){
        this.rule=rule;
        return this;
    }

    public Optional<T> get(){
        if(rule==null){
            return Optional.ofNullable(
                    options.size()==0?
                            null:
                            getOptionValue(options.get(0)));
        }else{
            for (Object o:options) {
                T value=getOptionValue(o);
                if(rule.test(value)){
                    return Optional.ofNullable(value);
                }
            }
            return Optional.empty();
        }

    }

    private T getOptionValue(Object o){
        if(o instanceof Supplier){
            return (T)(((Supplier) o).get());
        }else{
            return (T)o;
        }
    }
}
