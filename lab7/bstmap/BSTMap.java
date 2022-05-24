package bstmap;

import edu.princeton.cs.algs4.BST;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B{
    private BSTNode root;
    private int size = 0;

    @Override
    public void clear() {
        root = null;
        size = 0;
        System.out.println("This tree is cleared.");
    }

    @Override
    public boolean containsKey(Object key) {
        BSTNode pointer = searchKey(root, key);
        if (pointer == null) return false;
        int cmpr = pointer.key.compareTo((K) key);
        if (cmpr == 0) return true;
        return false;
    }

    private BSTNode searchKey(BSTNode pointer, Object key) {
        if (pointer == null) return null;
        int cmpr = pointer.key.compareTo((K) key);
        if (cmpr > 0 && pointer.leftChild != null) {
            return searchKey(pointer.leftChild, key);
        } else if (cmpr < 0 && pointer.rightChild != null) {
            return searchKey(pointer.rightChild, key);
        } else return pointer;
    }

    @Override
    public Object get(Object key) {
        BSTNode pointer = searchKey(root, key);
        if (pointer == null) return null;
        int cmpr = pointer.key.compareTo((K) key);
        if (cmpr == 0) return pointer.value;
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(Object key, Object value) {
        if (root == null) {
            root = new BSTNode((K) key, value);
            size++;
            return;
        }
        BSTNode pointer = searchKey(root, key);
        int cmpr = pointer.key.compareTo((K) key);
        if (cmpr == 0) {
            pointer.value = value;
        } else if (cmpr > 0) {
            pointer.leftChild = new BSTNode((K) key, value);
        } else {
            pointer.rightChild = new BSTNode((K) key, value);
        }
        size++;
    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException("Invalid operation for BSTMap");
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("Invalid operation for BSTMap");
    }

    @Override
    public Object remove(Object key, Object value) {
        throw new UnsupportedOperationException("Invalid operation for BSTMap");
    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException("Invalid operation for BSTMap");
    }

    public void printInOrder() {
        System.out.println("not finished yet");
    }

    private class BSTNode<K extends Comparable, V> {
        private K key;
        private V value;
        private BSTNode<K, V> leftChild;
        private BSTNode<K, V> rightChild;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
