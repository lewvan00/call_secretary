package call.ai.com.callsecretary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;

import call.ai.com.callsecretary.adapter.ChatAdapter;
import call.ai.com.callsecretary.bean.Chat;
import call.ai.com.callsecretary.chat.ChatActivity;
import call.ai.com.callsecretary.chat.ChatService;
import call.ai.com.callsecretary.floating.FloatingWindowsService;
import call.ai.com.callsecretary.widget.AlertDialog;

public class MainActivity extends BaseActivity implements ChatAdapter.OnItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAppBar();
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

    private void initTestButton() {
        findViewById(R.id.floatwindow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FloatingWindowsService floatingWindowsService = new FloatingWindowsService();
                floatingWindowsService.showFloatingWindows("hhhh");
                floatingWindowsService.startBot();
//                floatingWindowsService.startNativeBot();
            }
        });
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

    @Override
    public void onItemClick(Chat chat) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intent);
    }

    @Override
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
