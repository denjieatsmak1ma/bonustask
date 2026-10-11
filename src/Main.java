import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Main {
    static final int[] SIZES = {100, 1_000, 10_000};
    static final int WARMUP_RUNS = 20;
    static final int MEASURED_RUNS = 30;
    static final List<String> CSV = new ArrayList<>();

    public static void main(String[] args) {
        correctnessTests();
        System.out.println("all correctness tests passed.\n");

        System.out.println("random keys (average case), time in microseconds, avg of "
                + MEASURED_RUNS + " runs");
        printHeader();
        for (int n : SIZES) runBenchmark(n, false);

        System.out.println("\n Sorted keys (worst case for unbalanced BST)");
        printHeader();
        for (int n : SIZES) runBenchmark(n, true);

        System.out.println("\nBST height for n=10000: random=" + bstHeight(10_000, false)
                + ", sorted=" + bstHeight(10_000, true));

        writeCsv("results/result.csv");
    }

    static void printHeader() {
        System.out.printf("%-8s | %-24s | %-24s | %-24s%n", "n",
                "Insert  BST / Hash", "Search  BST / Hash", "Delete  BST / Hash");
    }

    static int[] makeKeys(int n, boolean sorted) {
        int[] keys = new int[n];
        for (int i = 0; i < n; i++) keys[i] = i;
        if (!sorted) {
            Random rnd = new Random(42);
            for (int i = n - 1; i > 0; i--) {
                int j = rnd.nextInt(i + 1);
                int t = keys[i]; keys[i] = keys[j]; keys[j] = t;
            }
        }
        return keys;
    }

    static long[] oneRun(int[] keys, int[] deleteOrder) {
        BST<Integer, Integer> bst = new BST<>();
        HashTable<Integer, Integer> ht = new HashTable<>();
        long t;
        long[] r = new long[6];

        t = System.nanoTime();
        for (int k : keys) bst.insert(k, k);
        r[0] = System.nanoTime() - t;

        t = System.nanoTime();
        for (int k : keys) ht.put(k, k);
        r[1] = System.nanoTime() - t;

        long sink = 0;
        t = System.nanoTime();
        for (int k : keys) sink += bst.search(k);
        r[2] = System.nanoTime() - t;

        t = System.nanoTime();
        for (int k : keys) sink += ht.get(k);
        r[3] = System.nanoTime() - t;

        t = System.nanoTime();
        for (int k : deleteOrder) bst.delete(k);
        r[4] = System.nanoTime() - t;

        t = System.nanoTime();
        for (int k : deleteOrder) ht.remove(k);
        r[5] = System.nanoTime() - t;

        if (sink == 42 || bst.size() != 0 || ht.size() != 0) System.out.print(""); // keep JIT honest
        return r;
    }

    static void runBenchmark(int n, boolean sorted) {
        int[] keys = makeKeys(n, sorted);
        int[] deleteOrder = keys.clone();

        for (int i = 0; i < WARMUP_RUNS; i++) oneRun(keys, deleteOrder);

        double[] sum = new double[6];
        for (int i = 0; i < MEASURED_RUNS; i++) {
            long[] r = oneRun(keys, deleteOrder);
            for (int j = 0; j < 6; j++) sum[j] += r[j];
        }
        for (int j = 0; j < 6; j++) sum[j] = sum[j] / MEASURED_RUNS / 1000.0; // -> microseconds

        CSV.add(String.format(Locale.ROOT, "%s,%d,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f",
                sorted ? "sorted" : "random", n, sum[0], sum[1], sum[2], sum[3], sum[4], sum[5]));
        System.out.printf("%-8d | %10.1f / %-11.1f | %10.1f / %-11.1f | %10.1f / %-11.1f%n",
                n, sum[0], sum[1], sum[2], sum[3], sum[4], sum[5]);
    }

    static void writeCsv(String path) {
        List<String> lines = new ArrayList<>();
        lines.add("scenario,n,bst_insert_us,hash_insert_us,bst_search_us,hash_search_us,bst_delete_us,hash_delete_us");
        lines.addAll(CSV);
        try {
            Path p = Paths.get(path);
            if (p.getParent() != null) Files.createDirectories(p.getParent());
            Files.write(p, lines);
            System.out.println("CSV written to " + path);
        } catch (IOException e) {
            System.err.println("could not write CSV: " + e.getMessage());
        }
    }

    static int bstHeight(int n, boolean sorted) {
        BST<Integer, Integer> bst = new BST<>();
        for (int k : makeKeys(n, sorted)) bst.insert(k, k);
        return bst.height();
    }

    static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError("FAILED: " + msg);
    }

    static void correctnessTests() {
        BST<Integer, Integer> bst = new BST<>();
        HashTable<Integer, Integer> ht = new HashTable<>();
        Map<Integer, Integer> ref = new HashMap<>();
        Random rnd = new Random(1);

        for (int i = 0; i < 20_000; i++) {
            int key = rnd.nextInt(2_000);
            int op = rnd.nextInt(3);
            if (op == 0) {
                bst.insert(key, i);
                ht.put(key, i);
                ref.put(key, i);
            } else if (op == 1) {
                Integer expected = ref.get(key);
                check(Objects.equals(bst.search(key), expected), "BST search " + key);
                check(Objects.equals(ht.get(key), expected), "Hash get " + key);
            } else {
                boolean expected = ref.remove(key) != null;
                check(bst.delete(key) == expected, "BST delete " + key);
                check(ht.remove(key) == expected, "Hash remove " + key);
            }
            check(bst.size() == ref.size(), "BST size");
            check(ht.size() == ref.size(), "Hash size");
        }

        BST<Integer, String> e = new BST<>();
        check(e.search(1) == null, "search empty");
        check(!e.delete(1), "delete empty");
        e.insert(5, "a"); e.insert(3, "b"); e.insert(8, "c");
        check(e.delete(5), "delete root with 2 children");
        check(e.search(3).equals("b") && e.search(8).equals("c"), "children kept");
        check(e.delete(3) && e.delete(8) && e.size() == 0, "delete all");
    }
}
