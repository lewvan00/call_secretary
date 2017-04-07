package call.ai.com.callsecretary.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/31.
 */
@DatabaseTable(tableName = "tb_message")
public class ChatMessage implements Serializable {
    public static final byte TYPE_CALLER = 0x01;
    public static final byte TYPE_SECRETARY = 0x02;

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String message;

    @DatabaseField
    private byte userType;

    @DatabaseField(foreign=true, foreignAutoRefresh=true)
    private Chat chat;

    private ChatMessage(byte userType, String message, Chat chat) {
        this.userType = userType;
        this.message = message;
        this.chat = chat;
    }

    public static ChatMessage createCallerMessage(String message, Chat chat) {
        return new ChatMessage(TYPE_CALLER, message, chat);
    }

    public static ChatMessage createSecretaryMessage(String message, Chat chat) {
        return new ChatMessage(TYPE_SECRETARY, message, chat);
    }

    public String getMessage() {
        return message;
    }

    public byte getUserType() {
        return userType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
