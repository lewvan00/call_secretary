package call.ai.com.callsecretary.utils;

import java.util.Random;

import call.ai.com.callsecretary.R;

/**
 * Created by Administrator on 2017/4/8.
 */

public class AvatarUtils {
    public final static int[] RES_ID_ARRAY = {
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4,
            R.drawable.avatar5,
            R.drawable.avatar6,
            R.drawable.avatar7,
            R.drawable.avatar8,
            R.drawable.avatar9,
            R.drawable.avatar10,
            R.drawable.avatar11,
            R.drawable.avatar12,
            R.drawable.avatar13,
            R.drawable.avatar14,
            R.drawable.avatar15,
            R.drawable.avatar16,
            R.drawable.avatar17,
            R.drawable.avatar18,
            R.drawable.avatar19,
            R.drawable.avatar20,
            R.drawable.avatar21
    };

    public static int getAvatarResId(long position) {
        position = position % 21;
        return RES_ID_ARRAY[(int)position];
    }

    public static int getRandomAvatarResId() {
        return getAvatarResId(System.currentTimeMillis());
    }
}
