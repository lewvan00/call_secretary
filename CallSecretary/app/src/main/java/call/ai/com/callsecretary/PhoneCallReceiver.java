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

//    private CallRecorder mCallRecorder;
    private FloatingWindowsService mFloatingService;

    public PhoneCallReceiver() {
        TelephonyManager telManager = (TelephonyManager) CallSecretaryApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        TelListner listener = new TelListner();
        telManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
//        mCallRecorder = new CallRecorder();
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
//                PhoneUtils.setSpeekModle(true);   //开启外放
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


    private class TelListner extends PhoneStateListener {
        boolean comingPhone=false;//标识
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:/* 无任何状态 */
                    if(this.comingPhone){
                        this.comingPhone=false;
                        PhoneUtils.setSpeekModle(false);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:/* 接起电话 */
                    this.comingPhone=true;
                    PhoneUtils.setSpeekModle(true);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:/* 电话进来 */
                    this.comingPhone=true;
                    PhoneUtils.setSpeekModle(true);
                    break;
            }
        }
    }
}
