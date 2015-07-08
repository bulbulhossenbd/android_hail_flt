package de.rocket.flt.hail.model;

import java.util.Arrays;

public class GlVector3 {

    private final float v[];

    public GlVector3() {
        this(0, 0, 0);
    }

    public GlVector3(float x, float y, float z) {
        v = new float[]{x, y, z};
    }

    public GlVector3(GlVector3 vec) {
        this(vec.v);
    }

    public GlVector3(float[] v) {
        this.v = Arrays.copyOf(v, 3);
    }

    public float x() {
        return v[0];
    }

    public float y() {
        return v[1];
    }

    public float z() {
        return v[2];
    }

    public float[] v() {
        return v;
    }

    public void setX(float x) {
        v[0] = x;
    }

    public void setY(float y) {
        v[1] = y;
    }

    public void setZ(float z) {
        v[2] = z;
    }

    public void set(GlVector3 vec) {
        set(vec.v);
    }

    public void set(float[] v) {
        System.arraycopy(v, 0, this.v, 0, 3);
    }

}
