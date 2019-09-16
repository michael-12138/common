package org.michael.common;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created on 2019-09-16 11:24
 * Author : Michael.
 */
public class ObjectPool<T> {

    private final int coreSize;
    private final ObjectFactory<T> factory;
    private final LinkedList<T> pool;

    private final ReentrantLock lock;

    public ObjectPool(int coreSize, ObjectFactory<T> factory) {
        this.coreSize = coreSize;
        this.factory = factory;
        this.pool = new LinkedList<>();
        this.lock = new ReentrantLock();
    }

    public void release(T e, boolean err) {
        if (err) {
            this.factory.releaseObject(e);
            return;
        }

        try {
            lock.lock();
            if (pool.size() < this.coreSize) {
                pool.addLast(e);
                return;
            }
        } finally {
            lock.unlock();
        }
        //exceed core size, close it
        this.factory.releaseObject(e);
    }

    public T acquire() throws Exception {

        T e = null;
        try {
            lock.lock();
            while (!pool.isEmpty()) {
                e = pool.removeFirst();
                if (e == null) {
                    continue;
                }
                if (!this.factory.isValid(e)) {
                    this.factory.releaseObject(e);
                    e = null;
                    continue;
                }
                break;
            }
        } finally {
            lock.unlock();
        }

        return e == null ? this.factory.createObject() : e;
    }

    public void releaseAllObject() {
        try {
            lock.lock();
            for (T e : this.pool) {
                this.factory.releaseObject(e);
            }
            this.pool.clear();
        } finally {
            lock.unlock();
        }
    }

    public interface ObjectFactory<T> {

        public boolean isValid(T o);

        public T createObject() throws Exception;

        public void releaseObject(T e);
    }

}
