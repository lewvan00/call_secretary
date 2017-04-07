package call.ai.com.callsecretary.bean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Administrator on 2017/4/7.
 */

public class DbHelper extends OrmLiteSqliteOpenHelper {
    private static final String DB_NAME = "db_call_secretary";

    private Dao<Chat, Integer> chatDao;
    private Dao<ChatMessage, Integer> chatMessageDao;

    private DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    private static DbHelper sInstance;

    public static DbHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DbHelper.class) {
                if (sInstance == null) sInstance = new DbHelper(context);
            }
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ChatMessage.class);
            TableUtils.createTable(connectionSource, Chat.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, ChatMessage.class, true);
            TableUtils.dropTable(connectionSource, Chat.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Dao<Chat, Integer> getChatDao() throws SQLException {
        if (chatDao == null) {
            chatDao = getDao(Chat.class);
        }
        return chatDao;
    }

    public synchronized Dao<ChatMessage, Integer> getChatMessageDao() throws SQLException {
        if (chatMessageDao == null) {
            chatMessageDao = getDao(ChatMessage.class);
        }
        return chatMessageDao;
    }

    @Override
    public void close() {
        super.close();
        chatDao = null;
        chatMessageDao = null;
    }
}
