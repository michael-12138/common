package org.michael.common;

import java.util.concurrent.TimeUnit;

/**
 * Created on 2019-09-16 11:21
 * Author : Michael.
 */
public interface LifeCycle {

    public void startup();

    public void startFinish() throws InterruptedException;

    public void shutdown();

    public void awaitShutdown(long timeWait, TimeUnit timeUnit) throws InterruptedException;

    public String name();

}
