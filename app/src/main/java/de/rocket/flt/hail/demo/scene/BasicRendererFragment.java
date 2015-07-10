package de.rocket.flt.hail.demo.scene;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import de.rocket.flt.hail.gl.GlTexture;
import de.rocket.flt.hail.gl.GlVertexArray;
import de.rocket.flt.hail.model.GlObject;

public abstract class BasicRendererFragment extends RendererFragment {

    private GlObject glObjectCube;
    private GlVertexArray glVertexArrayCube;

    @Override
    public void onSurfaceCreated() {
        // Vertex and normal data plus indices arrays.
        final float[][] CUBEVERTICES = {
                {-1, 1, 1}, {-1, -1, 1}, {1, 1, 1}, {1, -1, 1},
                {-1, 1, -1}, {-1, -1, -1}, {1, 1, -1}, {1, -1, -1}};
        final float[][] CUBENORMALS = {
                {0, 0, 1}, {0, 0, -1}, {-1, 0, 0},
                {1, 0, 0}, {0, 1, 0}, {0, -1, 0}};
        final int[][][] CUBEFILLED = {
                {{0, 1, 2, 1, 3, 2}, {0}},
                {{6, 7, 4, 7, 5, 4}, {1}},
                {{0, 4, 1, 4, 5, 1}, {2}},
                {{3, 7, 2, 7, 6, 2}, {3}},
                {{4, 0, 6, 0, 2, 6}, {4}},
                {{1, 5, 3, 5, 7, 3}, {5}}};

        FloatBuffer bufferVertices =
                ByteBuffer.allocateDirect(3 * 4 * 6 * 6)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer bufferNormals =
                ByteBuffer.allocateDirect(3 * 4 * 6 * 6)
                        .order(ByteOrder.nativeOrder()).asFloatBuffer();

        for (int indices[][] : CUBEFILLED) {
            for (int j = 0; j < indices[0].length; ++j) {
                bufferVertices.put(CUBEVERTICES[indices[0][j]]);
                bufferNormals.put(CUBENORMALS[indices[1][0]]);
            }
        }

        bufferVertices.position(0);
        bufferNormals.position(0);

        glObjectCube = new GlObject(36, bufferVertices, bufferNormals, null);

        glVertexArrayCube = new GlVertexArray().bind();

        glObjectCube.vertexBuffer().bind(GLES30.GL_ARRAY_BUFFER);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glEnableVertexAttribArray(0);

        glObjectCube.normalBuffer().bind(GLES30.GL_ARRAY_BUFFER);
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, 0, 0);
        GLES30.glEnableVertexAttribArray(1);

        glVertexArrayCube.unbind();
    }

    protected GlObject getObjectCube() {
        return glObjectCube;
    }

    protected void renderCubeFilled() {
        glVertexArrayCube.bind();
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
        glVertexArrayCube.unbind();
    }

    protected void renderCubeOutlines() {
        glVertexArrayCube.bind();
        for (int i = 0; i < 36; i += 3) {
            GLES30.glDrawArrays(GLES30.GL_LINE_LOOP, i, 3);
        }
        glVertexArrayCube.unbind();
    }

    protected GlTexture readCubeMap() throws IOException {
        GlTexture glTexture = new GlTexture();
        glTexture.bind(GLES30.GL_TEXTURE_CUBE_MAP);

        readTexture(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_X, "images/cubemap/posx.png");
        readTexture(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, "images/cubemap/posy.png");
        readTexture(GLES30.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, "images/cubemap/posz.png");
        readTexture(GLES30.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, "images/cubemap/negx.png");
        readTexture(GLES30.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, "images/cubemap/negy.png");
        readTexture(GLES30.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, "images/cubemap/negz.png");

        glTexture.unbind(GLES30.GL_TEXTURE_CUBE_MAP);
        return glTexture;
    }

    private void readTexture(int target, String path) throws IOException {
        InputStream is = null;
        try {
            is = new BufferedInputStream(getResources().getAssets().open(path));
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            GLUtils.texImage2D(target, 0, bitmap, 0);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

}
