package org.michael.common;

import java.util.LinkedList;
import java.util.List;

/**
 * Created on 2019-09-16 11:22
 * Author : Michael.
 */
public class LineBuffer {

    private static final byte BYTE_NL = (byte) 10;

    private final int originalBufSize;

    private final byte[] buffer;

    private int startPos;
    private int limit;

    public LineBuffer(int bufSize) {
        this.originalBufSize = bufSize;
        this.buffer = new byte[bufSize];
    }

    public void parseLines() {
        int index = startPos;
        int end = limit;

        List<Integer> nlPoses = new LinkedList<>();
        for (int i = index; i < limit; i++) {
            if (buffer[i] == BYTE_NL) {
                nlPoses.add(i);
            }
        }

        if (nlPoses.size() == 0) {
            moveBufToHead(index, end);
            return;
        }

        int cIndex = index;
        for (Integer nlPos : nlPoses) {
            byte[] lineBuf = copyLine(buffer, cIndex, nlPos);
            cIndex = nlPos + 1;
        }
        moveBufToHead(cIndex, end);

    }

    private void moveBufToHead(int newStart, int limit) {
        int headIndex = 0;
        if (newStart == headIndex) {
            return;
        }

        if (newStart >= this.buffer.length) {
            this.startPos = 0;
            this.limit = 0;
            return;
        }

        int len = limit - newStart;
        while (newStart < limit) {
            this.buffer[headIndex] = this.buffer[newStart];
            headIndex++;
            newStart++;
        }

        this.startPos = headIndex;
        this.limit = len;
    }

    private byte[] copyLine(byte[] buf, int start, int nlPos) {
        int len = nlPos - start;
        byte[] lineBuf = new byte[nlPos - start];

        System.arraycopy(buf, start, lineBuf, 0, len);
        return lineBuf;
    }

}
