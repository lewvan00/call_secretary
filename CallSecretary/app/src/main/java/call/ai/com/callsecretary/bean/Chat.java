package call.ai.com.callsecretary.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;
import java.util.Date;

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
    private Date date;

    @ForeignCollectionField
    private Collection<ChatMessage> messages;

    public Chat() {
    }

    public Chat(String phone, Date date, Collection<ChatMessage> messages) {
        this.phone = phone;
        this.date = date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Collection<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(Collection<ChatMessage> messages) {
        this.messages = messages;
    }
}
