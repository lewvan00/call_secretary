package call.ai.com.callsecretary;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.lex.interactionkit.InteractionClient;
import com.amazonaws.mobileconnectors.lex.interactionkit.Response;
import com.amazonaws.mobileconnectors.lex.interactionkit.ui.InteractiveVoiceView;
import com.amazonaws.services.lexrts.model.PostContentResult;

import java.util.Map;

import call.ai.com.callsecretary.adapter.ChatAdapter;
import call.ai.com.callsecretary.bean.Chat;
import call.ai.com.callsecretary.chat.ChatActivity;
import call.ai.com.callsecretary.chat.ChatService;
import call.ai.com.callsecretary.floating.FloatingWindowsService;
import call.ai.com.callsecretary.lex.InteractiveVoiceUtils;
import call.ai.com.callsecretary.socketcall.SocketClient;
import call.ai.com.callsecretary.socketcall.SocketService;
import call.ai.com.callsecretary.utils.CommonSharedPref;
import call.ai.com.callsecretary.widget.AlertDialog;
import lex.SerializablePostContentResult;

public class MainActivity extends BaseActivity implements ChatAdapter.OnItemClickListener,
        InteractiveVoiceView.InteractiveVoiceListener {
    String mCurrentIp;
    Handler mMainHandler;
    InteractiveVoiceUtils mVoiceUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAppBar();
        initSocketClient();
        initTestButton();
        initChatListView();
    }

    private void initAppBar() {
        setBarTitle(R.string.app_name);
        enableBackButton(false);
        View setting = View.inflate(this, R.layout.layout_setting, null);
        addMenuLayout(setting);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSetting();
            }
        });
    }

    private void initSocketClient() {
        final EditText editText = (EditText) findViewById(R.id.service_ip);
        editText.setText("10.60.196.42");
        mMainHandler = new Handler();
        Button btn = (Button) findViewById(R.id.set_ip_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = editText.getText().toString();
                if (TextUtils.equals(ip, mCurrentIp)) {
                    Toast.makeText(MainActivity.this, R.string.toast_connect_self, Toast.LENGTH_SHORT).show();
                    return;
                }
                CommonSharedPref.getInstance(MainActivity.this).setServiceIp(editText.getText().toString());
                mVoiceUtils =  InteractiveVoiceUtils.getInstance();
                SocketClient.getInstance().init(MainActivity.this.getApplicationContext(), mMainHandler, mVoiceUtils);
                mVoiceUtils.start(MainActivity.this, null, null);
            }
        });
    }

    private void initTestButton() {
        findViewById(R.id.floatwindow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingWindowsService floatingWindowsService = FloatingWindowsService.getServiceInstance();
                floatingWindowsService.showFloatingWindows("hhhh");
//                floatingWindowsService.startBot();
//                floatingWindowsService.startNativeBot();
            }
        });

        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        mCurrentIp = intToIp(ipAddress);
        Toast.makeText(this, "ip : " + mCurrentIp, Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, SocketService.class);
        startService(i);
    }

    private void initChatListView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ChatAdapter adapter = new ChatAdapter();
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        ChatService.getInstance().addListener(adapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void goToSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    @Override
    public void onItemClick(Chat chat, int resId) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_CHAT, chat);
        intent.putExtra(ChatActivity.EXTRA_RES_ID, resId);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent i = new Intent(this, SocketService.class);
        stopService(i);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            checkLeave();
        } else {
            super.onKeyDown(keyCode, event);
        }
        return false;
    }

    private void checkLeave() {
        int msgResId = R.string.warning_quit;
        showCommonAlert(0, msgResId, R.string.leave, R.string.stay,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideCommonAlert();
                        if (v.getId() == AlertDialog.BUTTON_POSITIVE) {
                            finish();
                        }
                    }
                });
    }

    @Override
    public void dialogReadyForFulfillment(Map<String, String> slots, String intent) {

    }

    @Override
    public void onResponse(Response response) {
        Log.d("liufan", "onResponse");
        mVoiceUtils.getClient().setAudioPlaybackState(InteractionClient.BUSY);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mVoiceUtils.onAudioPlaybackStarted();
            }
        });
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
        SocketClient.getInstance().sendMsgToSocket(result);
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mVoiceUtils.onAudioPlayBackCompleted();
            }
        });
        mVoiceUtils.getClient().setAudioPlaybackState(InteractionClient.NOT_BUSY);
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
        e.printStackTrace();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mVoiceUtils != null) {
            mVoiceUtils.finish();
        }
    }
}
