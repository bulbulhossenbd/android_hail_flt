package de.rocket.flt.hail.demo.scene;

import android.opengl.GLES30;
import android.opengl.Matrix;
import android.opengl.Visibility;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import de.rocket.flt.hail.demo.MainApplication;
import de.rocket.flt.hail.model.GlCamera;
import de.rocket.flt.hail.model.GlObject;
import de.rocket.flt.hail.model.GlObjectData;

abstract class AdvancedRendererFragment extends RendererFragment {

    private ArrayList<Model> modelArray;
    private GlObject glObjectQuad;
    private GlCamera glCamera;

    private float modelMatrix[] = new float[16];
    private float modelViewMatrix[] = new float[16];
    private float modelViewProjMatrix[] = new float[16];

    private int cullResult[] = new int[1];

    protected GlCamera getCamera() {
        return glCamera;
    }

    protected void prepareScene() {
        GlObject objO = createGlObject("letter_o");
        GlObject objP = createGlObject("letter_p");
        GlObject objE = createGlObject("letter_e");
        GlObject objN = createGlObject("letter_n");
        GlObject objG = createGlObject("letter_g");
        GlObject objL = createGlObject("letter_l");
        GlObject objS = createGlObject("letter_s");
        GlObject obj3 = createGlObject("letter_3");
        GlObject objX = createGlObject("letter_x");
        GlObject objMountain = createGlObject("mountain");

        modelArray = new ArrayList<>();
        modelArray.add(new Model(0.0f, 0f, 0f, objO, 3f));
        modelArray.add(new Model(2.0f, 0f, 0f, objP, 3f));
        modelArray.add(new Model(4.0f, 0f, 0f, objE, 3f));
        modelArray.add(new Model(6.0f, 0f, 0f, objN, 3f));
        modelArray.add(new Model(8.0f, 0f, 0f, objG, 3f));
        modelArray.add(new Model(9.5f, 0f, 0f, objL, 3f));
        modelArray.add(new Model(11.8f, 0f, 0f, objE, 3f));
        modelArray.add(new Model(13.8f, 0f, 0f, objS, 3f));
        modelArray.add(new Model(17.0f, 0f, 0f, obj3, 3f));
        modelArray.add(new Model(19.0f, 0f, 0f, objX, 3f));
        modelArray.add(new Model(14.0f, -2f, -3f, objMountain, 1f));

        FloatBuffer verticesQuad = ByteBuffer.allocateDirect(4 * 3 * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesQuad.put(new float[]{-1, 1, 0, -1, -1, 0, 1, 1, 0, 1, -1, 0});
        glObjectQuad = new GlObject(4, verticesQuad, null, null);

        glCamera = new GlCamera();
    }

    protected void prepareCamera(int width, int height) {
        glCamera.setPerspective(width, height, 45f, 1f, 100f);
    }

    protected void renderScene(int uModelView, int uModelViewProj) {
        float t = SystemClock.uptimeMillis() % 20000 / 20000f;
        float x = (float) (Math.sin(t * Math.PI * 2.0) * 8.0) + 8.0f;
        float z = (float) (Math.cos(t * Math.PI * 2.0) * 2.0) + 5f;
        glCamera.setPos(new float[]{x, 0f, z});
        glCamera.setDir(new float[]{x + 1f, 0f, 0});
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glEnableVertexAttribArray(1);
        for (Model model : modelArray) {
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, model.x(), model.y(), model.z());
            Matrix.scaleM(modelMatrix, 0, model.scale(), model.scale(), model.scale());
            Matrix.multiplyMM(modelViewMatrix, 0, glCamera.viewMat(), 0, modelMatrix, 0);
            Matrix.multiplyMM(modelViewProjMatrix, 0, glCamera.projMat(), 0, modelViewMatrix, 0);

            if (Visibility.frustumCullSpheres(modelViewProjMatrix, 0,
                    model.glObject().bsphere(), 0, 1,
                    cullResult, 0, 1) == 0) {
                continue;
            }

            GLES30.glUniformMatrix4fv(uModelView, 1, false, modelViewMatrix, 0);
            GLES30.glUniformMatrix4fv(uModelViewProj, 1, false, modelViewProjMatrix, 0);

            model.glObject().vertexBuffer().bind(GLES30.GL_ARRAY_BUFFER);
            GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, 0);
            model.glObject().vertexBuffer().unbind(GLES30.GL_ARRAY_BUFFER);

            model.glObject().normalBuffer().bind(GLES30.GL_ARRAY_BUFFER);
            GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, 0, 0);
            model.glObject().normalBuffer().unbind(GLES30.GL_ARRAY_BUFFER);

            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, model.glObject.vertexCount());
        }
        GLES30.glDisableVertexAttribArray(0);
        GLES30.glDisableVertexAttribArray(1);
    }

    protected void renderQuad() {
        glObjectQuad.vertexBuffer().bind(GLES30.GL_ARRAY_BUFFER);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, 0);
        glObjectQuad.vertexBuffer().unbind(GLES30.GL_ARRAY_BUFFER);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        GLES30.glDisableVertexAttribArray(0);
    }

    private GlObject createGlObject(String key) {
        MainApplication app = (MainApplication) getActivity().getApplication();
        GlObjectData objData = app.getObjectData(key);
        return new GlObject(objData.vertexCount(),
                objData.vertexBuffer(), objData.normalBuffer(), null);
    }

    private class Model {
        private final float x;
        private final float y;
        private final float z;
        private final GlObject glObject;
        private final float scale;

        public Model(float x, float y, float z, GlObject glObject, float scale) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.glObject = glObject;
            this.scale = scale;
        }

        public float x() {
            return x;
        }

        public float y() {
            return y;
        }

        public float z() {
            return z;
        }

        public GlObject glObject() {
            return glObject;
        }

        public float scale() {
            return scale;
        }

    }

}
