package call.ai.com.callsecretary.floating;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.chat.ChatActivity;

/**
 * Created by Administrator on 2017/3/28.
 */

public class TestActivity extends Activity {

    FloatingWindowsService mFloatingService;
//
//    ServiceConnection mServiceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            mFloatingService = ((FloatingWindowsService.FloatingWindowsBinder) iBinder).getFloatingService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//
//        }
//    };

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.layout_test);

        Button start = (Button) findViewById(R.id.btn1);
        Button remove = (Button) findViewById(R.id.btn2);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatingService = new FloatingWindowsService();
                mFloatingService.showFloatingWindows("hhhh");
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(mServiceConnection);
    }
}
