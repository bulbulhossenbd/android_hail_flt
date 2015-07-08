package de.rocket.flt.hail.gl;

import android.opengl.GLES31;

public class GlQuery {

    private final int query[] = {0};

    public GlQuery() {
        GLES31.glGenQueries(1, query, 0);
        GlUtils.checkGLErrors();
    }

    public GlQuery begin(int target) {
        GLES31.glBeginQuery(target, query[0]);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlQuery end(int target) {
        GLES31.glEndQuery(target);
        GlUtils.checkGLErrors();
        return this;
    }

    public GlQuery getObjectuiv(int pname, int params[]) {
        GLES31.glGetQueryObjectuiv(query[0], pname, params, 0);
        GlUtils.checkGLErrors();
        return this;
    }

}
