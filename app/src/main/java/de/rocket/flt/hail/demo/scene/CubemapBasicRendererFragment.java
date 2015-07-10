package de.rocket.flt.hail.demo.scene;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import de.rocket.flt.hail.egl.EglCore;
import de.rocket.flt.hail.gl.GlProgram;
import de.rocket.flt.hail.gl.GlSampler;
import de.rocket.flt.hail.gl.GlTexture;
import de.rocket.flt.hail.gl.GlUtils;
import de.rocket.flt.hail.model.GlCamera;

public class CubemapBasicRendererFragment extends BasicRendererFragment {

    private static final boolean enableLightning = true;
    private static final float MATERIAL_LIGHTNING_DISABLED[] = {1f, 0f, 0f, 1f};
    private static final float MATERIAL_LIGHTNING_ENABLED[] = {0.4f, 1.0f, 1.0f, 8.0f};

    private GlSampler glSamplerLinear;
    private GlTexture glTextureCubemap;
    private GlProgram glProgram;
    private GlCamera glCamera;

    private long lastRenderTime;

    private final float rotationMat[] = new float[16];
    private final float modelMat[] = new float[16];
    private final float modelViewMat[] = new float[16];
    private final float modelViewProjMat[] = new float[16];

    private final Uniforms uniforms = new Uniforms();

    private final class Uniforms {
        public int sTextureCube;
        public int uModelViewMat;
        public int uModelViewProjMat;
        public int uCameraPosition;
        public int uMaterial;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEglFlags(EglCore.FLAG_DEPTH_BUFFER);
    }

    @Override
    public void onSurfaceCreated() {
        super.onSurfaceCreated();

        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        GLES30.glCullFace(GLES30.GL_BACK);
        GLES30.glFrontFace(GLES30.GL_CCW);

        glSamplerLinear = new GlSampler();
        glSamplerLinear.parameter(GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        glSamplerLinear.parameter(GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        glSamplerLinear.parameter(GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        glSamplerLinear.parameter(GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        glCamera = new GlCamera();

        try {
            glTextureCubemap = readCubeMap();
            glProgram = new GlProgram(
                    GlUtils.loadString(getActivity(), "shaders/basic/cubemap/shader_vs.txt"),
                    GlUtils.loadString(getActivity(), "shaders/basic/cubemap/shader_fs.txt"),
                    null).useProgram().getUniformIndices(uniforms);
            GLES30.glUniform1i(uniforms.sTextureCube, 0);
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
        glCamera.setPerspective(width, height, 60.0f, 1f, 100f);
        glCamera.setPos(new float[]{0f, 0f, 40f});
        Matrix.setIdentityM(rotationMat, 0);
    }

    @Override
    public void onRenderFrame() {
        GLES30.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        glProgram.useProgram();

        int time = getMediaPlayer().getCurrentPosition();
        float diff = (time - lastRenderTime) / 1000f;
        lastRenderTime = time;

        Matrix.rotateM(rotationMat, 0, diff * 45f, 1f, 1.5f, 0f);
        Matrix.scaleM(modelMat, 0, rotationMat, 0, 20f, 10f, 10f);
        Matrix.multiplyMM(modelViewMat, 0, glCamera.viewMat(), 0, modelMat, 0);
        Matrix.multiplyMM(modelViewProjMat, 0, glCamera.projMat(), 0, modelViewMat, 0);

        GLES30.glUniformMatrix4fv(uniforms.uModelViewMat, 1, false, modelViewMat, 0);
        GLES30.glUniformMatrix4fv(uniforms.uModelViewProjMat, 1, false, modelViewProjMat, 0);
        GLES30.glUniform3fv(uniforms.uCameraPosition, 1, glCamera.pos(), 0);

        final float[] material = enableLightning ? MATERIAL_LIGHTNING_ENABLED : MATERIAL_LIGHTNING_DISABLED;
        GLES30.glUniform4fv(uniforms.uMaterial, 1, material, 0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        glTextureCubemap.bind(GLES30.GL_TEXTURE_CUBE_MAP);
        glSamplerLinear.bind(0);
        renderCubeFilled();
    }

    @Override
    public void onSurfaceReleased() {
    }

}
