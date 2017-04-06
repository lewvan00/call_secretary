package call.ai.com.callsecretary.floating;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.regions.Regions;

import amazonaws.mobileconnectors.lex.interactionkit.InteractionClient;
import amazonaws.mobileconnectors.lex.interactionkit.Response;
import amazonaws.mobileconnectors.lex.interactionkit.continuations.LexServiceContinuation;
import amazonaws.mobileconnectors.lex.interactionkit.listeners.AudioPlaybackListener;
import amazonaws.mobileconnectors.lex.interactionkit.listeners.InteractionListener;
import call.ai.com.callsecretary.utils.CommonSharedPref;
import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.utils.PhoneUtils;

/**
 * Created by Administrator on 2017/3/28.
 */

public class FloatingWindowsService extends Service implements AudioPlaybackListener, InteractionListener {
    WindowManager mWindowManager;
    WindowManager.LayoutParams mLayoutParams;
    LinearLayout mFloatingView;
    TextView mTitleTv;
    ListView mDetailListView;
    TextView mCloseBtn;
    private ChatHistoryAdapter mAdapter;
    boolean hasFloatingShowing = false;
    float mOffsetX;
    float mOffsetY;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new FloatingWindowsBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        initLayoutParams();
        CognitoCredentialsProvider credentialsProvider = new CognitoCredentialsProvider(
                getResources().getString(R.string.identity_id_test),
                Regions.fromName(getResources().getString(R.string.aws_region)));
        InteractionClient lexInteractionClient = new InteractionClient(getApplicationContext(),
                credentialsProvider,
                Regions.US_EAST_1,
                getResources().getString(R.string.bot_name),
                getResources().getString(R.string.bot_alias));
        lexInteractionClient.setAudioPlaybackListener(this);
        lexInteractionClient.setInteractionListener(this);
    }

    public void showFloatingWindows(String chatName) {
        initFloatingView();
        if (!hasFloatingShowing) {
            hasFloatingShowing = true;
            mTitleTv.setText(chatName);
            mWindowManager.addView(mFloatingView, mLayoutParams);
        }
    }

    public void hideFloatingWindows() {
        if (mFloatingView != null && mWindowManager != null && hasFloatingShowing) {
            mWindowManager.removeView(mFloatingView);
            if (mAdapter != null) {
                mAdapter.clearMessages();
            }
            hasFloatingShowing = false;
            CommonSharedPref.getInstance(this).setFloatingWindowsLocationX(mLayoutParams.x);
            CommonSharedPref.getInstance(this).setFloatingWindowsLocationY(mLayoutParams.y);
        }
    }

    private void initLayoutParams() {
        mLayoutParams = new WindowManager.LayoutParams();

        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

        if (CommonSharedPref.getInstance(this).getFloatingWindowsLocationX() == -1) {
            mLayoutParams.x = mWindowManager.getDefaultDisplay().getWidth() / 6;
            mLayoutParams.y = mWindowManager.getDefaultDisplay().getHeight() / 3;
        } else {
            mLayoutParams.x = CommonSharedPref.getInstance(this).getFloatingWindowsLocationX();
            mLayoutParams.y = CommonSharedPref.getInstance(this).getFloatingWindowsLocationY();
        }

        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = mWindowManager.getDefaultDisplay().getWidth() * 2 / 3;
        mLayoutParams.height = mWindowManager.getDefaultDisplay().getHeight() / 3;
    }

    private void initFloatingView() {
        if (mFloatingView == null) {
            mFloatingView = (LinearLayout) LayoutInflater.from(getApplication()).inflate(R.layout.layout_floating_view, null);
            mTitleTv = (TextView) mFloatingView.findViewById(R.id.title);
            mCloseBtn = (TextView) mFloatingView.findViewById(R.id.close_btn);

            TextView emptyTv = (TextView) mFloatingView.findViewById(R.id.empty_tv);
            mDetailListView = (ListView) mFloatingView.findViewById(R.id.chat_list);
            mDetailListView.setEmptyView(emptyTv);

            mAdapter = new ChatHistoryAdapter(getBaseContext());
            mDetailListView.setAdapter(mAdapter);


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
    }

    public void addChatMessage(ChatMessage message) {
        if (mAdapter == null || message == null) return;
        mAdapter.addChatMessage(message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideFloatingWindows();
    }

    @Override
    public void onAudioPlaybackStarted() {

    }

    @Override
    public void onAudioPlayBackCompleted() {

    }

    @Override
    public void onAudioPlaybackError(Exception e) {

    }

    @Override
    public void onReadyForFulfillment(Response response) {
        PhoneUtils.endCall();
    }

    @Override
    public void promptUserToRespond(Response response, LexServiceContinuation continuation) {

    }

    @Override
    public void onInteractionError(Response response, Exception e) {

    }

    public class FloatingWindowsBinder extends Binder {
        public FloatingWindowsService getFloatingService() {
            return FloatingWindowsService.this;
        }
    }

}
