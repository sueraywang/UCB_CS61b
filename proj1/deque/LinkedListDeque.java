package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private Node<T> sentinel = null;
    private int size;

    private class Node<T> {
        private T item;
        private Node<T> next;
        private Node<T> prev;

        Node(T item) {
            this.item = item;
        }

        public Node(T item, Node<T> prev, Node<T> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    public LinkedListDeque() {
        size = 0;
    }

    public LinkedListDeque(Node<T> node) {
        sentinel = node;
        size = 1;
    }

    /**
     * Make sentinel point to a single item, with prev and next all point to itself.
     *
     * @param item the content of the single item
     */
    public void createSingleItem(T item) {
        sentinel = new Node<>(item);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
    }

    @Override
    public void addFirst(T item) {
        if (size == 0) {
            createSingleItem(item);
        } else {
            Node<T> first = new Node<>(item, sentinel.prev, sentinel);
            sentinel.prev.next = first;
            if (size == 1) {
                sentinel.prev = first;
            }
            sentinel = first;
        }
        size++;
    }

    @Override
    public void addLast(T item) {
        if (size == 0) {
            createSingleItem(item);
        } else {
            Node<T> last = new Node<>(item, sentinel.prev, sentinel);
            sentinel.prev.next = last;
            sentinel.prev = last;
        }
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if (size == 0) {
            return;
        }
        Node<T> p = sentinel;
        while (p.next != sentinel) {
            System.out.println(p.item);
            p = p.next;
        }
        System.out.println(p.item);
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T firstItem = sentinel.item;
        if (size == 1) {
            sentinel = null;
        } else {
            sentinel.next.prev = sentinel.prev;
            sentinel.prev.next = sentinel.next;
            sentinel = sentinel.next;
        }
        size--;
        return firstItem;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T lastItem = sentinel.prev.item;
        if (size == 1) {
            sentinel = null;
        } else {
            sentinel.prev.prev.next = sentinel;
            sentinel.prev = sentinel.prev.prev;
        }
        size--;
        return lastItem;
    }

    @Override
    public T get(int index) {
        if (size == 0) {
            return null;
        }
        Node<T> p = sentinel;
        for (int i = 0; i < index; i++) {
            if (p.next != sentinel) {
                p = p.next;
            } else {
                return null;
            }
        }
        return p.item;
    }

    public T getRecursive(int index) {
        if (index == 0) {
            return sentinel.item;
        } else {
            LinkedListDeque<T> helperList = new LinkedListDeque<T>(sentinel.next);
            return helperList.getRecursive(index - 1);
        }
    }

    private class LinkedListDequeIterator implements Iterator {
        private int position;

        public LinkedListDequeIterator() {
            position = 0;
        }

        public boolean hasNext() {
            return position < size();
        }

        @Override
        public T next() {
            return get(position++);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof LinkedListDeque)) return false;
        LinkedListDeque<T> lld = (LinkedListDeque<T>) o;
        Node<T> thisp = sentinel;
        Node<T> lldp = lld.sentinel;
        while (thisp.next != sentinel) {
            if (thisp.item.equals(lldp.item)) {
                thisp = thisp.next;
                lldp = lldp.next;
            } else return false;
        }
        return true;
    }
}
