package org.michael.common;

/**
 * Created on 2019-09-16 11:24
 * Author : Michael.
 */
public class Picker<T> {

    final T[] elements;
    private volatile int next = 0;

    public Picker(T[] elements) {
        this.elements = elements;
    }

    public T next() {
        synchronized (this) {
            next++;
            next = next % elements.length;
            return this.elements[next];
        }
    }
}
