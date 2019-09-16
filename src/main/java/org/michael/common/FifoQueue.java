package org.michael.common;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created on 2019-09-16 11:18
 * Author : Michael.
 */
public class FifoQueue<T> {

    private final LinkedList<T> queue;
    private final int queueCapacity;
    private final ReentrantLock lock;

    public FifoQueue(int queueCapacity) {
        this.queueCapacity = queueCapacity;
        this.queue = new LinkedList<>();
        this.lock = new ReentrantLock();
    }

    public void add(T e) {
        lock.lock();
        try {
            while (queue.size() >= queueCapacity) {
                queue.removeFirst();
            }
            queue.add(e);
        } finally {
            lock.unlock();
        }
    }

    public T removeFirst(T e) {
        lock.lock();
        try {
            if (queue.size() == 0) {
                return null;
            } else {
                return queue.removeFirst();
            }
        } finally {
            lock.unlock();
        }
    }

    public List<T> getAndClearQueue() {
        List<T> r = new LinkedList<>();
        lock.lock();
        try {
            while (!queue.isEmpty()) {
                r.add(queue.removeFirst());
            }
        } finally {
            lock.unlock();
        }
        return r;
    }
}
