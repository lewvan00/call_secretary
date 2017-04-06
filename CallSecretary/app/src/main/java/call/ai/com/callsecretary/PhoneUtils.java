package call.ai.com.callsecretary;

import android.content.Context;
import android.os.IBinder;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/4/6.
 */

public class PhoneUtils {

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
}
