package call.ai.com.callsecretary;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

/**
 * Created by Administrator on 2017/3/28.
 */

public class TestActivity extends Activity {

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.layout_test);
        Button start = (Button) findViewById(R.id.btn1);
        Button remove = (Button) findViewById(R.id.btn2);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perssion();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity.this, FloatingWindowsService.class);
                stopService(intent);
            }
        });

    }

    private void perssion() {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
        } else {
            Intent intent = new Intent(TestActivity.this, FloatingWindowsService.class);
            startService(intent);
        }
    }

}
