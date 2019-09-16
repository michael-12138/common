package org.michael.common.utils;

import java.io.File;

/**
 * Created on 2019-09-16 11:30
 * Author : Michael.
 */
public class FileUtil {

    public static long fileLength(File file, long defaultValueOnError) {
        if (!file.exists()) {
            return defaultValueOnError;
        } else if (!file.isFile()) {
            return defaultValueOnError;
        }
        long len = file.length();
        return len;
    }

    public static long fileLength(String file, long defaultValueOnError) {
        File f = new File(file);
        return fileLength(f, defaultValueOnError);
    }
}
