package call.ai.com.callsecretary;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.lex.interactionkit.Response;
import com.amazonaws.mobileconnectors.lex.interactionkit.ui.InteractiveVoiceView;
import com.amazonaws.services.lexrts.model.PostContentResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import lex.InteractiveVoiceUtils;
import lex.SerializablePostContentResult;

/**
 * Created by lewvan on 2017/4/8.
 */

public class MainActivity extends Activity implements  InteractiveVoiceView.InteractiveVoiceListener {
    Handler mMainHandler;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        final EditText editText = (EditText) findViewById(R.id.service_ip);
        editText.setText("10.60.200.60");
        mMainHandler = new Handler();
        Button btn = (Button) findViewById(R.id.set_ip_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonSharedPref.getInstance(MainActivity.this).setServiceIp(editText.getText().toString());
                SocketHelper.getInstance().init(MainActivity.this.getApplicationContext(), mMainHandler);
                InteractiveVoiceUtils.getInstance().start(MainActivity.this, null, null);
            }
        });
    }

    @Override
    public void dialogReadyForFulfillment(Map<String, String> slots, String intent) {

    }

    @Override
    public void onResponse(final Response response) {
        Log.d("liufan", "onResponse");
        final SerializablePostContentResult result = new SerializablePostContentResult();
        PostContentResult realResult = response.getResult();
        result.setRealResult(realResult);
        Log.d("liufan", "response = " + result);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "response = " + result, Toast.LENGTH_LONG).show();
            }
});
        SocketHelper.getInstance().sendMsgToSocket(result);
        InputStream audioStream = realResult.getAudioStream();
        final byte buffer[] = new byte[16384];
        int length;
        try {
            while ((length = audioStream.read(buffer)) != -1) {
                SocketHelper.getInstance().sendMsgToSocket(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                audioStream.close();
            } catch (IOException e) {
                e.printStackTrace();

        }
    }

    @Override
    public void onError(final String responseText, final Exception e) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "onError = " + responseText
                        + ", error = " + e, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        InteractiveVoiceUtils.getInstance().finish();
    }
}
