package call.ai.com.callsecretary.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/31.
 */
@DatabaseTable(tableName = "tb_message")
public class ChatMessage implements Parcelable {
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

    public ChatMessage() {
    }

    public ChatMessage(byte userType, String message, Chat chat) {
        this.userType = userType;
        this.message = message;
        this.chat = chat;
    }

    protected ChatMessage(Parcel in) {
        id = in.readInt();
        message = in.readString();
        userType = in.readByte();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(message);
        dest.writeByte(userType);
    }
}
