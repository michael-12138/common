package org.michael.common;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created on 2019-09-16 11:19
 * Author : Michael.
 */
public class GapMarker {

    private final AtomicLong lastMarkTime;

    public GapMarker(long initTime) {
        this.lastMarkTime = new AtomicLong(initTime);
    }

    public boolean conditionMeet(long now, long gap) {
        long tmp = now - lastMarkTime.get();
        return tmp >= gap;
    }

    public void markTime(long now) {
        this.lastMarkTime.set(now);
    }

}
