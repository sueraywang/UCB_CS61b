package deque;

import java.net.Socket;
import java.sql.Array;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator = (o1, o2) -> {
        return ((int) o1 - (int) o2);
    };

    public MaxArrayDeque(Comparator<T> c) {
        setArr(new Object[8]);
        comparator = c;
    }

    public T max() {
        if (getSize() == 0) {
            return null;
        }
        T maxItem = (T) getArr()[0];
        for (int i = 1; i < getArr().length; i++) {
            if (getArr()[i] == null) break;
            if (comparator.compare((T) getArr()[i],maxItem) > 0) {
                maxItem = (T) getArr()[i];
            }
        }
        return maxItem;
    }

    public T max(Comparator<T> c) {
        if (getSize() == 0) {
            return null;
        }
        T maxItem = (T) getArr()[0];
        for (int i = 1; i < getArr().length; i++) {
            if (getArr()[i] == null) break;
            if (c.compare((T) getArr()[i],maxItem) > 0) {
                maxItem = (T) getArr()[i];
            }
        }
        return maxItem;
    }


}

