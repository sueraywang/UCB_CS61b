package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private Object[] arr;
    private int size = 0;
    private int firstIndex = 0;
    private int lastIndex = 0;

    public ArrayDeque() {
        arr = new Object[8];
    }

    public ArrayDeque(int size) {
        arr = new Object[size];
    }

    public Object[] getArr() {
        return arr;
    }

    public void setArr(Object[] arr) {
        this.arr = arr;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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
        if (size == 1) {
            firstIndex = lastIndex;
        } else {
            firstIndex = Math.floorMod(firstIndex + 1, arr.length);
        }
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
        if (size == 1) {
            lastIndex = firstIndex;
        } else {
            lastIndex = Math.floorMod(lastIndex - 1, arr.length);
        }
        size--;
        return removed;
    }

    @Override
    public T get(int index) {
        return (T) arr[Math.floorMod(index + firstIndex,arr.length)];
    }

    private void expandSize() {
        Object[] copy = new Object[(int) (arr.length * 1.5)];
        copyArray(copy);
    }

    private void shrinkSize() {
        Object[] copy = new Object[(int) (size / 0.5)];
        copyArray(copy);
    }

    private void copyArray(Object[] copy) {
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
        if (!(o instanceof Deque)) return false;
        Deque<T> lld = (Deque<T>) o;
        if (lld.size() != size) return false;
        for (int i = 0; i < size; i++) {
            if (!(get(i).equals(lld.get(i)))) {
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

