package org.michael.common;

import org.michael.common.utils.SystemUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created on 2019-09-16 11:13
 * Author : Michael.
 */
public abstract class AbstractRunner implements Runnable, LifeCycle {

    protected final String name;
    protected final Thread t;
    protected final AtomicBoolean running;
    protected final CountDownLatch shutdownLatch;
    protected final CountDownLatch startLatch;

    public AbstractRunner(String name) {
        this.name = name;
        this.t = new Thread(this, name);
        this.running = new AtomicBoolean(false);
        this.shutdownLatch = new CountDownLatch(1);
        this.startLatch = new CountDownLatch(1);
    }

    public void startup() {
        this.running.set(true);
        this.t.start();
    }

    public void startFinish() throws InterruptedException {
        this.startLatch.await();
    }

    public void shutdown() {
        this.running.set(false);
        this.t.interrupt();
    }

    public void awaitShutdown(long time, TimeUnit timeUnit) throws InterruptedException {
        this.shutdownLatch.await(time, timeUnit);
    }

    protected void markStartFinished() {
        startLatch.countDown();
    }

    @Override
    public void run() {
        markStartFinished();
        perform();
        shutdownLatch.countDown();
    }

    protected void doLongSleep(long intervalMs, long sleepTimeMs) {
        long endTime = System.currentTimeMillis() + sleepTimeMs;
        while (isRunning()) {
            SystemUtil.sleepQuietly(intervalMs);
            long now = CurrentTimeGenerator.currentTimeMillisAtSecAccuracy();
            if (now >= endTime) {
                break;
            }
        }
    }

    protected boolean isRunning() {
        return this.running.get();
    }

    protected abstract void perform();

    @Override
    public String name() {
        return name;
    }
}
