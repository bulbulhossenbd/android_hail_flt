package de.rocket.flt.hail.model;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlObjectData {

    private final int vertexCount;
    private final FloatBuffer vertexBuffer;
    private final FloatBuffer normalBuffer;
    private final FloatBuffer textureBuffer;

    private GlObjectData(int vertexCount, FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer textureBuffer) {
        this.vertexCount = vertexCount;
        this.vertexBuffer = vertexBuffer;
        this.normalBuffer = normalBuffer;
        this.textureBuffer = textureBuffer;
    }

    public int vertexCount() {
        return vertexCount;
    }

    public FloatBuffer vertexBuffer() {
        return vertexBuffer;
    }

    public FloatBuffer normalBuffer() {
        return normalBuffer;
    }

    public FloatBuffer textureBuffer() {
        return textureBuffer;
    }

    public static GlObjectData loadObj(Context context, String path) throws IOException {
        Matcher matcher, matcherIndex;
        Pattern patternEmpty = Pattern.compile("^\\s*$");
        Pattern patternComment = Pattern.compile("^#.*");
        Pattern patternObject = Pattern.compile("^o .*");
        Pattern patternS = Pattern.compile("^s .*");
        Pattern patternVertex = Pattern.compile("^v\\s+([-.0-9]+)\\s+([-.0-9]+)\\s+([-.0-9]+)\\s*$");
        Pattern patternNormal = Pattern.compile("^vn\\s+([-.0-9]+)\\s+([-.0-9]+)\\s+([-.0-9]+)\\s*$");
        Pattern patternFace = Pattern.compile("^f\\s+([/0-9]+)\\s+([/0-9]+)\\s+([/0-9]+)\\s*$");
        Pattern patternFaceIndex = Pattern.compile("^([0-9]*)/([0-9]*)/([0-9]*)$");

        ArrayList<float[]> arrayVertices = new ArrayList<>();
        ArrayList<float[]> arrayNormals = new ArrayList<>();
        ArrayList<float[]> arrayTextures = new ArrayList<>();
        ArrayList<int[]> arrayFaces = new ArrayList<>();

        arrayVertices.add(new float[]{0, 0, 0});
        arrayNormals.add(new float[]{0, 0, 0});
        arrayTextures.add(new float[]{0, 0});

        String currentLine;
        InputStream assetStream = context.getAssets().open(path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetStream));
        while ((currentLine = bufferedReader.readLine()) != null) {
            if (patternEmpty.matcher(currentLine).matches()) {

            } else if (patternComment.matcher(currentLine).matches()) {

            } else if (patternObject.matcher(currentLine).matches()) {

            } else if (patternS.matcher(currentLine).matches()) {

            } else if ((matcher = patternVertex.matcher(currentLine)).matches()) {
                //Log.d("VERTEX", matcher.group(1) + "  " + matcher.group(2) + "  " + matcher.group(3));
                float[] values = new float[3];
                for (int i = 0; i < 3; ++i) {
                    values[i] = Float.parseFloat(matcher.group(i + 1));
                }
                arrayVertices.add(values);
            } else if ((matcher = patternNormal.matcher(currentLine)).matches()) {
                //Log.d("NORMAL", matcher.group(1) + "  " + matcher.group(2) + "  " + matcher.group(3));
                float[] values = new float[3];
                for (int i = 0; i < 3; ++i) {
                    values[i] = Float.parseFloat(matcher.group(i + 1));
                }
                arrayNormals.add(values);
            } else if ((matcher = patternFace.matcher(currentLine)).matches()) {
                //Log.d("FACE", matcher.group(1) + "  " + matcher.group(2) + "  " + matcher.group(3));
                int[] values = new int[9];
                for (int i = 0; i < 3; ++i) {
                    matcherIndex = patternFaceIndex.matcher(matcher.group(i + 1));
                    if (matcherIndex.matches()) {
                        for (int j = 0; j < 3; ++j) {
                            String indexString = matcherIndex.group(j + 1);
                            if (indexString.isEmpty()) {
                                indexString = "0";
                            }
                            values[i * 3 + j] = Integer.parseInt(indexString);
                        }
                    } else {
                        throw new IOException("Obj face error : " + matcher.group(i + 1));
                    }
                }
                arrayFaces.add(values);
            } else {
                throw new IOException("Obj file error : " + currentLine);
            }
        }

        int vertexCount = 3 * arrayFaces.size();
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(4 * 3 * vertexCount).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer normalBuffer = ByteBuffer.allocateDirect(4 * 3 * vertexCount).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer textureBuffer = ByteBuffer.allocateDirect(4 * 2 * vertexCount).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for (int[] face : arrayFaces) {
            for (int i = 0; i < 3; ++i) {
                vertexBuffer.put(arrayVertices.get(face[i * 3 + 0]));
                normalBuffer.put(arrayNormals.get(face[i * 3 + 2]));
                textureBuffer.put(arrayTextures.get(face[i * 3 + 1]));
            }
        }

        vertexBuffer.position(0);
        normalBuffer.position(0);
        textureBuffer.position(0);

        return new GlObjectData(vertexCount, vertexBuffer, normalBuffer, textureBuffer);
    }

    public static GlObjectData loadDat(Context context, String path) throws IOException {
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(context.getAssets().open(path)));

        int vertexCount = inputStream.readInt();
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(4 * 3 * vertexCount).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer normalBuffer = ByteBuffer.allocateDirect(4 * 3 * vertexCount).order(ByteOrder.nativeOrder()).asFloatBuffer();
        FloatBuffer textureBuffer = ByteBuffer.allocateDirect(4 * 2 * vertexCount).order(ByteOrder.nativeOrder()).asFloatBuffer();

        for (int i = 0; i < vertexCount * 3; ++i) {
            vertexBuffer.put(inputStream.readFloat());
        }
        for (int i = 0; i < vertexCount * 3; ++i) {
            normalBuffer.put(inputStream.readFloat());
        }
        for (int i = 0; i < vertexCount * 2; ++i) {
            textureBuffer.put(inputStream.readFloat());
        }

        vertexBuffer.position(0);
        normalBuffer.position(0);
        textureBuffer.position(0);

        return new GlObjectData(vertexCount, vertexBuffer, normalBuffer, textureBuffer);
    }

}
