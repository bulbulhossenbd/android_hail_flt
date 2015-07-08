package de.rocket.flt.hail.model;

import android.opengl.Matrix;

public class GlCamera {

    private final GlVector3 posVec = new GlVector3();
    private final GlVector3 dirVec = new GlVector3();

    private final float[] viewMat = new float[16];
    private final float[] projMat = new float[16];
    private final float[] viewProjMat = new float[16];

    private boolean calcViewMat;
    private boolean calcViewProjMat;

    private float sensorHeight;
    private float sensorWidth;
    private float focalLength;
    private float planeInFocus;
    private float apertureDiameter;

    public GlCamera() {
        sensorHeight = 0.024f;
    }

    public GlCamera setPerspective(int width, int height, float fov, float zNear, float zFar) {
        float aspect = (float) width / height;
        Matrix.perspectiveM(projMat, 0, fov, aspect, zNear, zFar);
        calcViewProjMat = true;

        sensorWidth = sensorHeight * aspect;
        focalLength = (float) ((0.5f * sensorWidth) / Math.tan(Math.PI * fov / 360));

        return this;
    }

    public GlCamera setPos(GlVector3 posVec) {
        return setPos(posVec.v());
    }

    public GlCamera setPos(float[] pos) {
        calcViewMat = true;
        posVec.set(pos);
        return this;
    }

    public GlCamera setDir(GlVector3 dirVec) {
        return setDir(dirVec.v());
    }

    public GlCamera setDir(float[] dir) {
        calcViewMat = true;
        dirVec.set(dir);
        return this;
    }

    public GlVector3 posVec() {
        return posVec;
    }

    public float[] pos() {
        return posVec.v();
    }

    public GlVector3 dirVec() {
        return dirVec;
    }

    public float[] dir() {
        return dirVec.v();
    }

    public float[] viewMat() {
        if (calcViewMat) {
            Matrix.setLookAtM(viewMat, 0,
                    posVec.x(), posVec.y(), posVec.z(),
                    dirVec.x(), dirVec.y(), dirVec.z(),
                    0f, 1f, 0f);
            calcViewMat = false;
        }
        return viewMat;
    }

    public float[] projMat() {
        return projMat;
    }

    public float[] viewProjMat() {
        float[] viewMat = viewMat();
        if (calcViewProjMat) {
            Matrix.multiplyMM(viewProjMat, 0, projMat, 0, viewMat, 0);
            calcViewProjMat = false;
        }
        return viewProjMat;
    }


    public float focalLength() {
        return focalLength;
    }

    public float planeInFocus() {
        return planeInFocus;
    }

    public void setPlaneInFocus(float planeInFocus) {
        this.planeInFocus = planeInFocus;
    }

    public float apertureDiameter() {
        return apertureDiameter;
    }

    public void setApertureDiameter(float apertureDiameter) {
        this.apertureDiameter = apertureDiameter;
    }

    public float sensorHeight() {
        return sensorHeight;
    }

    public float getSensorWidth() {
        return sensorWidth;
    }

}
