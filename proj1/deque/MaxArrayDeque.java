package deque;

import java.net.Socket;
import java.sql.Array;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator = (o1, o2) -> {
        return ((int) o1 - (int) o2);
    };

    public MaxArrayDeque() {
        arr = new Object[8];
    }

    public MaxArrayDeque(T item) {
        arr = new Object[8];
        arr[0] = item;
        size++;
    }

    public MaxArrayDeque(Comparator<T> c) {
        arr = new Object[8];
        comparator = c;
    }

    public T max() {
        if (size == 0) {
            return null;
        }
        T maxItem = (T) arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] == null) break;
            if (comparator.compare((T) arr[i],maxItem) > 0) {
                maxItem = (T) arr[i];
            }
        }
        return maxItem;
    }

    public T max(Comparator<T> c) {
        if (size == 0) {
            return null;
        }
        T maxItem = (T) arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] == null) break;
            if (c.compare((T) arr[i],maxItem) > 0) {
                maxItem = (T) arr[i];
            }
        }
        return maxItem;
    }


}

