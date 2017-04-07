package call.ai.com.callsecretary.utils;

import call.ai.com.callsecretary.bean.Chat;

/**
 * Created by Administrator on 2017/4/7.
 */

public class ChatUtils {

    public static Chat createTestChat() {
        Chat chat = new Chat();
        chat.addCallerMessage("ddewdd");
        chat.addCallerMessage("dddefed");
        chat.addSecretaryMessage("dddefd");
        chat.addCallerMessage("aaewsss");
        chat.addSecretaryMessage("ddsedd");
        chat.addCallerMessage("aasss");
        return chat;
    }
}
