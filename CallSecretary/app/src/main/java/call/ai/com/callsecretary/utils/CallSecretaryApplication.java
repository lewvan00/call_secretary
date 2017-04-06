package call.ai.com.callsecretary.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/4/6.
 */

public class CallSecretaryApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }

}
