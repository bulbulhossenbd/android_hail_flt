package de.rocket.flt.hail.gl;

import android.opengl.GLES31;

public class GlFramebuffer {

    private final int[] framebuffer = {0};

    public GlFramebuffer() {
        GLES31.glGenFramebuffers(1, framebuffer, 0);
        GlUtils.checkGLErrors();
    }

    public GlFramebuffer bind(int target) {
        GLES31.glBindFramebuffer(target, framebuffer[0]);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlFramebuffer unbind(int target) {
        GLES31.glBindFramebuffer(target, 0);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlFramebuffer renderbuffer(int target, int attachment, int renderbuffer) {
        GLES31.glFramebufferRenderbuffer(target, attachment, GLES31.GL_RENDERBUFFER, renderbuffer);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlFramebuffer texture2D(int target, int attachment, int textarget, int texture, int level) {
        GLES31.glFramebufferTexture2D(target, attachment, textarget, texture, level);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlFramebuffer drawBuffers(int... buffers) {
        GLES31.glDrawBuffers(buffers.length, buffers, 0);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlFramebuffer readBuffer(int buffer) {
        GLES31.glReadBuffer(buffer);
        GlUtils.checkGLErrors();
        return this;
    }

    public int checkStatus(int target) {
        GlUtils.checkGLErrors();
        return GLES31.glCheckFramebufferStatus(target);
    }

    public int name() {
        return framebuffer[0];
    }

}
