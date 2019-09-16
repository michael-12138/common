package org.michael.common;

import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 * Created on 2019-09-16 11:23
 * Author : Michael.
 */
public class MiniStopWatch {

    private static final long NANO_2_MILLIS = 1000000L;

    private long startTime;

    public MiniStopWatch(long startTime) {
        this.startTime = startTime;
    }

    public static MiniStopWatch createStarted() {
        return new MiniStopWatch(System.nanoTime());
    }

    public long getNanoTime() {
        return System.nanoTime() - startTime;
    }

    public void resetAndRestart() {
        this.startTime = System.nanoTime();
    }

    public long getTimeAndRestart() {
        long time = getTime();
        resetAndRestart();
        return time;
    }

    public long getTime() {
        return getNanoTime() / NANO_2_MILLIS;
    }

    @Override
    public String toString() {
        return DurationFormatUtils.formatDurationHMS(getTime());
    }
}
