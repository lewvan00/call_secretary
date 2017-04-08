package call.ai.com.callsecretary.utils;

import java.util.ArrayList;
import java.util.List;

import call.ai.com.callsecretary.bean.Chat;

/**
 * Created by Administrator on 2017/4/7.
 */

public class ChatUtils {

    public static Chat createTestChat() {
        Chat chat = new Chat();
        chat.setPhone("25636656");
        chat.addCallerMessage("ddewdd");
        chat.addCallerMessage("dddefed");
        chat.addSecretaryMessage("dddefd");
        chat.addCallerMessage("aaewsss");
        chat.addSecretaryMessage("ddsedd");
        chat.addCallerMessage("aasss");
        return chat;
    }

    public static List<Chat> createTestChatList() {
        List<Chat> chats = new ArrayList<>();
        for(int i = 0 ; i < 10; i++) {
            chats.add(createTestChat());
        }
        return chats;
    }
}
