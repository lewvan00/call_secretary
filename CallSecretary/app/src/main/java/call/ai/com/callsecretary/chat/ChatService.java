package call.ai.com.callsecretary.chat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import call.ai.com.callsecretary.bean.Chat;
import call.ai.com.callsecretary.utils.BackgroundThread;
import call.ai.com.callsecretary.utils.ChatUtils;
import call.ai.com.callsecretary.utils.DbUtils;

/**
 * Created by Administrator on 2017/4/8.
 */
public class ChatService {
    public interface LoaderListener {
        void onListChange(List<Chat> list);
    }

    private List<LoaderListener> listeners = new ArrayList<>();
    private List<Chat> mList = new ArrayList<>();

    public List<Chat> getChatList() {
        return mList;
    }

    public void addListener(LoaderListener loaderListener) {
        if (!listeners.contains(loaderListener)) {
            listeners.add(loaderListener);
        }
    }

    public void addChatHeader(Chat chat) {
        mList.add(0, chat);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (LoaderListener loaderListener: listeners) {
                    loaderListener.onListChange(mList);
                }
            }
        });
    }

    public void loadChats() {
        BackgroundThread.post(new Runnable() {
            @Override
            public void run() {
                List<Chat> chats = DbUtils.getAllChat();
                if (chats.isEmpty()) {
                    Log.e("hhhh", "empty!!!!!!!");
                    chats = ChatUtils.createTestChatList();
                    DbUtils.saveChats(chats);
                }
                mList.clear();
                mList.addAll(chats);
                final List<Chat> results = chats;

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        for (LoaderListener loaderListener: listeners) {
                            loaderListener.onListChange(results);
                        }
                    }
                });
            }
        });
    }

    private static ChatService ourInstance = new ChatService();

    public static ChatService getInstance() {
        return ourInstance;
    }

    private ChatService() {
    }
}
