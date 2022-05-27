package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int initialSize = 16;
    private double loadFactor = 0.75;
    private int size = 0;

    /** Constructors */
    public MyHashMap() {
        buckets = createTable(initialSize);
    }

    public MyHashMap(int initialSize) {
        this.initialSize = initialSize;
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        this.initialSize = initialSize;
        this.loadFactor = maxLoad;
        buckets = createTable(initialSize);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    @Override
    public void clear() {
        buckets = null;
        size = 0;
    }

    /** A helper method for hashing the key. */
    private int hashing(K key) {
        int hashCode = key.hashCode();
        int index = Math.floorMod(hashCode, initialSize);
        return index;
    }

    @Override
    public boolean containsKey(K key) {
        if (buckets == null) return false;
        int index = hashing(key);
        if (buckets[index] == null) return false;
        Collection bucket = buckets[index];
        for (Object o : bucket) {
            Node node = (Node) o;
            if (node.key.equals(key)) return true;
        }
        return false;
    }

    @Override
    public V get(K key) {
        if (buckets == null) return null;
        int index = hashing(key);
        if (buckets[index] == null) return null;
        Collection bucket = buckets[index];
        for (Object o : bucket) {
            Node node = (Node) o;
            if (node.key.equals(key)) return node.value;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (size >=  loadFactor * initialSize) {
            resize();
        }
        int index = hashing(key);
        if (buckets[index] == null) {
            buckets[index] = createBucket();
            buckets[index].add(createNode(key, value));
            size++;
            return;
        }
        Collection bucket = buckets[index];
        for (Object o : bucket) {
            Node node = (Node) o;
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }
        bucket.add(createNode(key, value));
        size++;
    }

    @Override
    public Set<K> keySet() {
        if (buckets == null) return null;
        HashSet<K> keySet = new HashSet<>();
        for (Collection bucket : buckets) {
            if (bucket == null) continue;
            for (Object o : bucket) {
                Node node = (Node) o;
                keySet.add(node.key);
            }
        }
        return keySet;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("This is an unsupported operation.");
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("This is an unsupported operation.");
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    private class MyHashMapIterator implements Iterator {
        private int position;

        public MyHashMapIterator() {
            position = 0;
        }

        @Override
        public boolean hasNext() {
            return position < size();
        }

        @Override
        public Object next() {
            Collection<Node> result = buckets[position];
            while (buckets[position] != null) {
                position++;
            }
            return result;
        }
    }

    private void resize() {
        rehash();
        initialSize *= 2;
    }

    private void rehash() {
        MyHashMap<K, V> newHashMap = new MyHashMap<>(initialSize*2);
        for (Collection bucket : buckets) {
            if (bucket != null) {
                for (Object o : bucket) {
                    Node node = (Node) o;
                    newHashMap.put(node.key, node.value);
                }
            }
        }
        clear();
        buckets= newHashMap.buckets;
        size = newHashMap.size();
    }

}
