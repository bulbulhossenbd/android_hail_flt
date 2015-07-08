package de.rocket.flt.hail.gl;

import android.opengl.GLES31;

import java.nio.Buffer;

public class GlTexture {

    private final int[] texture = {0};

    public GlTexture() {
        GLES31.glGenTextures(1, texture, 0);
        GlUtils.checkGLErrors();
    }

    public int name() {
        return texture[0];
    }

    public GlTexture bind(int target) {
        GLES31.glBindTexture(target, texture[0]);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlTexture unbind(int target) {
        GLES31.glBindTexture(target, 0);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlTexture texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, Buffer data) {
        GLES31.glTexImage2D(target, level, internalFormat, width, height, border, format, type, data);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlTexture parameter(int target, int pname, float pvalue) {
        GLES31.glTexParameterf(target, pname, pvalue);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlTexture parameter(int target, int pname, int pvalue) {
        GLES31.glTexParameteri(target, pname, pvalue);
        GlUtils.checkGLErrors();
        return this;
    }

}
