package org.michael.common;

import org.michael.common.utils.IOUtil;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2019-09-16 11:25
 * Author : Michael.
 */
public class StreamGobbler implements Runnable {

    private final InputStream in;
    private final OutputStream out;
    private final Thread t;
    private volatile boolean finished = false;
    private volatile boolean err = false;
    private volatile Exception exception = null;
    private final CountDownLatch shutdown;

    public StreamGobbler(String name, InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
        this.t = new Thread(this, name);
        this.shutdown = new CountDownLatch(1);
    }

    public void startup() {
        this.t.start();
    }

    public void forceKill() {
        IOUtil.closeQuietely(in);
        IOUtil.closeQuietely(out);
    }

    @Override
    public void run() {

        BufferedReader br = new BufferedReader(new InputStreamReader(this.in));
        BufferedWriter bw = out == null ? null : new BufferedWriter(new OutputStreamWriter(this.out));
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (out != null) {
                    bw.write(line);
                    bw.newLine();
                }
            }
            if (out != null) {
                bw.flush();
            }
        } catch (IOException e) {
            err = true;
            this.exception = e;
        } finally {
            IOUtil.closeQuietely(br);
            IOUtil.closeQuietely(bw);
            IOUtil.closeQuietely(in);
            IOUtil.closeQuietely(out);
        }
        finished = true;
        this.shutdown.countDown();
    }

    public void awaitShutdown(long time, TimeUnit timeUnit) throws InterruptedException {
        this.shutdown.await(time, timeUnit);
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isErr() {
        return err;
    }

    public void setErr(boolean err) {
        this.err = err;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
