package de.rocket.flt.hail.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.rocket.flt.hail.gl.GlUtils;

public class GlCameraAnimator {

    private final ArrayList<Position> mPositions = new ArrayList<>();

    public void addPosition(float posX, float posY, float posZ, float lookAtX, float lookAtY, float lookAtZ, float time) {
        Position position = new Position();
        position.mPosition[0] = posX;
        position.mPosition[1] = posY;
        position.mPosition[2] = posZ;
        position.mLookAt[0] = lookAtX;
        position.mLookAt[1] = lookAtY;
        position.mLookAt[2] = lookAtZ;
        position.mTime = time;
        mPositions.add(position);
    }

    public void prepare() {
        Collections.sort(mPositions, new Comparator<Position>() {
            @Override
            public int compare(Position position, Position position2) {
                return position.mTime < position2.mTime ? -1 : 1;
            }
        });

        for (int index = 0; index < mPositions.size() - 1; ++index) {
            Position p1 = mPositions.get(index);
            Position p2 = mPositions.get(index + 1);
            p1.mPositionDist = distanceSqrt(p1.mPosition, p2.mPosition);
            p1.mLookAtDist = distanceSqrt(p1.mLookAt, p2.mLookAt);
        }
    }

    public void interpolate(GlCamera camera, float time) {
        int index = 0;
        for (; index < mPositions.size() - 1; ++index) {
            if (mPositions.get(index).mTime > time) {
                break;
            }
        }

        Position p0 = mPositions.get(index - 2);
        Position p1 = mPositions.get(index - 1);
        Position p2 = mPositions.get(index + 0);
        Position p3 = mPositions.get(index + 1);

        final float[] pX = {p0.mPosition[0], p1.mPosition[0], p2.mPosition[0], p3.mPosition[0]};
        final float[] pY = {p0.mPosition[1], p1.mPosition[1], p2.mPosition[1], p3.mPosition[1]};
        final float[] pZ = {p0.mPosition[2], p1.mPosition[2], p2.mPosition[2], p3.mPosition[2]};
        final float[] laX = {p0.mLookAt[0], p1.mLookAt[0], p2.mLookAt[0], p3.mLookAt[0]};
        final float[] laY = {p0.mLookAt[1], p1.mLookAt[1], p2.mLookAt[1], p3.mLookAt[1]};
        final float[] laZ = {p0.mLookAt[2], p1.mLookAt[2], p2.mLookAt[2], p3.mLookAt[2]};
        final float[] tP = {0, p0.mPositionDist, p0.mPositionDist + p1.mPositionDist, p0.mPositionDist + p1.mPositionDist + p2.mPositionDist};
        final float[] tLa = {0, p0.mLookAtDist, p0.mLookAtDist + p1.mLookAtDist, p0.mLookAtDist + p1.mLookAtDist + p2.mLookAtDist};
        final float t = (time - p1.mTime) / (p2.mTime - p1.mTime);

        float posX = GlUtils.interpolate(pX, tP, t);
        float posY = GlUtils.interpolate(pY, tP, t);
        float posZ = GlUtils.interpolate(pZ, tP, t);
        float lookAtX = GlUtils.interpolate(laX, tLa, t);
        float lookAtY = GlUtils.interpolate(laY, tLa, t);
        float lookAtZ = GlUtils.interpolate(laZ, tLa, t);

        //Log.d("ERR", "posDist2=" + p2.mPositionDist);
        //Log.d("POS", "x=" + posX + " y=" + posY + " z=" + posZ);

        camera.setPos(new float[]{posX, posY, posZ});
        camera.setDir(new float[]{lookAtX, lookAtY, lookAtZ});
    }

    private final float distanceSqrt(float[] p1, float[] p2) {
        float dx = p2[0] - p1[0];
        float dy = p2[1] - p1[1];
        float dz = p2[2] - p1[2];
        return (float) (Math.sqrt(Math.sqrt(dx * dx + dy * dy + dz * dz)));
    }

    private class Position {
        public final float[] mPosition = {0, 0, 0};
        public float mPositionDist = 0;
        public final float[] mLookAt = {0, 0, 0};
        public float mLookAtDist = 0;
        public float mTime;
    }
}
