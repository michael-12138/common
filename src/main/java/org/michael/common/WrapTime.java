package org.michael.common;

import java.util.Objects;

/**
 * Created on 2019-09-16 11:26
 * Author : Michael.
 */
public class WrapTime<T> {
    public final T value;
    public final long time;

    public WrapTime(T value, long time) {
        this.value = value;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WrapTime<?> wrapTime = (WrapTime<?>) o;
        return time == wrapTime.time && Objects.equals(value, wrapTime.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, time);
    }

    @Override
    public String toString() {
        return "WrapTime{" + "value=" + value + ", time=" + time + '}';
    }
}
