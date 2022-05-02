package deque;

import java.net.Socket;
import java.sql.Array;
import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T>{
    protected Object[] arr;
    protected int size = 0;
    protected int firstIndex = 0;
    protected int lastIndex = 0;

    public ArrayDeque() {
        arr = new Object[8];
    }

    public ArrayDeque(T item) {
        arr = new Object[8];
        arr[0] = item;
        size++;
        lastIndex = 1;
    }

    @Override
    public void addFirst(T item) {
        if (size > 0.75 * arr.length) {
            expandSize();
        }
        if (size != 0) {
            firstIndex = Math.floorMod(firstIndex - 1, arr.length);
        }
        arr[firstIndex] = item;
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size > 0.75 * arr.length) {
            expandSize();
        }
        if (size != 0) {
            lastIndex = Math.floorMod(lastIndex + 1, arr.length);
        }
        arr[lastIndex] = item;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = firstIndex; i < size + firstIndex; i++) {
            System.out.println(arr[Math.floorMod(i, arr.length)]);
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if (arr.length >= 16 && size < arr.length * 0.25) {
            shrinkSize();
        }
        T removed = (T) arr[firstIndex];
        firstIndex = Math.floorMod(firstIndex + 1, arr.length);
        size--;
        return removed;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if (arr.length >= 16 && size < arr.length * 0.25) {
            shrinkSize();
        }
        T removed = (T) arr[lastIndex];
        lastIndex = Math.floorMod(lastIndex - 1, arr.length);
        size--;
        return removed;
    }

    @Override
    public T get(int index) {
        return (T) arr[Math.floorMod(index + firstIndex,arr.length)];
    }

    public void expandSize() {
        Object[] copy = new Object[(int) (arr.length * 1.5)];
        copyArray(copy);
    }

    public void shrinkSize() {
        Object[] copy = new Object[(int) (size / 0.5)];
        copyArray(copy);
    }

    public void copyArray(Object[] copy) {
        for (int i = firstIndex; i < size + firstIndex; i++) {
            copy[i - firstIndex] = arr[Math.floorMod(i, arr.length)];
        }
        arr = copy;
        firstIndex = 0;
        lastIndex = size - 1;
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof ArrayDeque)) return false;
        ArrayDeque<T> arrayDeque = (ArrayDeque<T>) o;
        for (int i = 0; i < size; i++) {
            if (!(this.get(i).equals(arrayDeque.get(i)))) {
                return false;
            }
        }
        return true;
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        int count;

        public ArrayDequeIterator() {
            count = 0;
        }

        @Override
        public boolean hasNext() {
            return count < size ;
        }

        @Override
        public T next() {
            T returnVal = (T) arr[Math.floorMod(firstIndex + 1, arr.length)];
            count++;
            return returnVal;
        }
    }
}

