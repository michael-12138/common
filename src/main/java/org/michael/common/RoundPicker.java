package org.michael.common;

import java.util.List;

/**
 * Created on 2019-09-16 11:25
 * Author : Michael.
 */
public class RoundPicker<E> {
    private final List<E> list;
    private final int endIndex;
    private int index = 0;
    private boolean end = false;

    public RoundPicker(List<E> list, int startIndex) {
        this.list = list;
        if (list.size() == 0) {
            end = true;
        }
        int realStartIndex = list.size() == 0 ? 0 : startIndex % list.size();
        this.index = realStartIndex;
        this.endIndex = realStartIndex;
    }

    public E next() {
        if (end) {
            return null;
        }
        E e = this.list.get(index);
        index++;
        if (index >= this.list.size()) {
            index = 0;
        }
        index = index % this.list.size();
        if (index == endIndex) {
            end = true;
        }
        return e;
    }
}
