public class HashTable<K, V> {
    private static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;
        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private static final int INITIAL_CAPACITY = 16;
    private static final double MAX_LOAD_FACTOR = 0.75;

    private Node<K, V>[] buckets;
    private int size;
    @SuppressWarnings("unchecked")
    public HashTable() {
        buckets = (Node<K, V>[]) new Node[INITIAL_CAPACITY];
    }

    public int size() {
        return size;
    }

    private int indexFor(K key, int capacity) {
        int h = key.hashCode();
        h ^= (h >>> 16); // spread high bits
        return (h & 0x7fffffff) % capacity;
    }

    public void put(K key, V value) {
        int idx = indexFor(key, buckets.length);
        for (Node<K, V> n = buckets[idx]; n != null; n = n.next) {
            if (n.key.equals(key)) {
                n.value = value;
                return;
            }
        }
        buckets[idx] = new Node<>(key, value, buckets[idx]);
        size++;
        if (size > buckets.length * MAX_LOAD_FACTOR) {
            resize(buckets.length * 2);
        }
    }

    public V get(K key) {
        int idx = indexFor(key, buckets.length);
        for (Node<K, V> n = buckets[idx]; n != null; n = n.next) {
            if (n.key.equals(key)) return n.value;
        }
        return null;
    }

    public boolean containsKey(K key) {
        int idx = indexFor(key, buckets.length);
        for (Node<K, V> n = buckets[idx]; n != null; n = n.next) {
            if (n.key.equals(key)) return true;
        }
        return false;
    }

    public boolean remove(K key) {
        int idx = indexFor(key, buckets.length);
        Node<K, V> prev = null;
        for (Node<K, V> n = buckets[idx]; n != null; prev = n, n = n.next) {
            if (n.key.equals(key)) {
                if (prev == null) buckets[idx] = n.next;
                else prev.next = n.next;
                size--;
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Node<K, V>[] newBuckets = (Node<K, V>[]) new Node[newCapacity];
        for (Node<K, V> head : buckets) {
            for (Node<K, V> n = head; n != null; ) {
                Node<K, V> next = n.next;
                int idx = indexFor(n.key, newCapacity);
                n.next = newBuckets[idx];
                newBuckets[idx] = n;
                n = next;
            }
        }
        buckets = newBuckets;
    }
}
