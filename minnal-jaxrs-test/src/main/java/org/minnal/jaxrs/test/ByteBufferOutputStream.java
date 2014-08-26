package org.minnal.jaxrs.test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream {
    protected final ByteBuffer buffer;

    public ByteBufferOutputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int b) throws IOException {
        buffer.put((byte) b);
    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        buffer.put(bytes, off, len);
    }

    /**
     * @return buffer
     */
    public ByteBuffer getBuffer() {
        return buffer;
    }
}
