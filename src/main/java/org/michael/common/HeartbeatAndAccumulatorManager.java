package org.michael.common;

import org.michael.common.utils.SystemUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created on 2019-09-16 11:20
 * Author : Michael.
 */
public class HeartbeatAndAccumulatorManager<T> extends AccumulatorPool<T> implements LifeCycle, Runnable {

    private final long expireMilSec;
    private final HearbeatAndAccumulatorExpireListener listener;
    private final Thread t;
    private final AtomicBoolean running;
    private final CountDownLatch shutdownLatch;
    private final long hbIntervalMilSec;

    public HeartbeatAndAccumulatorManager(String[] accumulatorNames, long hbIntervalMilSec, long expireMilsec, HearbeatAndAccumulatorExpireListener listener,
                                          int errorQueueSize) {
        super(accumulatorNames, errorQueueSize);
        this.hbIntervalMilSec = hbIntervalMilSec;
        this.expireMilSec = expireMilsec;
        this.listener = listener;
        this.t = new Thread(this, "HeartbeatAndAccumulatorManager");
        this.running = new AtomicBoolean(false);
        this.shutdownLatch = new CountDownLatch(1);
    }

    @Override
    public void startup() {
        this.running.set(true);
        this.t.start();
    }

    @Override
    public void startFinish() {

    }

    @Override
    public void shutdown() {
        this.running.set(false);
    }

    @Override
    public void awaitShutdown(long timeWait, TimeUnit timeUnit) throws InterruptedException {
        this.shutdownLatch.await(timeWait, timeUnit);
    }

    @Override
    public String name() {
        return null;
    }

    private boolean isRunning() {
        return this.running.get();
    }

    @Override
    public void run() {
        try {
            while (isRunning()) {
                int expiredSize = runListener();
                int sleepTime = expiredSize == 0 ? 200 : 10;
                if (expiredSize == 0) {
                    doHeartbeat();
                }
                if (isRunning()) {
                    SystemUtil.sleepQuietly(sleepTime);
                }
            }
        } finally {
            this.listener.releaseResources();
            this.shutdownLatch.countDown();
        }
    }

    long lastHbTime = CurrentTimeGenerator.currentTimeMillisAtSecAccuracy();

    private void doHeartbeat() {
        long now = CurrentTimeGenerator.currentTimeMillisAtSecAccuracy();
        long gap = now - lastHbTime;
        if (gap < this.hbIntervalMilSec) {
            return;
        }
        lastHbTime = now;
        List<String> errors = this.fetchErrors();
        this.listener.heartbeat(errors);
    }

    private int runListener() {
        try {
            long now = CurrentTimeGenerator.currentTimeMillisAtSecAccuracy();
            long time = now - expireMilSec;
            Map<T, Map<String, Accumulator>> expired = this.getAndRemoveExpiredAccumulators(time);
            if (expired.size() > 0) {
                List<String> errors = this.fetchErrors();
                this.listener.doExpired(expired, errors);
            }

            long snapTime = now - 60 * 1000L;
            Map<T, Map<String, Accumulator>> snap = this.getSnapshot(snapTime);
            if (snap.size() > 0) {
                List<String> errors = this.fetchErrors();
                this.listener.doSnapshot(snap, errors);
            }
            int size = snap.size() + expired.size();
            return size;
        } catch (Exception e) {
            //Should not happen, Listener should handle their exceptions.
            return 0;
        }
    }
}
