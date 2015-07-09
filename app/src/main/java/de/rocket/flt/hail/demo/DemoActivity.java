package de.rocket.flt.hail.demo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.RippleDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.rocket.flt.hail.R;
import de.rocket.flt.hail.demo.scene.DofAdvancedRendererFragment;
import de.rocket.flt.hail.demo.scene.RendererFragment;
import de.rocket.flt.hail.demo.scene.TwisterRendererFragment;

/**
 * Created by harism
 */
public class DemoActivity extends Activity implements RendererFragment.RendererHost {

    private enum Scene {
        NONE, TWISTER, TWISTER_TEXT, TWISTER_TEXT_RIPPLE, TWISTER_TEXT_UH,
        TWISTER_TEXT_OUT
    }

    private final static String EXTRA_RESOLUTION_WIDTH = "EXTRA_RESOLUTION_WIDTH";
    private final static String EXTRA_RESOLUTION_HEIGHT = "EXTRA_RESOLUTION_HEIGHT";
    private final static String EXTRA_SHOW_FPS = "EXTRA_SHOW_FPS";

    private TextView textView;
    private RippleDrawable rippleDrawable;

    private Scene scene = Scene.NONE;
    private MediaPlayer mediaPlayer;
    private Timer timer;

    public static Intent newIntent(Context context, int width, int height, boolean showFps) {
        Intent intent = new Intent(context, DemoActivity.class);
        intent.putExtra(EXTRA_RESOLUTION_WIDTH, width);
        intent.putExtra(EXTRA_RESOLUTION_HEIGHT, height);
        intent.putExtra(EXTRA_SHOW_FPS, showFps);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        textView = (TextView) findViewById(R.id.textview);
        rippleDrawable = (RippleDrawable) textView.getBackground();

        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor desc = getAssets().openFd("music/music.mp3");
            mediaPlayer.setDataSource(desc.getFileDescriptor(), desc.getStartOffset(), desc.getLength());
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.prepare();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Ooooops, something went rong  :(", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
        timer = new Timer();
        timer.scheduleAtFixedRate(new SceneTimerTask(), 0, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
        timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    private void onSceneTimerTask(int position) {
        if (position < 4000) {
            if (scene != Scene.TWISTER) {
                scene = Scene.TWISTER;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.renderer_in, R.animator.renderer_out);
                ft.replace(R.id.container, new TwisterRendererFragment());
                ft.commitAllowingStateLoss();
                textView.setAlpha(0f);
            }
        } else if (position < 10000) {
            if (scene != Scene.TWISTER_TEXT) {
                scene = Scene.TWISTER_TEXT;

                textView.setTextColor(0xFFFFFFFF);
                textView.setText("HAIL FAIRLIGHT");

                List<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f));
                animators.add(ObjectAnimator.ofFloat(textView, "scaleX", 4f, 1f));
                animators.add(ObjectAnimator.ofFloat(textView, "scaleY", 4f, 1f));

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(animators);
                animatorSet.setDuration(6000);
                animatorSet.start();
            }
        } else if (position < 19000) {
            if (scene != Scene.TWISTER_TEXT_RIPPLE) {
                scene = Scene.TWISTER_TEXT_RIPPLE;
                rippleDrawable.setHotspot(0, 0);
                rippleDrawable.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});
            }
        } else if (position < 20000) {
            if (scene != Scene.TWISTER_TEXT_UH) {
                scene = Scene.TWISTER_TEXT_UH;
                textView.setBackgroundColor(0x80FF4040);

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.renderer_in, R.animator.renderer_out);
                ft.replace(R.id.container, new DofAdvancedRendererFragment());
                ft.commitAllowingStateLoss();
            }
        } else if (position < 30000) {
            if (scene != Scene.TWISTER_TEXT_OUT) {
                scene = Scene.TWISTER_TEXT_OUT;
                textView.animate().alpha(0f).setDuration(2000).start();
            }
        }
    }

    private class SceneTimerTask extends TimerTask {

        private final SceneRunnable sceneRunnable = new SceneRunnable();

        @Override
        public void run() {
            runOnUiThread(sceneRunnable);
        }
    }

    private class SceneRunnable implements Runnable {
        @Override
        public void run() {
            onSceneTimerTask(mediaPlayer.getCurrentPosition());
        }
    }

}
