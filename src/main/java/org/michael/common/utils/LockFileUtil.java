package org.michael.common.utils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Created on 2019-09-16 11:31
 * Author : Michael.
 */
public class LockFileUtil {

    public static void ensureLockFileExists(File fd) throws IOException {
        String parentPath = fd.getParent();
        File pd = new File(parentPath);
        if (!pd.exists()) {
            pd.mkdirs();
        }

        if (!pd.isDirectory()) {
            throw new IOException(pd.getAbsolutePath() + " is not a directory.");
        }

        if (!fd.exists()) {
            fd.createNewFile();
        }
    }

    public static boolean checkLockFile(File fd) {
        String parentPath = fd.getParent();
        File pd = new File(parentPath);

        if (!pd.exists()) {
            return false;
        }

        if (!pd.isDirectory()) {
            return false;
        }

        if (!fd.exists()) {
            return false;
        }
        return true;
    }

    public static FileLock lockFile(File fd) throws IOException {
        FileOutputStream fos = new FileOutputStream(fd);
        FileChannel channel = fos.getChannel();
        FileLock lock = null;
        boolean err = false;
        try {
            lock = channel.tryLock();
            if (lock != null) {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                bw.write(String.valueOf(SystemUtil.getPid()));
                bw.flush();
            }
            if (lock == null) {
                err = true;
            }
            return lock;
        } catch (IOException e) {
            err = true;
            throw e;
        } finally {
            if (err) {
                if (lock != null) {
                    lock.release();
                }
                IOUtil.closeQuietely(channel);
                IOUtil.closeQuietely(fos);
            }
        }

    }

}
