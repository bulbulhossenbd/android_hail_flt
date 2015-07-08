package de.rocket.flt.hail.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * Created by harism
 */
public class DemoActivity extends Activity {

    private final static String EXTRA_RESOLUTION_WIDTH = "EXTRA_RESOLUTION_WIDTH";
    private final static String EXTRA_RESOLUTION_HEIGHT = "EXTRA_RESOLUTION_HEIGHT";
    private final static String EXTRA_SHOW_FPS = "EXTRA_SHOW_FPS";

    private MediaPlayer mediaPlayer;

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

        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor desc = getAssets().openFd("music/music.mp3");
            mediaPlayer.setDataSource(desc.getFileDescriptor(), desc.getStartOffset(), desc.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Ooooops, something went rong  :(", Toast.LENGTH_LONG).show();
            finish();
        }
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

}
