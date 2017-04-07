package call.ai.com.callsecretary.chat;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import call.ai.com.callsecretary.BaseActivity;
import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.adapter.MessageAdapter;
import call.ai.com.callsecretary.bean.Chat;
import call.ai.com.callsecretary.utils.ChatUtils;

public class ChatActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initData();
        initViews();
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        adapter = new MessageAdapter(chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        chat = ChatUtils.createTestChat();
    }
}
