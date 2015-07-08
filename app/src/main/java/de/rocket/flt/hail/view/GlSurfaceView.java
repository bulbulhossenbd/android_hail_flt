package de.rocket.flt.hail.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import de.rocket.flt.hail.util.GlRenderThread;
import de.rocket.flt.hail.util.GlRenderer;

public class GlSurfaceView extends SurfaceView {

    private static final String TAG = GlSurfaceView.class.getSimpleName();
    private final GlRenderThread mGlRenderThread;

    public GlSurfaceView(Context context) {
        this(context, null, 0, 0);
    }

    public GlSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public GlSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public GlSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mGlRenderThread = new GlRenderThread();
        mGlRenderThread.startAndWaitUntilReady();
        getHolder().addCallback(new GlSurfaceHolderCallback());
    }

    public void setEglContext(int eglVersion, int eglFlags) {
        mGlRenderThread.getGlRenderHandler().postCreateEglContext(eglVersion, eglFlags);
    }

    public void setGlRenderer(GlRenderer glRenderer) {
        mGlRenderThread.getGlRenderHandler().postSetRenderer(glRenderer);
    }

    public void renderFrame() {
        mGlRenderThread.getGlRenderHandler().postRenderFrame();
    }

    public void renderFrame(long frameTimeNanos) {
        mGlRenderThread.getGlRenderHandler().postRenderFrame(frameTimeNanos);
    }

    public void onDestroy() {
        mGlRenderThread.stopAndWaitUntilReady();
    }

    private class GlSurfaceHolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            mGlRenderThread.getGlRenderHandler().postCreateEglSurface(surfaceHolder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            mGlRenderThread.getGlRenderHandler().postResizeEglSurface(width, height);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            mGlRenderThread.getGlRenderHandler().postReleaseEglSurfaceAndWait();
        }

    }

}
