# Analyze and compare the performance of a Binary Search Tree (BST) and a Hash Table

## A. Project Overview
This project implements two data structures in Java from scratch and compares them:
- **Binary Search Tree (BST)** - unbalanced, with insert, search and delete
- **Hash Table** - separate chaining, resizes when the load factor exceeds 0.75

Both were tested for correctness and benchmarked with 100, 1,000 and 10,000 elements,
using random keys and sorted keys. Files: `src/BST.java`, `src/HashTable.java`, `src/Main.java`,
`results/result.csv`

## B. Algorithm Analysis

| Operation | BST best / avg / worst | Hash table best / avg / worst |
|-----------|------------------------|-------------------------------|
| Insert    | O(1) / O(log n) / O(n) | O(1) / O(1) / O(n)            |
| Search    | O(1) / O(log n) / O(n) | O(1) / O(1) / O(n)            |
| Delete    | O(1) / O(log n) / O(n) | O(1) / O(1) / O(n)            |

- **BST:** every operation follows one path from the root, so the cost equals the tree height.
  Best case: the key is at the root. Worst case: keys are inserted in sorted order, the tree becomes
  a linked list and height = n
- **Hash table:** the cost is computing the hash and scanning one bucket. Best and average case:
  short chains, O(1). Worst case: all keys fall into one bucket, O(n). Resizing costs O(n) but
  happens rarely, so insert stays O(1) amortized

## C. Experimental Results
Time in microseconds (µs) for all n operations

| Keys | n | Insert BST / Hash | Search BST / Hash | Delete BST / Hash |
|------|---|-------------------|-------------------|-------------------|
| random | 100    | 19.6 / 17.3     | 9.0 / 7.7      | 13.4 / 5.7    |
| random | 1,000  | 72.4 / 50.1     | 61.5 / 13.8    | 64.6 / 14.5   |
| random | 10,000 | 914.8 / 322.5   | 955.4 / 51.6   | 1099.6 / 55.2 |
| sorted | 100    | 7.5 / 1.2       | 8.1 / 0.3      | 0.3 / 0.3     |
| sorted | 1,000  | 764.0 / 15.8    | 846.2 / 4.2    | 3.9 / 6.8     |
| sorted | 10,000 | 71636.4 / 346.9 | 78697.8 / 38.7 | 31.7 / 35.0   |

- With random keys the hash table is faster, and the gap grows with n (search is ~18x faster at n = 10,000).
- With sorted keys the BST degenerates: 10,000 searches take ~79 ms vs ~0.04 ms for the hash table.
- Deleting sorted keys in sorted order is fast for the BST only because the root is removed every time.
- The hash table is faster in practice because chains are short and its buckets sit in one array
  (better cache use), while the BST follows pointers and needs several comparisons per step.
  The BST is still needed when sorted order or range queries matter

## E. Reflection
This project showed me that the same asymptotic idea behaves very differently depending on the input.
An unbalanced BST is fast on random data but turns into a linked list on sorted data, while the hash table
does not care about key order. It also showed me why balanced trees (AVL, Red-Black) exist and why a good
hash function and load factor matter for a hash table

The hardest part of the implementation was deleting a BST node with two children: the node has to be
replaced with its in-order successor and the parent links must stay correct. I wrote the BST operations
iteratively because recursion on a degenerate tree of 10,000 nodes can overflow the stack. Another challenge
was benchmarking: JVM warm-up makes small inputs noisy, so I used warm-up runs and averaged several runs.
Preparing the results table and charts also took effort because decimal separators in the CSV were
misread by the spreadsheet program

## F. Screenshots
![Снимок экрана 2026-10-10 225852.png](../AppData/Local/Temp/bonustask/docs/screen/%D0%A1%D0%BD%D0%B8%D0%BC%D0%BE%D0%BA%20%D1%8D%D0%BA%D1%80%D0%B0%D0%BD%D0%B0%202026-10-10%20225852.png)
