package de.rocket.flt.hail.gl;

import android.opengl.GLES31;

public class GlVertexArray {

    private final int[] vertexArray = {0};

    public GlVertexArray() {
        GLES31.glGenVertexArrays(1, vertexArray, 0);
        GlUtils.checkGLErrors();
    }

    public GlVertexArray bind() {
        GLES31.glBindVertexArray(vertexArray[0]);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlVertexArray unbind() {
        GLES31.glBindVertexArray(0);
        return this;
    }

}
