package call.ai.com.callsecretary.utils;

import java.util.ArrayList;
import java.util.List;

import call.ai.com.callsecretary.bean.Chat;

/**
 * Created by Administrator on 2017/4/7.
 */

public class ChatUtils {
    static String[] list = new String[]{
            "13178864112",
            "15862559871",
            "13498597445",
            "13258965856",
            "15685552582",
            "25638254",
            "13825588585",
            "56845825"
    };
    static long seed = 0;

    static String randPhone() {
        long pos = seed % list.length;
        seed = seed+1;
        return list[(int)pos];
    }

    public static Chat createTestChat() {
        Chat chat = new Chat();
        chat.setPhone(randPhone());
        chat.setTime(System.currentTimeMillis());
        chat.addCallerMessage("hey brother.");
        chat.addSecretaryMessage("Oh, What's up man!");
        chat.addCallerMessage("let't go to the hospital");
        chat.addSecretaryMessage("Okay. Wait a minute. Let me call my master");
        return chat;
    }

    public static Chat createTest2Chat() {
        Chat chat = new Chat();
        chat.setPhone(randPhone());
        chat.setTime(System.currentTimeMillis());
        chat.addCallerMessage("hey brother.");
        chat.addSecretaryMessage("Oh, What's up man!");
        chat.addCallerMessage("let't go to play basketball");
        chat.addSecretaryMessage("Okay. Wait a minute. Let me call my master");
        return chat;
    }

    public static List<Chat> createTestChatList() {
        List<Chat> chats = new ArrayList<>();
        for(int i = 0 ; i < 5; i++) {
            chats.add(createTestChat());
            chats.add(createTest2Chat());
        }
        return chats;
    }
}
