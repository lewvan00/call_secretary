package call.ai.com.callsecretary.chat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.adapter.MessageAdapter;
import call.ai.com.callsecretary.bean.Chat;

public class ChatActivity extends Activity {

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
        chat = new Chat();
        chat.addCallerMessage("ddewdd");
        chat.addCallerMessage("dddefed");
        chat.addSecretaryMessage("dddefd");
        chat.addCallerMessage("aaewsss");
        chat.addSecretaryMessage("ddsedd");
        chat.addCallerMessage("aasss");
    }
}
