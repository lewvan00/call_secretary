package call.ai.com.callsecretary;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/3/28.
 */

public class FloatingWindowsService extends Service {
    WindowManager mWindowManager;
    WindowManager.LayoutParams mLayoutParams;
    LayoutInflater mInfalte;
    LinearLayout mFloatingView;
    TextView mTitleTv;
    TextView mDetailTv;
    Button mCloseBtn;
    boolean hasFloatingShowing = false;
    float mOffsetX;
    float mOffsetY;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        mInfalte = LayoutInflater.from(getApplication());
        initLayoutParams();

        showFloatingWindows("纯粹测试");
    }

    private void initLayoutParams() {
        mLayoutParams = new WindowManager.LayoutParams();

        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

        mLayoutParams.x=mWindowManager.getDefaultDisplay().getWidth()/6;
        mLayoutParams.y=mWindowManager.getDefaultDisplay().getHeight()/3;

        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = mWindowManager.getDefaultDisplay().getWidth() * 2 / 3;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
    }

    public void showFloatingWindows(@NonNull String detail) {
        if (mFloatingView == null) {
            mFloatingView = (LinearLayout) mInfalte.inflate(R.layout.layout_floating_view, null);
            mTitleTv = (TextView) mFloatingView.findViewById(R.id.title);
            mCloseBtn = (Button) mFloatingView.findViewById(R.id.close_btn);
            mDetailTv = (TextView) mFloatingView.findViewById(R.id.detail);
            mCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideFloatingWindows();
                }
            });

            mTitleTv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            mOffsetX = motionEvent.getRawX() - mLayoutParams.x;
                            mOffsetY = motionEvent.getRawY() - mLayoutParams.y;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            mLayoutParams.x = (int) (motionEvent.getRawX() - mOffsetX);
                            mLayoutParams.y = (int) (motionEvent.getRawY() - mOffsetY);
                            mWindowManager.updateViewLayout(mFloatingView, mLayoutParams);
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                    }

                    return true;
                }
            });
        }
        if (!hasFloatingShowing) {
            hasFloatingShowing = true;
            mDetailTv.setText(detail);
            mWindowManager.addView(mFloatingView, mLayoutParams);
        }
    }

    public void hideFloatingWindows() {
        if (mFloatingView != null && mWindowManager != null && hasFloatingShowing) {
            mWindowManager.removeView(mFloatingView);
            hasFloatingShowing = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideFloatingWindows();
    }
}
