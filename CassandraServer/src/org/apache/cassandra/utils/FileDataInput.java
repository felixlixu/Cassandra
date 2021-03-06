package org.apache.cassandra.utils;

import java.io.Closeable;
import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.cassandra.io.utils.FileMark;

public interface FileDataInput extends DataInput, Closeable {
    public String getPath();

    public boolean isEOF() throws IOException;

    public long bytesRemaining() throws IOException;

    public FileMark mark();

    public void reset(FileMark mark) throws IOException;

    public long bytesPastMark(FileMark mark);

    /**
     * Read length bytes from current file position
     * @param length length of the bytes to read
     * @return buffer with bytes read
     * @throws IOException if any I/O operation failed
     */
    public ByteBuffer readBytes(int length) throws IOException;
}
