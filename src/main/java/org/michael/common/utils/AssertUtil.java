package org.michael.common.utils;

/**
 * Created on 2019-09-16 11:28
 * Author : Michael.
 */
public class AssertUtil {

    public static void assertNotNull(Object o, String msg) {
        if (o == null) {
            throw new RuntimeException(msg);
        }
    }

    public static void assertTrue(boolean condition, String msg) {
        if (condition == false) {
            throw new RuntimeException(msg);
        }
    }

}
