package org.michael.common;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created on 2019-09-16 11:14
 * Author : Michael.
 */
public class Accumulator {

    private final AtomicLong value;

    public Accumulator() {
        this(0);
    }

    public Accumulator(long initValue) {
        this.value = new AtomicLong(initValue);
    }

    public long getCurrentValue() {
        return value.get();
    }

    public long getAndReset() {
        return value.getAndSet(0);
    }

    public long incr(long delta) {
        return value.addAndGet(delta);
    }

    @Override
    public String toString() {
        return String.valueOf(value.get());
    }
}
