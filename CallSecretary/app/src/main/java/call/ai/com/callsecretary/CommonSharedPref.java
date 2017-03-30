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

    public void setAutoPickupPhone(boolean autoPickupPhone) {
        mSharedPreferences.edit().putBoolean("auto_pickup_phone", autoPickupPhone).apply();
    }

    public boolean getAutoPickupPhone() {
        return mSharedPreferences.getBoolean("auto_pickup_phone", false);
    }

    public int getFloatingWindowsLocationX() {
        return mSharedPreferences.getInt("floating_windows_location_x", -1);
    }

    public int getFloatingWindowsLocationY() {
        return mSharedPreferences.getInt("floating_windows_location_y", -1);
    }

    public void setFloatingWindowsLocationX(int locationX) {
        mSharedPreferences.edit().putInt("floating_windows_location_x", locationX).apply();
    }

    public void setFloatingWindowsLocationY(int locationY) {
        mSharedPreferences.edit().putInt("floating_windows_location_y", locationY).apply();
    }
}
