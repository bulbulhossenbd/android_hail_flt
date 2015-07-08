package de.rocket.flt.hail.demo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;

import de.rocket.flt.hail.R;

/**
 * Created by harism
 */
public class SettingsActivity extends Activity {

    private static final String PREFERENCE_RESOLUTION = "PREFERENCE_RESOLUTION";
    private static final String PREFERENCE_SHOW_FPS = "PREFERENCE_SHOW_FPS";
    final int[] RADIOBUTTON_IDS = {R.id.radiobutton_1080p, R.id.radiobutton_720p};

    private RadioGroup radioGroupResolution;
    private CheckBox checkBoxFps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        radioGroupResolution = (RadioGroup) findViewById(R.id.radiogroup_resolution);
        checkBoxFps = (CheckBox) findViewById(R.id.checkbox_fps);

        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        radioGroupResolution.check(RADIOBUTTON_IDS[prefs.getInt(PREFERENCE_RESOLUTION, 0)]);
        checkBoxFps.setChecked(prefs.getBoolean(PREFERENCE_SHOW_FPS, false));

        findViewById(R.id.button_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int width = 0;
                int height = 0;

                SharedPreferences.Editor prefsEditor = getPreferences(MODE_PRIVATE).edit();
                int resolutionId = radioGroupResolution.getCheckedRadioButtonId();
                for (int i = 0; i < RADIOBUTTON_IDS.length; ++i) {
                    if (RADIOBUTTON_IDS[i] == resolutionId) {
                        prefsEditor.putInt(PREFERENCE_RESOLUTION, i);
                        switch (i) {
                            case 0:
                                width = 1920;
                                height = 1080;
                                break;
                            case 1:
                                width = 1280;
                                height = 720;
                                break;
                        }
                        break;
                    }
                }

                boolean showFps = checkBoxFps.isChecked();
                prefsEditor.putBoolean(PREFERENCE_SHOW_FPS, showFps);
                prefsEditor.commit();

                startActivity(DemoActivity.newIntent(SettingsActivity.this, width, height, showFps));
                finish();
            }
        });
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
