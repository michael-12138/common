package org.michael.common;

import java.util.List;
import java.util.Map;

/**
 * Created on 2019-09-16 11:19
 * Author : Michael.
 */
public interface HearbeatAndAccumulatorExpireListener<T> {

    public void doSnapshot(Map<T, Map<String, Accumulator>> snap, List<String> errors);

    public void doExpired(Map<T, Map<String, Accumulator>> expired, List<String> errors);

    public void releaseResources();

    public void heartbeat(List<String> errors);

}
