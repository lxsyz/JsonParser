import java.util.ArrayList;

/**
 * Created by Administrator on 2017/6/11.
 */
public class Test {
    public static void main(String[] args) {
        ArrayList<Object> list = new ArrayList<>();
        list.add(1);
        list.add(2.3);
        list.add("sss");

        if (list.get(0) instanceof Integer) {
            System.out.println("ssd");
        }

        System.out.println(list.get(0).getClass());
    }
}
