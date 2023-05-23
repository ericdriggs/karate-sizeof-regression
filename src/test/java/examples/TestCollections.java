package examples;

import com.intuit.karate.core.Variable;

import java.lang.reflect.Array;
import java.util.*;

public class TestCollections {

    public static List<String> list() {
        return Arrays.asList("apple", "banana", "canteloupe", "apple");
    }

    public static Set<String> set() {

        return new TreeSet<>(list());
    }

    public static Map<String, String> map() {
        return Collections.singletonMap("foo", "bar");
    }

    public static byte[] byteArray() {
        return "abcdefg".getBytes();
    }

    public static Object sizeOf(Object o) {
        Variable v = new Variable(o);
        if (v.isList()) {
            return v.<List>getValue().size();
        } else if (v.isMap()) {
            return v.<Map>getValue().size();
        } else if (v.isSet()) {
            return v.<Set>getValue().size();
        } else if (v.isArray()) {
            return Array.getLength(v.getValue());
        } else {
            return -1;
        }
    }

}
