package call.ai.com.callsecretary;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import call.ai.com.callsecretary.adapter.ChatAdapter;
import call.ai.com.callsecretary.bean.Chat;
import call.ai.com.callsecretary.chat.ChatActivity;
import call.ai.com.callsecretary.chat.ChatService;
import call.ai.com.callsecretary.floating.FloatingWindowsService;
import call.ai.com.callsecretary.widget.AlertDialog;

public class MainActivity extends BaseActivity implements ChatAdapter.OnItemClickListener{

    RecyclerView recyclerView;
    View dialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAppBar();
        initTestButton();

        initDialView();
        initChatListView();
        initViewPager();
    }

    private void initDialView() {
        dialView = View.inflate(this, R.layout.layout_dial, null);
    }

    private void initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                if (position == 0) {
                    container.addView(dialView);
                    return dialView;
                }
                if (position == 1) {
                    container.addView(recyclerView);
                    return recyclerView;
                }
                return null;
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
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
        String ip = intToIp(ipAddress);
        Toast.makeText(this, "ip : " + ip, Toast.LENGTH_LONG).show();
        Intent i = new Intent(this, SocketService.class);
        startService(i);
    }

    private void initChatListView() {
        recyclerView = (RecyclerView) View.inflate(this, R.layout.layout_recyclerview, null);
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
}
