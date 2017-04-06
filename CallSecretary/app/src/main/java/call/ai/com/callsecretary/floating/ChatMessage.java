package call.ai.com.callsecretary.floating;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/31.
 */

public class ChatMessage implements Serializable {
    public static final byte TYPE_CALLER = 0x01;
    public static final byte TYPE_SECRETARY = 0x02;
    private String message;
    private byte userType;

    private ChatMessage(byte userType, String message) {
        this.userType = userType;
        this.message = message;
    }

    public static ChatMessage createCallerMessage(String message) {
        return new ChatMessage(TYPE_CALLER, message);
    }

    public static ChatMessage createSecretaryMessage(String message) {
        return new ChatMessage(TYPE_SECRETARY, message);
    }

    public String getMessage() {
        return message;
    }

    public byte getUserType() {
        return userType;
    }
}
