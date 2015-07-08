package de.rocket.flt.hail.gl;

import android.opengl.GLES31;

import java.nio.Buffer;

public class GlBuffer {

    private final int[] buffer = {0};

    public GlBuffer() {
        GLES31.glGenBuffers(1, buffer, 0);
        GlUtils.checkGLErrors();
    }

    public GlBuffer bind(int target) {
        GLES31.glBindBuffer(target, buffer[0]);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlBuffer unbind(int target) {
        GLES31.glBindBuffer(target, 0);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlBuffer data(int target, int sizeInBytes, Buffer data, int usage) {
        GLES31.glBufferData(target, sizeInBytes, data, usage);
        GlUtils.checkGLErrors();
        return this;
    }

}
