package call.ai.com.callsecretary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

    private CallRecorder mCallRecorder;
    private FloatingWindowsService mFloatingService;

    public PhoneCallReceiver() {
        mCallRecorder = new CallRecorder();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Log.d("liufan", "action = " + intent.getAction() + ", state = " + telephonyManager.getCallState());
            Toast.makeText(context, "action = " + intent.getAction() + ", state = " + telephonyManager.getCallState(), Toast.LENGTH_LONG).show();
            if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
                //来电
                PhoneUtils.autoAnswer();
                PhoneUtils.setSpeekModle(true);   //开启外放
                mFloatingService = new FloatingWindowsService();
                mFloatingService.showFloatingWindows("liufan");
                Toast.makeText(CallSecretaryApplication.getContext(), "starting bot ", Toast.LENGTH_LONG).show();
                mFloatingService.startBot();
//                mCallRecorder.startRecording();
            } else if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
                //挂断
//                PhoneUtils.setSpeekModle(false);   //关闭外放
//                mCallRecorder.stopRecoding();
            }
        }
    }

}
