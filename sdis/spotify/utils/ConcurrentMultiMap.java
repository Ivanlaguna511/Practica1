package sdis.spotify.utils;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Thread-safe Generic MultiMap implementation using non-blocking queues.
 */
public class ConcurrentMultiMap<K, T> {
    private final ConcurrentMap<K, ConcurrentLinkedQueue<T>> map = new ConcurrentHashMap<>();

    public void push(K key, T value) {
        ConcurrentLinkedQueue<T> queue = map.get(key);
        if (queue == null) {
            ConcurrentLinkedQueue<T> newQueue = new ConcurrentLinkedQueue<>();
            ConcurrentLinkedQueue<T> previous = map.putIfAbsent(key, newQueue);
            queue = (previous == null) ? newQueue : previous;
        }
        queue.add(value);
    }

    public T pop(K key) {
        Queue<T> queue = map.get(key);
        return (queue != null) ? queue.poll() : null;
    }
}