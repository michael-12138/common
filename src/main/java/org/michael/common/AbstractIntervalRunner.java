package org.michael.common;

import org.michael.common.utils.SystemUtil;

/**
 * Created on 2019-09-16 11:13
 * Author : Michael.
 */
public abstract class AbstractIntervalRunner extends AbstractRunner {

    protected final long performIntervalMilSec;
    protected final long startDelayMilSec;

    public AbstractIntervalRunner(String name, long performIntervalMilSec, long startDelayMilSec) {
        super(name);
        this.performIntervalMilSec = performIntervalMilSec;
        this.startDelayMilSec = startDelayMilSec;
    }

    @Override
    protected final void perform() {
        onStart();
        SystemUtil.sleepQuietly(startDelayMilSec);
        long lastPerform = 0;
        while (isRunning()) {
            SystemUtil.sleepQuietly(200);
            long now = System.currentTimeMillis();
            long gap = now - lastPerform;
            if (gap < this.performIntervalMilSec) {
                continue;
            }
            lastPerform = now;
            intervalPerform();
        }
        onStop();
    }

    protected abstract void onStart();

    protected abstract void intervalPerform();

    protected abstract void onStop();
}
