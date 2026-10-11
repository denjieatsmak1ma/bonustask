public class BST<K extends Comparable<K>, V> {
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> left, right;
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V> root;
    private int size;

    public int size() {
        return size;
    }

    public void insert(K key, V value) {
        if (root == null) {
            root = new Node<>(key, value);
            size++;
            return;
        }
        Node<K, V> cur = root;
        while (true) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) {
                cur.value = value;
                return;
            } else if (cmp < 0) {
                if (cur.left == null) {
                    cur.left = new Node<>(key, value);
                    size++;
                    return;
                }
                cur = cur.left;
            } else {
                if (cur.right == null) {
                    cur.right = new Node<>(key, value);
                    size++;
                    return;
                }
                cur = cur.right;
            }
        }
    }

    public V search(K key) {
        Node<K, V> cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) return cur.value;
            cur = (cmp < 0) ? cur.left : cur.right;
        }
        return null;
    }

    public boolean contains(K key) {
        Node<K, V> cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) return true;
            cur = (cmp < 0) ? cur.left : cur.right;
        }
        return false;
    }

    public boolean delete(K key) {
        Node<K, V> parent = null;
        Node<K, V> cur = root;

        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) break;
            parent = cur;
            cur = (cmp < 0) ? cur.left : cur.right;
        }
        if (cur == null) return false;

        if (cur.left != null && cur.right != null) {
            Node<K, V> succParent = cur;
            Node<K, V> succ = cur.right;
            while (succ.left != null) {
                succParent = succ;
                succ = succ.left;
            }
            cur.key = succ.key;
            cur.value = succ.value;
            parent = succParent;
            cur = succ; // now delete the successor (has at most a right child)
        }

        Node<K, V> child = (cur.left != null) ? cur.left : cur.right;
        if (parent == null) {
            root = child;
        } else if (parent.left == cur) {
            parent.left = child;
        } else {
            parent.right = child;
        }
        size--;
        return true;
    }

    public int height() {
        if (root == null) return 0;
        java.util.ArrayDeque<Node<K, V>> level = new java.util.ArrayDeque<>();
        level.add(root);
        int h = 0;
        while (!level.isEmpty()) {
            int n = level.size();
            h++;
            for (int i = 0; i < n; i++) {
                Node<K, V> x = level.poll();
                if (x.left != null) level.add(x.left);
                if (x.right != null) level.add(x.right);
            }
        }
        return h;
    }
}
