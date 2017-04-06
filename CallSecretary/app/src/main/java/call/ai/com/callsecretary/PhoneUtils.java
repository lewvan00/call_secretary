package call.ai.com.callsecretary;

import android.content.Context;
import android.media.AudioManager;
import android.os.IBinder;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/4/6.
 */

public class PhoneUtils {

    public static int currVolume;

    //挂断电话
    public static void endCall(){
        try {
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            Method method = clazz.getMethod("getService", String.class);
            IBinder ibinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(ibinder);
            iTelephony.endCall();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void setSpeekModle(boolean open) {
        AudioManager audioManager = (AudioManager) CallSecretaryApplication.getContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.ROUTE_SPEAKER);
        currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        audioManager.setMode(AudioManager.MODE_IN_CALL);

        if (!audioManager.isSpeakerphoneOn() && open) {
            audioManager.setSpeakerphoneOn(true);//开启免提
            Toast.makeText(CallSecretaryApplication.getContext(), "免提模式", Toast.LENGTH_LONG).show();
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.STREAM_VOICE_CALL);
        } else if (audioManager.isSpeakerphoneOn() && !open) {
            audioManager.setSpeakerphoneOn(false);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                    AudioManager.STREAM_VOICE_CALL);
        }
    }
}
