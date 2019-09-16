package org.michael.common;

import org.michael.common.utils.LockFileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileLock;

/**
 * Created on 2019-09-16 11:18
 * Author : Michael.
 */
public class FileLocker {

    private final String file;
    private volatile FileLock fileLock;

    public FileLocker(String file) {
        this.file = file;
        checkLockFile();
    }

    private void checkLockFile() {
        File f = new File(this.file);
        try {
            LockFileUtil.ensureLockFileExists(f);
            LockFileUtil.checkLockFile(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void lock() throws IOException {
        try {
            fileLock = LockFileUtil.lockFile(new File(file));
            if (fileLock == null) {
                throw new IOException("Lock file[" + file + "] already locked by other process");
            }
        } catch (IOException e) {
            throw new IOException("lock file[" + file + "] failed", e);
        }
    }

    public synchronized void unlock() {
        if (fileLock != null) {
            try {
                fileLock.release();
            } catch (IOException e) {
            }
        }
    }

}
