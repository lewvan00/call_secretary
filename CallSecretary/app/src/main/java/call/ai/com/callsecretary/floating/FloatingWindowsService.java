package call.ai.com.callsecretary.floating;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.mobileconnectors.lex.interactionkit.InteractionClient;
import com.amazonaws.mobileconnectors.lex.interactionkit.Response;
import com.amazonaws.mobileconnectors.lex.interactionkit.config.InteractionConfig;
import com.amazonaws.mobileconnectors.lex.interactionkit.continuations.LexServiceContinuation;
import com.amazonaws.mobileconnectors.lex.interactionkit.listeners.AudioPlaybackListener;
import com.amazonaws.mobileconnectors.lex.interactionkit.listeners.InteractionListener;
import com.amazonaws.mobileconnectors.lex.interactionkit.ui.InteractiveVoiceView;
import com.amazonaws.mobileconnectors.lex.interactionkit.ui.InteractiveVoiceViewAdapter;
import com.amazonaws.regions.Regions;

import java.util.Map;

import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.chat.ChatActivity;
import call.ai.com.callsecretary.utils.CallSecretaryApplication;
import call.ai.com.callsecretary.utils.PhoneUtils;

/**
 * Created by Administrator on 2017/3/28.
 */

public class FloatingWindowsService implements AudioPlaybackListener, InteractionListener, InteractiveVoiceView.InteractiveVoiceListener {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private FloatingWindow mFloatingView;

    private boolean hasFloatingShowing = false;

    CognitoCredentialsProvider credentialsProvider;

    private static FloatingWindowsService sServiceInstance;

    public static synchronized FloatingWindowsService getServiceInstance() {
        if (sServiceInstance == null) {
            if (sServiceInstance == null) {
                sServiceInstance = new FloatingWindowsService();
            }
        }
        return sServiceInstance;
    }

    public synchronized void release() {
        sServiceInstance = null;
    }

    private FloatingWindowsService() {
        Context context = CallSecretaryApplication.getContext();
        mWindowManager = (WindowManager) CallSecretaryApplication.getContext().getSystemService(CallSecretaryApplication.getContext().WINDOW_SERVICE);
        initLayoutParams();
        credentialsProvider = new CognitoCredentialsProvider(
                context.getResources().getString(R.string.identity_id_test),
                Regions.fromName(context.getResources().getString(R.string.aws_region)));
        InteractionClient lexInteractionClient = new InteractionClient(context,
                credentialsProvider,
                Regions.US_EAST_1,
                context.getResources().getString(R.string.bot_name),
                context.getResources().getString(R.string.bot_alias));
        lexInteractionClient.setAudioPlaybackListener(this);
        lexInteractionClient.setInteractionListener(this);

        initFloatingView();
    }

    public void startBot() {
        Context context = CallSecretaryApplication.getContext();
        InteractiveVoiceView interactiveVoiceView = mFloatingView.getInteractiveVoiceView();
        interactiveVoiceView.setInteractiveVoiceListener(this);
        interactiveVoiceView.getViewAdapter().setCredentialProvider(credentialsProvider);
        interactiveVoiceView.getViewAdapter().setInteractionConfig(
                new InteractionConfig(context.getResources().getString(R.string.bot_name),
                context.getResources().getString(R.string.bot_alias)));
        interactiveVoiceView.getViewAdapter().setAwsRegion(context.getResources().getString(R.string.aws_region));
        interactiveVoiceView.performClick();
    }

    public void stopBot() {
        Context context = CallSecretaryApplication.getContext();
        InteractiveVoiceView interactiveVoiceView = mFloatingView.getInteractiveVoiceView();
        InteractiveVoiceViewAdapter interactiveVoiceViewAdapter =
                InteractiveVoiceViewAdapter.getInstance(context, interactiveVoiceView);
        interactiveVoiceViewAdapter.cancel();
    }

    public void showFloatingWindows(String chatName) {
        initFloatingView();
        if (!hasFloatingShowing) {
            hasFloatingShowing = true;
            mFloatingView.setTitle(chatName);
            mWindowManager.addView(mFloatingView, mLayoutParams);
        }
    }

    private void initLayoutParams() {
        mLayoutParams = new WindowManager.LayoutParams();

        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

        mLayoutParams.x = 0;
        mLayoutParams.y = 0;

        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
        mLayoutParams.windowAnimations = android.R.style.Animation_Translucent;
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.height = mWindowManager.getDefaultDisplay().getHeight() ;
    }

    private void initFloatingView() {
        if (mFloatingView != null) return;

        mFloatingView = new FloatingWindow(CallSecretaryApplication.getContext());
        mFloatingView.setUiInterface(new FloatingWindow.UiInterface() {
            @Override
            public void onClose() {
                hideFloatingWindows();
            }
        });

        mFloatingView.setLongClickable(true);
        mFloatingView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(mFloatingView.getContext(), ChatActivity.class);
                mFloatingView.getContext().startActivity(intent);
                hideFloatingWindows();
                return true;
            }
        });

        mFloatingView.setOnArrowTouchListener(new View.OnTouchListener() {
            float mOffsetY;
            float mLastY = mFloatingView.getHeight();

            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mOffsetY = motionEvent.getRawY() - mLastY;
                        if (mOffsetY < 0) {
                            mLastY = motionEvent.getRawY();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mOffsetY < 0) {
                            mLayoutParams.height = (int)(mFloatingView.getHeight() + mLastY - motionEvent.getRawY());
                            mWindowManager.updateViewLayout(mFloatingView, mLayoutParams);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
    }

    public void hideFloatingWindows() {
        if (mFloatingView != null && mWindowManager != null && hasFloatingShowing) {
            mWindowManager.removeView(mFloatingView);
            hasFloatingShowing = false;
        }
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

    @Override
    public void dialogReadyForFulfillment(Map<String, String> slots, String intent) {

    }

    @Override
    public void onResponse(Response response) {
        mFloatingView.getMessageAdapter().addCallerMessage(response.getResult().getInputTranscript());
        mFloatingView.getMessageAdapter().addSecretaryMessage(response.getResult().getMessage());
    }

    @Override
    public void onHangUp(Response response) {

    }

    @Override
    public void onError(String responseText, Exception e) {

    }
}
