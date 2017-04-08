package call.ai.com.callsecretary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import call.ai.com.callsecretary.floating.FloatingWindowsService;
import call.ai.com.callsecretary.utils.PhoneUtils;

/**
 * Created by lewvan on 2017/3/27.
 */

public class PhoneCallReceiver extends BroadcastReceiver {
    public PhoneCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            FloatingWindowsService floatingService = FloatingWindowsService.getServiceInstance();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
                floatingService.showFloatingWindows("liufan");
                PhoneUtils.autoAnswer();
            } else if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
                PhoneUtils.setSpeekModle(false);   //关闭外放
                floatingService.stopBot();
                floatingService.hideFloatingWindows();
            } else if (telephonyManager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK) {
                PhoneUtils.setSpeekModle(true);
                floatingService.startBot();
            }
        }
    }

}
