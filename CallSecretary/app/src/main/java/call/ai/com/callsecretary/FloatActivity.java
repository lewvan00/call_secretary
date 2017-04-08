package call.ai.com.callsecretary;

import android.app.Activity;
import android.os.Bundle;

import call.ai.com.callsecretary.floating.FloatingWindow;

/**
 * Created by lewvan on 2017/4/9.
 */

public class FloatActivity extends Activity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FloatingWindow floatingWindow = new FloatingWindow(this);
        setContentView(floatingWindow);
    }
}
