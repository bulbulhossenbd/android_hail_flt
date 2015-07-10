package de.rocket.flt.hail.demo.scene;

import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.Matrix;
import android.util.Size;
import android.widget.Toast;

import de.rocket.flt.hail.gl.GlFramebuffer;
import de.rocket.flt.hail.gl.GlProgram;
import de.rocket.flt.hail.gl.GlSampler;
import de.rocket.flt.hail.gl.GlTexture;
import de.rocket.flt.hail.gl.GlUtils;

public class DeferredAdvancedRendererFragment extends AdvancedRendererFragment {

    private static final int specularColor = Color.argb(255, 64, 64, 64);
    private static final int diffuseColor = Color.argb(255, 64, 64, 64);
    private static final float roughness = 40f;

    private GlSampler glSamplerNearest;
    private GlTexture glTextureGnormal;
    private GlTexture glTextureGdepth;
    private GlTexture glTextureGlight;
    private GlFramebuffer glFramebufferGnormal;
    private GlFramebuffer glFramebufferGlight;
    private GlProgram glProgramNormal;
    private GlProgram glProgramLight;
    private GlProgram glProgramOutput;

    private Size surfaceSize;

    private final float projMatInv[] = new float[16];

    private final UniformsNormal uniformsNormal = new UniformsNormal();
    private final UniformsLight uniformsLight = new UniformsLight();
    private final UniformsOutput uniformsOutput = new UniformsOutput();

    private final class UniformsNormal {
        public int uModelViewMat;
        public int uModelViewProjMat;
        public int uRoughness;
    }

    private final class UniformsLight {
        public int sNormal;
        public int sDepth;
        public int uDiffuse;
        public int uSpecular;
        public int uProjMatInv;
        public int uSurfaceSizeInv;
    }

    private final class UniformsOutput {
        public int uModelViewMat;
        public int uModelViewProjMat;
        public int sLight;
        public int uSurfaceSizeInv;
    }

    @Override
    public void onSurfaceCreated() {
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        GLES30.glCullFace(GLES30.GL_BACK);
        GLES30.glFrontFace(GLES30.GL_CCW);

        glSamplerNearest = new GlSampler()
                .parameter(GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST)
                .parameter(GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST)
                .parameter(GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE)
                .parameter(GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        glTextureGnormal = new GlTexture();
        glTextureGlight = new GlTexture();
        glTextureGdepth = new GlTexture();
        glFramebufferGnormal = new GlFramebuffer();
        glFramebufferGlight = new GlFramebuffer();

        prepareScene();

        try {
            glProgramNormal = new GlProgram(
                    GlUtils.loadString(getActivity(), "shaders/advanced/deferred/normal_vs.txt"),
                    GlUtils.loadString(getActivity(), "shaders/advanced/deferred/normal_fs.txt"),
                    null).useProgram().getUniformIndices(uniformsNormal);

            glProgramLight = new GlProgram(
                    GlUtils.loadString(getActivity(), "shaders/advanced/deferred/light_vs.txt"),
                    GlUtils.loadString(getActivity(), "shaders/advanced/deferred/light_fs.txt"),
                    null).useProgram().getUniformIndices(uniformsLight);
            GLES30.glUniform1i(uniformsLight.sNormal, 0);
            GLES30.glUniform1i(uniformsLight.sDepth, 1);

            glProgramOutput = new GlProgram(
                    GlUtils.loadString(getActivity(), "shaders/advanced/deferred/output_vs.txt"),
                    GlUtils.loadString(getActivity(), "shaders/advanced/deferred/output_fs.txt"),
                    null).useProgram().getUniformIndices(uniformsOutput);
            GLES30.glUniform1i(uniformsOutput.sLight, 0);

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
        surfaceSize = new Size(width, height);
        prepareCamera(width, height);

        Matrix.invertM(projMatInv, 0, getCamera().projMat(), 0);

        glTextureGnormal
                .bind(GLES30.GL_TEXTURE_2D)
                .texImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA16F, width, height, 0, GLES30.GL_RGBA, GLES30.GL_HALF_FLOAT, null)
                .unbind(GLES30.GL_TEXTURE_2D);
        glTextureGlight
                .bind(GLES30.GL_TEXTURE_2D)
                .texImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA16F, width, height, 0, GLES30.GL_RGBA, GLES30.GL_HALF_FLOAT, null)
                .unbind(GLES30.GL_TEXTURE_2D);
        glTextureGdepth
                .bind(GLES30.GL_TEXTURE_2D)
                .texImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_DEPTH_COMPONENT32F, width, height, 0, GLES30.GL_DEPTH_COMPONENT, GLES30.GL_FLOAT, null)
                .unbind(GLES30.GL_TEXTURE_2D);

        glFramebufferGnormal
                .bind(GLES30.GL_DRAW_FRAMEBUFFER)
                .texture2D(GLES30.GL_DRAW_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, glTextureGnormal.name(), 0)
                .texture2D(GLES30.GL_DRAW_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_TEXTURE_2D, glTextureGdepth.name(), 0)
                .drawBuffers(GLES30.GL_COLOR_ATTACHMENT0)
                .unbind(GLES30.GL_DRAW_FRAMEBUFFER);
        glFramebufferGlight
                .bind(GLES30.GL_DRAW_FRAMEBUFFER)
                .texture2D(GLES30.GL_DRAW_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, glTextureGlight.name(), 0)
                .drawBuffers(GLES30.GL_COLOR_ATTACHMENT0)
                .unbind(GLES30.GL_DRAW_FRAMEBUFFER);
    }

    @Override
    public void onRenderFrame() {
        glFramebufferGnormal.bind(GLES30.GL_DRAW_FRAMEBUFFER);
        GLES30.glClearColor(.0f, .0f, .0f, .0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        glProgramNormal.useProgram();
        GLES30.glUniform1f(uniformsNormal.uRoughness, roughness);
        renderScene(uniformsNormal.uModelViewMat, uniformsNormal.uModelViewProjMat);
        //glFramebufferGnormal.unbind(GLES30.GL_DRAW_FRAMEBUFFER);

        glFramebufferGlight.bind(GLES30.GL_DRAW_FRAMEBUFFER);
        glProgramLight.useProgram();
        GLES30.glUniform3f(uniformsLight.uDiffuse, Color.red(diffuseColor) / 255f,
                Color.green(diffuseColor) / 255f, Color.blue(diffuseColor) / 255f);
        GLES30.glUniform3f(uniformsLight.uSpecular, Color.red(specularColor) / 255f,
                Color.green(specularColor) / 255f, Color.blue(specularColor) / 255f);
        GLES30.glUniformMatrix4fv(uniformsLight.uProjMatInv, 1, false, projMatInv, 0);
        GLES30.glUniform2f(uniformsLight.uSurfaceSizeInv,
                1f / surfaceSize.getWidth(), 1f / surfaceSize.getHeight());
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        glTextureGnormal.bind(GLES30.GL_TEXTURE_2D);
        glSamplerNearest.bind(0);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        glTextureGdepth.bind(GLES30.GL_TEXTURE_2D);
        glSamplerNearest.bind(1);
        renderQuad();
        glFramebufferGlight.unbind(GLES30.GL_DRAW_FRAMEBUFFER);

        GLES30.glClearColor(0.72f, 0.70f, 0.60f, 1.0f);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        glProgramOutput.useProgram();
        GLES30.glUniform2f(uniformsOutput.uSurfaceSizeInv,
                1f / surfaceSize.getWidth(), 1f / surfaceSize.getHeight());
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        glTextureGlight.bind(GLES30.GL_TEXTURE_2D);
        glSamplerNearest.bind(0);
        renderScene(uniformsOutput.uModelViewMat, uniformsOutput.uModelViewProjMat);
        glTextureGlight.unbind(GLES30.GL_TEXTURE_2D);
    }

    @Override
    public void onSurfaceReleased() {
    }

}
