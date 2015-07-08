package de.rocket.flt.hail.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.rocket.flt.hail.util.GlRenderer;
import de.rocket.flt.hail.view.GlTextureView;

public abstract class GlTextureViewFragment extends Fragment implements GlRenderer {

    private static final String KEY_EGL_VERSION = "KEY_EGL_VERSION";
    private static final String KEY_EGL_FLAGS = "KEY_EGL_FLAGS";

    private final Choreographer.FrameCallback frameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            glTextureView.renderFrame(frameTimeNanos);
            choreographer.postFrameCallback(this);
        }
    };

    private Choreographer choreographer;
    private GlTextureView glTextureView;
    private boolean continuousRendering;


    public GlTextureViewFragment(int eglVersion, int eglFlags) {
        Bundle args = new Bundle();
        args.putInt(KEY_EGL_VERSION, eglVersion);
        args.putInt(KEY_EGL_FLAGS, eglFlags);
        setArguments(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        choreographer = Choreographer.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        glTextureView = new GlTextureView(getActivity());
        glTextureView.setEglContext(args.getInt(KEY_EGL_VERSION), args.getInt(KEY_EGL_FLAGS));
        glTextureView.setGlRenderer(this);
        return glTextureView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (continuousRendering) {
            choreographer.postFrameCallback(frameCallback);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        choreographer.removeFrameCallback(frameCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        glTextureView.onDestroy();
    }

    public final void setContinuousRendering(boolean continuousRendering) {
        this.continuousRendering = continuousRendering;
        if (continuousRendering) {
            choreographer.postFrameCallback(frameCallback);
        } else {
            choreographer.removeFrameCallback(frameCallback);
        }
    }

    public final void requestRender() {
        glTextureView.renderFrame();
    }

}
