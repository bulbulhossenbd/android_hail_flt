package de.rocket.flt.hail.demo.scene;

import android.graphics.PointF;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Size;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import de.rocket.flt.hail.egl.EglCore;
import de.rocket.flt.hail.gl.GlProgram;
import de.rocket.flt.hail.gl.GlUtils;

public class TunnelRendererFragment extends RendererFragment {

    private static final int CIRCLE_COUNT = 16;
    private static final float CIRCLE_RADIUS = 0.1f;

    private Size size;
    private GlProgram glProgramCircle;
    private ByteBuffer verticesQuad;

    private final float[] projMat = new float[16];
    private final float[] modelViewMat = new float[16];
    private final float[] modelViewProjMat = new float[16];

    private final PointF targetPos = new PointF();
    private final PointF[] circleArr = new PointF[CIRCLE_COUNT];

    private int inPosition;
    private int uModelViewProjMat;
    private int uCircleColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEglFlags(EglCore.FLAG_DEPTH_BUFFER);

        final byte[] VERTICES = {-1, 1, -1, -1, 1, 1, 1, -1};
        verticesQuad = ByteBuffer.allocateDirect(VERTICES.length).order(ByteOrder.nativeOrder());
        verticesQuad.put(VERTICES).position(0);

        for (int ii = 0; ii < circleArr.length; ++ii) {
            circleArr[ii] = new PointF();
        }
    }

    @Override
    public void onSurfaceCreated() {
        try {
            String vs = GlUtils.loadString(getActivity(), "shaders/blackandwhite/tunnel/circle.vs");
            String fs = GlUtils.loadString(getActivity(), "shaders/blackandwhite/tunnel/circle.fs");
            glProgramCircle = new GlProgram(vs, fs, null).useProgram();
            inPosition = glProgramCircle.getAttribLocation("inPosition");
            uModelViewProjMat = glProgramCircle.getUniformLocation("uModelViewProjMat");
            uCircleColor = glProgramCircle.getUniformLocation("uCircleColor");

            setContinuousRendering(true);
        } catch (final Exception ex) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        size = new Size(width, height);
        float aspect = (float) height / width;
        Matrix.orthoM(projMat, 0, -1f, 1f, -aspect, aspect, 1f, 100f);
        targetPos.set(0f, 0f);
    }

    @Override
    public void onRenderFrame() {
        GLES30.glViewport(0, 0, size.getWidth(), size.getHeight());
        GLES30.glClearColor(1f, 1f, 1f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        glProgramCircle.useProgram();
        GLES30.glVertexAttribPointer(inPosition, 2, GLES30.GL_BYTE, false, 0, verticesQuad);
        GLES30.glEnableVertexAttribArray(inPosition);

        int time = getMediaPlayer().getCurrentPosition();
        float t1 = time % 18323 / 18323f;
        float t2 = time % 20492 / 20492f;
        float t3 = time % 30280 / 30280f;
        float t4 = time % 35949 / 35949f;
        targetPos.x = (float) (Math.sin(2 * t1 * Math.PI) * Math.sin(t3 * Math.PI));
        targetPos.y = (float) (Math.cos(2 * t2 * Math.PI) * Math.sin(t4 * Math.PI)) * .5f;

        for (int ii = 0; ii < circleArr.length; ++ii) {
            PointF c1 = targetPos;
            if (ii > 0) {
                c1 = circleArr[ii - 1];
            }
            PointF c2 = circleArr[ii];

            float dx = c1.x - c2.x;
            float dy = c1.y - c2.y;
            float l = (float) Math.sqrt(dx * dx + dy * dy);
            if (l > 0f) {
                dx /= l;
                dy /= l;
                l = l * l * 0.35f;
                c2.offset(dx * l, dy * l);
            }
        }

        for (int ii = 0; ii < circleArr.length; ++ii) {
            float depth = ii + 1f;
            float scale = CIRCLE_RADIUS + CIRCLE_RADIUS * ii;
            float color = (ii & 1) == 1 ? 1f : 0f;
            Matrix.setIdentityM(modelViewMat, 0);
            Matrix.translateM(modelViewMat, 0, circleArr[ii].x, circleArr[ii].y, -depth);
            Matrix.scaleM(modelViewMat, 0, scale, scale, 1f);
            Matrix.multiplyMM(modelViewProjMat, 0, projMat, 0, modelViewMat, 0);
            GLES30.glUniformMatrix4fv(uModelViewProjMat, 1, false, modelViewProjMat, 0);
            GLES30.glUniform3f(uCircleColor, color, color, color);
            GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        }
    }

    @Override
    public void onSurfaceReleased() {

    }

}
