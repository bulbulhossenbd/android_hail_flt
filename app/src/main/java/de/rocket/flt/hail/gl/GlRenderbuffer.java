package de.rocket.flt.hail.gl;

import android.opengl.GLES31;

public class GlRenderbuffer {

    private final int[] renderbuffer = {0};

    public GlRenderbuffer() {
        GLES31.glGenRenderbuffers(1, renderbuffer, 0);
        GlUtils.checkGLErrors();
    }

    public GlRenderbuffer bind(int target) {
        GLES31.glBindRenderbuffer(target, renderbuffer[0]);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlRenderbuffer unbind(int target) {
        GLES31.glBindRenderbuffer(target, 0);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlRenderbuffer storage(int target, int internalFormat, int width, int height) {
        GLES31.glRenderbufferStorage(target, internalFormat, width, height);
        GlUtils.checkGLErrors();
        return this;
    }

    public int name() {
        return renderbuffer[0];
    }

}
