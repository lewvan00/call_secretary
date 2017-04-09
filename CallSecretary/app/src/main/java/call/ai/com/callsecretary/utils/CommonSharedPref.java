package call.ai.com.callsecretary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArraySet;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;

/**
 * Created by lewvan on 2017/3/27.
 */

public class CommonSharedPref {
    private static CommonSharedPref sInstance;
    private SharedPreferences mSharedPreferences;

    private CommonSharedPref(Context context) {
        mSharedPreferences = context.getSharedPreferences("ai_call", Context.MODE_PRIVATE);
    }

    public static CommonSharedPref getInstance() {
        return getInstance(CallSecretaryApplication.getContext());
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
        return mSharedPreferences.getBoolean("auto_pickup_phone", true);
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

    public void setServiceIp(String ip) {
        mSharedPreferences.edit().putString("service_ip", ip).apply();
    }

    public String getServiceIp() {
        return mSharedPreferences.getString("service_ip", "10.60.205.64");
    }

    public int getWorkMode() {
        return mSharedPreferences.getInt("work_mode", 0);
    }

    public void setWorkMode(int i) {
        mSharedPreferences.edit().putInt("work_mode", i).apply();
    }

    public Set<String> getWhiteList() {
        return mSharedPreferences.getStringSet("white_list", new HashSet<String>());
    }

    public void setWhiteList(HashSet<String> strings) {
        mSharedPreferences.edit().putStringSet("white_list", strings).apply();
    }
}
