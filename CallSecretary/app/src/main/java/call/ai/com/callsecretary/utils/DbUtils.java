package call.ai.com.callsecretary.utils;

import android.content.Context;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import call.ai.com.callsecretary.bean.Chat;
import call.ai.com.callsecretary.bean.DbHelper;

/**
 * Created by Administrator on 2017/4/7.
 */

public class DbUtils {

    public static List<Chat> getAllChat() {
        try {
            return DbHelper.getInstance(getContext()).getChatDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void saveChat(Chat chat) {
        try {
            DbHelper.getInstance(getContext()).getChatDao().create(chat);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Context getContext() {
        return CallSecretaryApplication.getContext();
    }

    public static void saveChats(List<Chat> chats) {
        try {
            DbHelper.getInstance(getContext()).getChatDao().create(chats);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
