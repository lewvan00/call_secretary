package call.ai.com.callsecretary.floating;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import call.ai.com.callsecretary.R;

/**
 * Created by Administrator on 2017/3/28.
 */

public class TestActivity extends Activity {

    FloatingWindowsService mFloatingService;

    ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mFloatingService = ((FloatingWindowsService.FloatingWindowsBinder) iBinder).getFloatingService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.layout_test);
        Intent intent = new Intent(TestActivity.this, FloatingWindowsService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Button start = (Button) findViewById(R.id.btn1);
        Button remove = (Button) findViewById(R.id.btn2);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFloatingService != null) {
                    mFloatingService.showFloatingWindows("测试测试");
                }
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFloatingService != null) {
                    mFloatingService.hideFloatingWindows();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}
