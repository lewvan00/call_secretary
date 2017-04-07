package call.ai.com.callsecretary.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Administrator on 2017/4/7.
 */

@DatabaseTable(tableName = "tb_chat")
public class Chat {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String phone;

    @DatabaseField
    private long time;

    @ForeignCollectionField
    private Collection<ChatMessage> messages = new ArrayList<>();;

    private List<ChatMessage> messageList = new ArrayList<>();

    public Chat() {
        time = System.currentTimeMillis();
        phone = "";
    }

    public Chat(String phone, long time, Collection<ChatMessage> messages) {
        this.phone = phone;
        this.time = time;
        this.messages = messages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<ChatMessage> getMessages() {
        if (!messages.isEmpty() && messageList.isEmpty()) {
            messageList = new ArrayList<>(messages);
        }
        return messageList;
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        messageList.add(message);
    }

    public void addCallerMessage(String text) {
        addMessage(ChatMessage.createCallerMessage(text, this));
    }

    public void addSecretaryMessage(String text) {
        addMessage(ChatMessage.createSecretaryMessage(text, this));
    }
}
