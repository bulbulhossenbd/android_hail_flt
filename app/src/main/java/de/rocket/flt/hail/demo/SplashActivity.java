package de.rocket.flt.hail.demo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import de.greenrobot.event.EventBus;
import de.rocket.flt.hail.R;
import de.rocket.flt.hail.demo.event.GetProgressEvent;
import de.rocket.flt.hail.demo.event.SetProgressEvent;
import de.rocket.flt.hail.demo.view.FadeImageView;
import de.rocket.flt.hail.demo.view.FadeTextView;

/**
 * Created by harism
 */
public class SplashActivity extends Activity {

    private static final int ANIM_DURATION = 1000;
    private static final int ANIM_DELAY = 500;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.seekbar_progress);

        FadeTextView tv = (FadeTextView) findViewById(R.id.textview_title);
        tv.setAlphaLeft(0f);
        tv.setAlphaRight(0f);
        tv.setBlurLeft(1f);
        tv.setBlurRight(1f);

        FadeImageView iv = (FadeImageView) findViewById(R.id.imageview_icon);
        iv.setAlphaLeft(0f);
        iv.setAlphaRight(0f);
        iv.setBlurLeft(1f);
        iv.setBlurRight(1f);

        AnimatorSet animSet = new AnimatorSet();
        AnimatorSet animSetTv = createFade(tv);
        AnimatorSet animSetIv = createFade(iv);
        animSetIv.setStartDelay(ANIM_DELAY);
        animSet.playTogether(animSetTv, animSetIv);
        animSet.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new GetProgressEvent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
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

    public void onEventMainThread(SetProgressEvent event) {
        progressBar.setProgress(event.getProgress());
        progressBar.setMax(event.getMaxProgress());
        if (event.getProgress() >= event.getMaxProgress()) {
            finish();
            startActivity(new Intent(this, CreditsActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
    }

    private AnimatorSet createFade(View v) {
        ObjectAnimator animAlphaL = ObjectAnimator.ofFloat(v, "alphaLeft", .0f, .5f, 1.f, 1.f).setDuration(ANIM_DURATION);
        ObjectAnimator animAlphaR = ObjectAnimator.ofFloat(v, "alphaRight", .0f, .0f, .5f, 1.f).setDuration(ANIM_DURATION);
        ObjectAnimator animBlurL = ObjectAnimator.ofFloat(v, "blurLeft", 1.f, .5f, 0.f, .0f).setDuration(ANIM_DURATION);
        ObjectAnimator animBlurR = ObjectAnimator.ofFloat(v, "blurRight", 1.f, 1.f, .5f, .0f).setDuration(ANIM_DURATION);
        animBlurL.setStartDelay(ANIM_DELAY);
        animBlurR.setStartDelay(ANIM_DELAY);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(animBlurL).with(animBlurR).with(animAlphaL).with(animAlphaR);
        return animSet;
    }

}
