package call.ai.com.callsecretary;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lewvan on 2017/3/27.
 */

public class CommonSharedPref {
    private static CommonSharedPref sInstance;
    private SharedPreferences mSharedPreferences;

    private CommonSharedPref(Context context) {
        mSharedPreferences = context.getSharedPreferences("ai_call", Context.MODE_PRIVATE);
    }

    public static CommonSharedPref getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CommonSharedPref.class) {
                if (sInstance == null) {
                    sInstance = new CommonSharedPref(context);
                }
            }
        }
        return sInstance;
    }

    public void setServiceIp(String ip) {
        mSharedPreferences.edit().putString("service_ip", ip).apply();
    }

    public String getServiceIp() {
        return mSharedPreferences.getString("service_ip", null);
    }
}
