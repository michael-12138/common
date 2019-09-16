package org.michael.common;

import java.util.LinkedHashMap;

/**
 * Created on 2019-09-16 11:22
 * Author : Michael.
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {

    protected final int maxElements;

    public LRUCache(int maxSize) {
        super(maxSize, 0.75F, true);
        maxElements = maxSize;
    }

    protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
        return size() > maxElements;
    }

}
