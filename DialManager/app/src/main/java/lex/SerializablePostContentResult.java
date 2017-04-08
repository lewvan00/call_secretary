package lex;

import com.amazonaws.services.lexrts.model.PostContentResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by lewvan on 2017/4/8.
 */

public class SerializablePostContentResult implements Serializable {
    public static final int STATE_RESPONSE = 1;
    public static final int STATE_CALL = 2;
    public static final int STATE_FINAL = 3;
    public static final int STATE_HANGUP = 4;

    private String contentType;

    private String intentName;

    private String message;

    private String inputTranscript;

    private int mState;

    private byte[] audioBytes;

    public byte[] getAudioBytes() {
        return audioBytes;
    }

    public void setAudioBytes(byte[] audioBytes) {
        this.audioBytes = audioBytes;
    }

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
            InputStream audioStream = contentResult.getAudioStream();
            final byte buffer[] = new byte[16384];
            int length;
            int desPos = 0;
            try {
                audioBytes = new byte[audioStream.available()];
                while ((length = audioStream.read(buffer)) != -1) {
                    System.arraycopy(buffer, 0, audioBytes, desPos, length);
                    desPos = length;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    audioStream.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
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
