package de.rocket.flt.hail.demo;

import android.app.Application;
import android.os.AsyncTask;
import android.renderscript.RenderScript;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

import de.greenrobot.event.EventBus;
import de.rocket.flt.hail.demo.event.GetProgressEvent;
import de.rocket.flt.hail.demo.event.SetProgressEvent;
import de.rocket.flt.hail.demo.view.ScriptC_BlurEffect;
import de.rocket.flt.hail.model.GlObjectData;

public class MainApplication extends Application {

    private static final String MODELS[] = {
            "letter_o", "models/letter_o.obj",
            "letter_p", "models/letter_p.obj",
            "letter_e", "models/letter_e.obj",
            "letter_n", "models/letter_n.obj",
            "letter_g", "models/letter_g.obj",
            "letter_l", "models/letter_l.obj",
            "letter_s", "models/letter_s.obj",
            "letter_3", "models/letter_3.obj",
            "letter_x", "models/letter_x.obj",
            "mountain", "models/mountain.obj"
    };

    private RenderScript renderScript;
    private ScriptC_BlurEffect scriptBlurEffect;
    private SetProgressEvent setProgressEvent;
    private HashMap<String, GlObjectData> mapObjects;

    @Override
    public void onCreate() {
        super.onCreate();
        renderScript = RenderScript.create(this);
        scriptBlurEffect = new ScriptC_BlurEffect(renderScript);
        setProgressEvent = new SetProgressEvent(0, 1);
        mapObjects = new HashMap<>();
        EventBus.getDefault().register(this);
        new ModelLoaderTask().execute(MODELS);
    }

    public RenderScript getRenderScript() {
        return renderScript;
    }

    public ScriptC_BlurEffect getBlurEffect() {
        return scriptBlurEffect;
    }

    public GlObjectData getObjectData(String key) {
        return mapObjects.get(key);
    }

    public void onEvent(GetProgressEvent event) {
        EventBus.getDefault().post(setProgressEvent);
    }

    private class ModelLoaderTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... strings) {
            for (int index = 0; index < strings.length; index += 2) {
                try {
                    String name = strings[index];
                    String path = strings[index + 1];
                    GlObjectData data = GlObjectData.loadObj(MainApplication.this, path);
                    mapObjects.put(name, data);
                    publishProgress((index + 2) / 2, strings.length / 2);
                } catch (IOException ex) {
                    return ex.getMessage();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            setProgressEvent = new SetProgressEvent(values[0], values[1]);
            EventBus.getDefault().post(setProgressEvent);
        }

        @Override
        protected void onPostExecute(String error) {
            if (error != null) {
                Toast.makeText(MainApplication.this, error, Toast.LENGTH_LONG).show();
            }
        }

    }

}
