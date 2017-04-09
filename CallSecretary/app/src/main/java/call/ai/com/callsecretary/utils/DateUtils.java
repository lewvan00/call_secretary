package call.ai.com.callsecretary.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/9.
 */

public class DateUtils {

    public static String covertTimeToText(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
        return formatter.format(new Date(time));
    }
}
