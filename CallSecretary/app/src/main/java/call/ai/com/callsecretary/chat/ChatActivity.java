package call.ai.com.callsecretary.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import call.ai.com.callsecretary.BaseActivity;
import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.adapter.MessageAdapter;
import call.ai.com.callsecretary.bean.Chat;
import call.ai.com.callsecretary.utils.ChatUtils;

public class ChatActivity extends BaseActivity {

    public static final String EXTRA_CHAT = "extra_chat";

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackButton(true);
        setBackClickFinish();

        initData();
        initViews();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        adapter = new MessageAdapter(chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            chat = intent.getParcelableExtra(EXTRA_CHAT);
        }
        if (chat == null) {
            chat = ChatUtils.createTestChat();
        }
        setBarTitle(chat.getPhone());
    }
}
