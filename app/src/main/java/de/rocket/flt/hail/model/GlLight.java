package de.rocket.flt.hail.model;

import android.opengl.Matrix;

public class GlLight {

    private final GlVector3 posVec = new GlVector3();
    private final GlVector3 dirVec = new GlVector3();

    private final float biasMat[] = new float[16];
    private final float viewMat[] = new float[16];
    private final float projMat[] = new float[16];
    private final float viewProjMat[] = new float[16];
    private final float shadowMat[] = new float[16];

    private boolean calcViewMat;
    private boolean calcViewProjMat;

    public GlLight() {
        Matrix.setIdentityM(biasMat, 0);
        Matrix.translateM(biasMat, 0, 0.5f, 0.5f, 0.5f);
        Matrix.scaleM(biasMat, 0, 0.5f, 0.5f, 0.5f);
    }

    public GlLight setPerspective(int width, int height, float fovy, float zNear, float zFar) {
        float aspect = (float) width / height;
        Matrix.perspectiveM(projMat, 0, fovy, aspect, zNear, zFar);
        calcViewProjMat = true;
        return this;
    }

    public GlLight setPos(GlVector3 posVector) {
        return setPos(posVector.v());
    }

    public GlLight setPos(float[] pos) {
        calcViewMat = true;
        posVec.set(pos);
        return this;
    }

    public GlLight setDir(GlVector3 dirVector) {
        return setDir(dirVector.v());
    }

    public GlLight setDir(float[] dir) {
        calcViewMat = true;
        dirVec.set(dir);
        return this;
    }

    public float[] viewMat() {
        if (calcViewMat) {
            Matrix.setLookAtM(viewMat, 0,
                    posVec.x(), posVec.y(), posVec.z(),
                    dirVec.x(), dirVec.y(), dirVec.z(),
                    0f, 1f, 0f);
            calcViewMat = false;
            calcViewProjMat = true;
        }
        return viewMat;
    }

    public float[] projMat() {
        return projMat;
    }

    public float[] viewProjMat() {
        final float[] viewMat = viewMat();
        if (calcViewProjMat) {
            Matrix.multiplyMM(viewProjMat, 0, projMat, 0, viewMat, 0);
            calcViewProjMat = false;
        }
        return viewProjMat;
    }

    public float[] shadowMat(float viewMatrix[]) {
        Matrix.invertM(shadowMat, 0, viewMatrix, 0);
        Matrix.multiplyMM(shadowMat, 0, viewProjMat(), 0, shadowMat, 0);
        Matrix.multiplyMM(shadowMat, 0, biasMat, 0, shadowMat, 0);
        return shadowMat;
    }


}
