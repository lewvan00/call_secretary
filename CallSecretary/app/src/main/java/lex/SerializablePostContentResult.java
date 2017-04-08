package lex;

import com.amazonaws.services.lexrts.model.PostContentResult;

import java.io.Serializable;

/**
 * Created by lewvan on 2017/4/8.
 */

public class SerializablePostContentResult implements Serializable {
    public static final int STATE_RESPONSE = 1;

    private String contentType;

    private String intentName;

    private String message;

    private String inputTranscript;

    private int mState;

    public int getState() {
        return mState;
    }

    public void setState(int mState) {
        this.mState = mState;
    }

    public void setRealResult(PostContentResult contentResult) {
        if (contentResult != null) {
            setContentType(contentResult.getContentType());
            setIntentName(contentResult.getIntentName());
            setInputTranscript(contentResult.getInputTranscript());
            setMessage(contentResult.getMessage());
            setState(STATE_RESPONSE);
        }
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getIntentName() {
        return intentName;
    }

    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getInputTranscript() {
        return inputTranscript;
    }

    public void setInputTranscript(String inputTranscript) {
        this.inputTranscript = inputTranscript;
    }

    @Override
    public String toString() {
        return "message = " + message + ", input = " + inputTranscript;
    }
}
