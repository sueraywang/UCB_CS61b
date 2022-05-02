package deque;

import java.net.Socket;
import java.sql.Array;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator = new Comparator<T>() {
        @Override
        public int compare(T o1, T o2) {
            if ((int) o1 > (int) o2) {
                return 1;
            } else if ((int) o1 == (int) o2) {
                return 0;
            } else return -1;
        }
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
        for (int i = 1; i < size; i++) {
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
        for (int i = 1; i < size; i++) {
            if (c.compare((T) arr[i],maxItem) > 0) {
                maxItem = (T) arr[i];
            }
        }
        return maxItem;
    }


}

