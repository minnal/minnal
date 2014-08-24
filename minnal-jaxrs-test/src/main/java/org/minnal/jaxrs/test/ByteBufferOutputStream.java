package org.minnal.jaxrs.test;

import com.fasterxml.jackson.databind.util.ByteBufferBackedOutputStream;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends ByteBufferBackedOutputStream {

    public ByteBufferOutputStream(ByteBuffer buf) {
        super(buf);
    }

    public ByteBufferOutputStream(OutputStream outputStream) {
        super(((ByteBufferOutputStream) outputStream).getByteBuffer());
    }

    public ByteBuffer getByteBuffer() {
        return _b;
    }
}
