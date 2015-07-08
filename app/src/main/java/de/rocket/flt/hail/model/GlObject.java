package de.rocket.flt.hail.model;

import android.opengl.GLES31;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import de.rocket.flt.hail.gl.GlBuffer;

public class GlObject {

    private final int vertexCount;
    private final GlBuffer vertexBuffer;
    private final GlBuffer normalBuffer;
    private final GlBuffer textureBuffer;
    private final GlBuffer bboxBuffer;
    private final float bsphere[] = new float[4];

    public GlObject(GlObjectData objectData) {
        this(objectData.vertexCount(),
                objectData.vertexBuffer(), objectData.normalBuffer(), objectData.textureBuffer());
    }

    public GlObject(int vertexCount, FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer textureBuffer) {
        this.vertexCount = vertexCount;

        this.vertexBuffer = new GlBuffer()
                .bind(GLES31.GL_ARRAY_BUFFER)
                .data(GLES31.GL_ARRAY_BUFFER, 4 * 3 * vertexCount, vertexBuffer.position(0), GLES31.GL_STATIC_DRAW)
                .unbind(GLES31.GL_ARRAY_BUFFER);

        if (normalBuffer != null) {
            this.normalBuffer = new GlBuffer()
                    .bind(GLES31.GL_ARRAY_BUFFER)
                    .data(GLES31.GL_ARRAY_BUFFER, 4 * 3 * vertexCount, normalBuffer.position(0), GLES31.GL_STATIC_DRAW)
                    .unbind(GLES31.GL_ARRAY_BUFFER);
        } else {
            this.normalBuffer = null;
        }

        if (textureBuffer != null) {
            this.textureBuffer = new GlBuffer()
                    .bind(GLES31.GL_ARRAY_BUFFER)
                    .data(GLES31.GL_ARRAY_BUFFER, 4 * 2 * vertexCount, textureBuffer.position(0), GLES31.GL_STATIC_DRAW)
                    .unbind(GLES31.GL_ARRAY_BUFFER);
        } else {
            this.textureBuffer = null;
        }

        bboxBuffer = createBoundingBox(vertexCount, vertexBuffer);
    }

    public int vertexCount() {
        return vertexCount;
    }

    public GlBuffer vertexBuffer() {
        return vertexBuffer;
    }

    public GlBuffer normalBuffer() {
        return normalBuffer;
    }

    public GlBuffer textureBuffer() {
        return textureBuffer;
    }

    public GlBuffer bboxBuffer() {
        return bboxBuffer;
    }

    public float[] bsphere() {
        return bsphere;
    }

    private GlBuffer createBoundingBox(int vertexCount, FloatBuffer vertexBuffer) {
        final float vertex[] = {0, 0, 0};
        final float vertexMax[] = {Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE};
        final float vertexMin[] = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
        for (int i = 0; i < vertexCount; ++i) {
            vertexBuffer.get(vertex);
            for (int j = 0; j < 3; ++j) {
                if (vertex[j] > vertexMax[j]) {
                    vertexMax[j] = vertex[j];
                }
                if (vertex[j] < vertexMin[j]) {
                    vertexMin[j] = vertex[j];
                }
            }
        }
        vertexBuffer.position(0);

        float dx = (vertexMax[0] - vertexMin[0]) * 0.5f;
        float dy = (vertexMax[1] - vertexMin[1]) * 0.5f;
        float dz = (vertexMax[2] - vertexMin[2]) * 0.5f;
        bsphere[0] = vertexMin[0] + dx;
        bsphere[1] = vertexMin[1] + dy;
        bsphere[2] = vertexMin[2] + dz;
        bsphere[3] = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);

        final float[][] CUBEVERTICES = {
                {vertexMin[0], vertexMax[1], vertexMax[2]},
                {vertexMin[0], vertexMin[1], vertexMax[2]},
                {vertexMax[0], vertexMax[1], vertexMax[2]},
                {vertexMax[0], vertexMin[1], vertexMax[2]},
                {vertexMin[0], vertexMax[1], vertexMin[2]},
                {vertexMin[0], vertexMin[1], vertexMin[2]},
                {vertexMax[0], vertexMax[1], vertexMin[2]},
                {vertexMax[0], vertexMin[1], vertexMin[2]}};
        final int[][][] CUBEFILLED = {
                {{0, 1, 2, 1, 3, 2}},
                {{6, 7, 4, 7, 5, 4}},
                {{0, 4, 1, 4, 5, 1}},
                {{3, 7, 2, 7, 6, 2}},
                {{4, 0, 6, 0, 2, 6}},
                {{1, 5, 3, 5, 7, 3}}};

        FloatBuffer bufferVertices =
                ByteBuffer
                        .allocateDirect(3 * 4 * 6 * 6)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer();

        for (int indices[][] : CUBEFILLED) {
            for (int j = 0; j < indices[0].length; ++j) {
                bufferVertices.put(CUBEVERTICES[indices[0][j]]);
            }
        }

        return new GlBuffer()
                .bind(GLES31.GL_ARRAY_BUFFER)
                .data(GLES31.GL_ARRAY_BUFFER, 3 * 4 * 6 * 6, bufferVertices.position(0), GLES31.GL_STATIC_DRAW)
                .unbind(GLES31.GL_ARRAY_BUFFER);
    }

}
