package org.michael.common;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created on 2019-09-16 11:17
 * Author : Michael.
 */
public class CurrentTimeGenerator {

    static final Timer timer = new Timer(true);
    static final AtomicLong currentTime = new AtomicLong(System.currentTimeMillis());
    static final AtomicBoolean inited = new AtomicBoolean(false);

    static {
        init();
    }

    private synchronized static void init() {
        if (!inited.get()) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    currentTime.set(System.currentTimeMillis());
                }
            }, 0, 500);
            inited.set(true);
        }
    }

    public static long currentTimeMillisAtSecAccuracy() {
        return currentTime.get();
    }

}
