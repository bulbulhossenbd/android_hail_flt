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
import de.rocket.flt.hail.demo.scene.CubemapBasicRendererFragment;
import de.rocket.flt.hail.demo.scene.DeferredAdvancedRendererFragment;
import de.rocket.flt.hail.demo.scene.DofAdvancedRendererFragment;
import de.rocket.flt.hail.demo.scene.OcclusionBasicRendererFragment;
import de.rocket.flt.hail.demo.scene.RendererFragment;
import de.rocket.flt.hail.demo.scene.TunnelRendererFragment;
import de.rocket.flt.hail.demo.scene.TwisterRendererFragment;

/**
 * Created by harism
 */
public class DemoActivity extends Activity implements RendererFragment.RendererHost {

    private enum Scene {
        NONE,
        TWISTER,
        TWISTER_TEXT,
        TWISTER_TEXT_RIPPLE_OFF,
        TWISTER_TEXT_RIPPLE_ON,
        TWISTER_TEXT_UH,
        TWISTER_TEXT_OUT,
        GLES3X_COLOR_GREEN,
        GLES3X_COLOR_PINK,
        TUNNEL,
        CUBEMAP,
        DEFERRED,
        DEFERRED_TWO,
        OCCLUSION_QUERY,
        OCCLUSION_QUERY_GREETINGS
    }

    private TextView textView;
    private RippleDrawable rippleDrawable;

    private Scene scene = Scene.NONE;
    private MediaPlayer mediaPlayer;
    private Timer timer;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, DemoActivity.class);
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
        } else if (position < 9500) {
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
                animatorSet.setDuration(5000);
                animatorSet.start();

                final int STATES[][] = {{android.R.attr.state_pressed, android.R.attr.state_enabled}};
                final int COLORS[] = {0xFF3366FF};
                rippleDrawable.setColor(new ColorStateList(STATES, COLORS));

                rippleDrawable.setHotspot(0, 0);
                rippleDrawable.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});
            }
        } else if (position < 14000) {
            if (scene != Scene.TWISTER_TEXT_RIPPLE_OFF) {
                scene = Scene.TWISTER_TEXT_RIPPLE_OFF;
                rippleDrawable.setState(new int[]{});
            }
        } else if (position < 19000) {
            if (scene != Scene.TWISTER_TEXT_RIPPLE_ON) {
                scene = Scene.TWISTER_TEXT_RIPPLE_ON;
                final int STATES[][] = {{android.R.attr.state_pressed, android.R.attr.state_enabled}};
                final int COLORS[] = {0xFF404040};
                rippleDrawable.setColor(new ColorStateList(STATES, COLORS));

                rippleDrawable.setHotspot(textView.getWidth(), textView.getHeight());
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
        } else if (position < 28000) {
            if (scene != Scene.TWISTER_TEXT_OUT) {
                scene = Scene.TWISTER_TEXT_OUT;
                textView.animate().alpha(0f).setDuration(2000).start();
            }
        } else if (position < 37000) {
            if (scene != Scene.GLES3X_COLOR_GREEN) {
                scene = Scene.GLES3X_COLOR_GREEN;

                textView.setText("");
                textView.setAlpha(1f);
                textView.setBackground(rippleDrawable);

                final int STATES[][] = {{android.R.attr.state_pressed, android.R.attr.state_enabled}};
                final int COLORS[] = {0x4040FF40};
                rippleDrawable.setColor(new ColorStateList(STATES, COLORS));

                rippleDrawable.setHotspot(textView.getWidth() * .5f, textView.getHeight() * .5f);
                rippleDrawable.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});
            }
        } else if (position < 46000) {
            if (scene != Scene.GLES3X_COLOR_PINK) {
                scene = Scene.GLES3X_COLOR_PINK;

                final int STATES[][] = {{android.R.attr.state_pressed, android.R.attr.state_enabled}};
                final int COLORS[] = {0x40FF4040};
                rippleDrawable.setColor(new ColorStateList(STATES, COLORS));
            }
        } else if (position < 64000) {
            if (scene != Scene.TUNNEL) {
                scene = Scene.TUNNEL;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.renderer_in, R.animator.renderer_out);
                ft.replace(R.id.container, new TunnelRendererFragment());
                ft.commitAllowingStateLoss();
            }
        } else if (position < 83000) {
            if (scene != Scene.CUBEMAP) {
                scene = Scene.CUBEMAP;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.renderer_in, R.animator.renderer_out);
                ft.replace(R.id.container, new CubemapBasicRendererFragment());
                ft.commitAllowingStateLoss();
            }
        } else if (position < 92000) {
            if (scene != Scene.DEFERRED) {
                scene = Scene.DEFERRED;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.renderer_in, R.animator.renderer_out);
                ft.replace(R.id.container, new DeferredAdvancedRendererFragment());
                ft.commitAllowingStateLoss();
            }
        } else if (position < 120000) {
            if (scene != Scene.DEFERRED_TWO) {
                scene = Scene.DEFERRED_TWO;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.renderer_in, R.animator.renderer_out);
                ft.replace(R.id.container, new DeferredAdvancedRendererFragment());
                ft.commitAllowingStateLoss();
            }
        } else if (position < 121000) {
            if (scene != Scene.OCCLUSION_QUERY) {
                scene = Scene.OCCLUSION_QUERY;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.renderer_in, R.animator.renderer_out);
                ft.replace(R.id.container, new OcclusionBasicRendererFragment());
                ft.commitAllowingStateLoss();
            }
        } else if (position < 150000) {
            if (scene != Scene.OCCLUSION_QUERY_GREETINGS) {
                scene = Scene.OCCLUSION_QUERY_GREETINGS;
                textView.setText("greetings smash destop");
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
