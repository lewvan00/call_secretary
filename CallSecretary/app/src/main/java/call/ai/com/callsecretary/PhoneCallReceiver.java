package call.ai.com.callsecretary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import call.ai.com.callsecretary.floating.FloatingWindowsService;
import call.ai.com.callsecretary.recorder.CallRecorder;
import call.ai.com.callsecretary.utils.CallSecretaryApplication;
import call.ai.com.callsecretary.utils.PhoneUtils;

/**
 * Created by lewvan on 2017/3/27.
 */

public class PhoneCallReceiver extends BroadcastReceiver {

    private FloatingWindowsService mFloatingService;

    public PhoneCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
                PhoneUtils.autoAnswer();
                mFloatingService = new FloatingWindowsService();
                mFloatingService.showFloatingWindows("liufan");
            } else if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
                PhoneUtils.setSpeekModle(false);   //关闭外放
            } else if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK) {
                PhoneUtils.setSpeekModle(true);
                mFloatingService.startBot();
            }
        }
    }

}
