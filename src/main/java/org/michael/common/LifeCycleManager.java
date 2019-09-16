package org.michael.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2019-09-16 11:22
 * Author : Michael.
 */
public class LifeCycleManager {

    private static Logger logger = LoggerFactory.getLogger(LifeCycleManager.class);

    private final Map<Integer, LinkedList<LifeCycle>> lifeCycles;

    public LifeCycleManager() {
        this.lifeCycles = new HashMap<>();
    }

    public void register(LifeCycle lc, Integer priority) {
        LinkedList<LifeCycle> list = this.lifeCycles.get(priority);
        if (list == null) {
            list = new LinkedList<>();
            this.lifeCycles.put(priority, list);
        }
        list.addLast(lc);
    }

    public void startAll() throws InterruptedException {
        List<Integer> p = sortPriority();
        for (int i = p.size() - 1; i > 0; i--) {
            Integer priority = p.get(i);
            LinkedList<LifeCycle> list = this.lifeCycles.get(priority);
            for (LifeCycle lc : list) {
                lc.startup();
                lc.startFinish();
            }
        }
    }

    public void shopAll(long time, TimeUnit timeUnit) {
        List<Integer> p = sortPriority();
        for (Integer priority : p) {
            LinkedList<LifeCycle> list = this.lifeCycles.get(priority);
            for (LifeCycle lc : list) {
                lc.shutdown();
                try {
                    lc.awaitShutdown(time, timeUnit);
                } catch (InterruptedException e) {
                    logger.warn("Shutdown life cycle [ {} ] timeout.", lc.name());
                }
            }
        }
    }

    private List<Integer> sortPriority() {
        List<Integer> p = new ArrayList<>(this.lifeCycles.size());
        p.addAll(this.lifeCycles.keySet());
        Collections.sort(p);
        return p;
    }
}
