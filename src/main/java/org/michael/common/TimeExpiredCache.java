package org.michael.common;

import org.michael.common.utils.DateUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2019-09-16 11:25
 * Author : Michael.
 */
public class TimeExpiredCache<K, V> {

    private final ConcurrentHashMap<K, WrapTime<V>> map;
    private final Timer timer;

    public TimeExpiredCache(long expiredIntervalMs) {
        this.map = new ConcurrentHashMap<>();
        this.timer = new Timer("TimeExpiredCacheTimer", true);
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                doExpired();
            }
        }, DateUtil.timeOfSeconds(2), expiredIntervalMs);
    }

    private void doExpired() {
        List<K> expired = new LinkedList<>();
        long now = System.currentTimeMillis();
        for (Map.Entry<K, WrapTime<V>> e : map.entrySet()) {
            if (e.getValue().time < now) {
                expired.add(e.getKey());
            }
        }
        for (K key : expired) {
            this.map.remove(key);
        }
    }

    public void putToCache(K key, V value, long ttl) {
        long expiredTime = ttl + CurrentTimeGenerator.currentTimeMillisAtSecAccuracy();
        WrapTime<V> w = new WrapTime<>(value, expiredTime);
        this.map.put(key, w);
    }

    public V get(K key) {
        WrapTime<V> w = map.get(key);
        return w == null ? null : w.value;
    }

    public V remove(K key) {
        WrapTime<V> w = map.remove(key);
        return w == null ? null : w.value;
    }

}
