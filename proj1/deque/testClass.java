package deque;

import org.junit.Test;
import java.util.Arrays;

public class testClass {
    @Test
    public static void main(String[] args) {
        /*
        Base base = new Base();
        Sub sub = new Sub();
        System.out.println(base.getArr());
        System.out.println(sub.getArr());
        base.modifyArr();
        sub.modifyArr();
        System.out.println(base.getArr());
        System.out.println(sub.getArr());
         */

        System.out.println((Math.floorMod(-1,5)));
    }
}

class Base {
    private int[] arr;

    public Base() {
        arr = new int[] {1, 2, 3, 4, 5};
    }

    public void modifyArr() {
        arr = new int[] {0,0,0,0,0};
    }

    public String getArr() {
        return Arrays.toString(arr);
    }
}

class Sub extends Base {
    private int[] arr;

    public Sub() {
        arr = new int[] {6,7,8,9,10};
    }

    /*
    public void modifyArr() {
        arr = new int[] {1,1,1,1,1};
    }

     */


    public String getArr() {
        return Arrays.toString(arr);
    }


}