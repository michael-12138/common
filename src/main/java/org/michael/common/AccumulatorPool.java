package org.michael.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 2019-09-16 11:14
 * Author : Michael.
 */
public class AccumulatorPool<T> {

    private final ConcurrentHashMap<T, TimeWrapper> accumulators;
    private final String[] accumulatorNames;
    private final FifoQueue<String> accumulateErrors;

    public AccumulatorPool(String[] accumulatorNames, int errorQueueSize) {
        this.accumulators = new ConcurrentHashMap<>();
        this.accumulatorNames = accumulatorNames;
        this.accumulateErrors = new FifoQueue<>(errorQueueSize);
    }

    private TimeWrapper getAccumulator(T key) {
        TimeWrapper ac = this.accumulators.get(key);
        if (ac == null) {
            synchronized (this.accumulators) {
                ac = this.accumulators.get(key);
                if (ac == null) {
                    Map<String, Accumulator> tmp = new ConcurrentHashMap<>();
                    for (String name : this.accumulatorNames) {
                        tmp.put(name, new Accumulator());
                    }
                    ac = new TimeWrapper(System.currentTimeMillis(), tmp);
                    this.accumulators.put(key, ac);
                }
            }
        }
        return ac;
    }

    public void reportError(String errorMsg) {
        this.accumulateErrors.add(errorMsg);
    }

    public List<String> fetchErrors() {
        return this.accumulateErrors.getAndClearQueue();
    }

    public void incr(T key, String accumulatorName, long delta) {
        TimeWrapper a = getAccumulator(key);
        Accumulator ac = a.name2accumulator.get(accumulatorName);
        if (ac != null) {
            ac.incr(delta);
        }
    }

    public Map<T, Map<String, Accumulator>> getSnapshot(long expiredSnapshotTime) {

        Map<T, Map<String, Accumulator>> result = new HashMap<>();
        long now = System.currentTimeMillis();
        for (Map.Entry<T, TimeWrapper> e : this.accumulators.entrySet()) {
            if (e.getValue().snapshotTime < expiredSnapshotTime) {
                result.put(e.getKey(), e.getValue().snap(now));
            }
        }
        return result;
    }

    public Map<T, Map<String, Accumulator>> getAndRemoveExpiredAccumulators(long expiredTime) {

        Map<T, Map<String, Accumulator>> result = new HashMap<>();

        for (Map.Entry<T, TimeWrapper> e : this.accumulators.entrySet()) {
            if (e.getValue().time < expiredTime) {
                result.put(e.getKey(), e.getValue().name2accumulator);
            }
        }

        if (result.size() > 0) {
            synchronized (this.accumulators) {
                for (T t : result.keySet()) {
                    this.accumulators.remove(t);
                }
            }
        }
        return result;
    }

    static class TimeWrapper {
        final long time;
        volatile long snapshotTime;
        final Map<String, Accumulator> name2accumulator;

        public TimeWrapper(long time, Map<String, Accumulator> name2accumulator) {
            this.time = time;
            this.name2accumulator = name2accumulator;
            this.snapshotTime = time;
        }

        public Map<String, Accumulator> snap(long snapshotTime) {
            this.snapshotTime = snapshotTime;
            Map<String, Accumulator> snap = new HashMap<>();
            for (Map.Entry<String, Accumulator> e : this.name2accumulator.entrySet()) {
                snap.put(e.getKey(), new Accumulator(e.getValue().getAndReset()));
            }
            return snap;
        }
    }
}
