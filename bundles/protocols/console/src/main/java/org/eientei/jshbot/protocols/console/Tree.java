package org.eientei.jshbot.protocols.console;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Tumin Alexander
 * Date: 2013-02-05
 * Time: 22:12
 */
public class Tree<K,V> implements Iterable<Tree.Node<K, V>> {
    private Node<K,V> root = new Node<K, V>(null,null);

    public Node<K,V> traverse(List<K> keys) {
        if (keys == null) return root;
        Node<K,V> n = root;

        for (K key : keys) {
            n = n.getChild(key);
            if (n == null) throw new IllegalAccessError("Accessd non-existent key in tree: " + key);
        }

        return n;
    }

    public Node<K,V> tryTraverse(List<K> keys) {
        if (keys == null) return root;
        Node<K,V> n = root;

        for (K key : keys) {
            Node<K,V> c = n.getChild(key);
            if (c == null) break;
            n = c;
        }

        return n;
    }

    private Node<K,V> createPath(List<K> keys) {
        if (keys == null) return root;

        Node<K,V> n = root;

        for (K key : keys) {
            Node<K,V> c = n.getChild(key);
            if (c == null) {
                n = n.addChild(key);
            } else {
                n = c;
            }
        }

        return n;
    }

    public V set(V value, List<K> keys) {
        Node<K,V> n = traverse(keys);
        V oldValue = n.getData();
        n.setData(value);
        return oldValue;
    }

    public V get(List<K> keys) {
        Node<K,V> n = traverse(keys);
        return n.getData();
    }

    public V tryGet(List<K> keys) {
        return tryTraverse(keys).getData();
    }

    public Node<K,V> insert(V value, List<K> keys) {
        Node<K,V> n = createPath(keys);
        n.setData(value);
        return n;
    }

    public Node<K,V> remove(List<K> keys) {
        Node<K,V> n = traverse(keys);
        if (n.isRoot()) {
            Node<K,V> r = n;
            n.setData(null);
            return r;
        }

        Node<K,V> parent = n.getParent();
        if (n.isLeaf()) {
            parent.removeChild(n.getKey());
        } else {
            n.markForDeletion();
        }

        if ((parent.isEmpty() || parent.isMarkedForDeletion()) && parent.isLeaf()) {
            remove(parent.getPath());
        }

        return n;
    }

    public Node<K,V> getRoot() {
        return root;
    }

    @Override
    public Iterator<Node<K, V>> iterator() {
        return root.iterator();
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public static class Node<K,V> implements Iterable<Node<K,V>> {
        private final TreeMap<K,Node<K,V>> children = new TreeMap<K, Node<K, V>>();
        private final Node<K,V> parent;
        private final List<K> path;
        private V data = null;
        private boolean markedForDeletion = false;

        public Node(K key, Node<K, V> parent) {
            this.parent = parent;
            this.path = new ArrayList<K>();
            if (parent != null) {
                path.addAll(parent.getPath());
            }
            if (key != null) {
                path.add(key);
            }
        }

        public void markForDeletion() {
            markedForDeletion = true;
        }

        public boolean isMarkedForDeletion() {
            return markedForDeletion;
        }

        public V getData() {
            return data;
        }

        public void setData(V value) {
            data = value;
        }

        public K getKey() {
            return path.get(path.size()-1);
        }

        public Node<K,V> getParent() {
            return parent;
        }

        public Node<K,V> getChild(K key) {
            return children.get(key);
        }

        public Node<K,V> addChild(K key) {
            Node<K,V> n = new Node<K, V>(key,this);
            children.put(key, n);
            return n;
        }

        public List<K> getPath() {
            return Collections.unmodifiableList(path);
        }

        public Node<K,V> removeChild(K key) {
            return children.remove(key);
        }

        public SortedMap<K,Node<K,V>> tailMap(K key) {
            return children.tailMap(key);
        }

        public boolean isLeaf() {
            return children.isEmpty();
        }

        public boolean isRoot() {
            return parent == null;
        }

        public boolean isEmpty() {
            return data == null;
        }

        @Override
        public Iterator<Node<K, V>> iterator() {
            return children.values().iterator();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Node[");
            for (K k : getPath()) {
                sb.append("/");
                sb.append(k);
            }
            sb.append("] = ");
            sb.append(data);
            sb.append(" ");
            sb.append(children);
            return sb.toString();
        }
    }
}

