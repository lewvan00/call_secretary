package call.ai.com.callsecretary.floating;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.amazonaws.regions.Regions;

import java.util.Map;


import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.lex.InteractiveVoiceUtils;
import call.ai.com.callsecretary.utils.CallSecretaryApplication;
import call.ai.com.callsecretary.utils.CommonSharedPref;
import call.ai.com.callsecretary.utils.PhoneUtils;

/**
 * Created by Administrator on 2017/3/28.
 */

public class FloatingWindowsService implements AudioPlaybackListener, InteractionListener, InteractiveVoiceView.InteractiveVoiceListener {
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private FloatingWindow mFloatingView;

    private boolean hasFloatingShowing = false;
    private float mOffsetX;
    private float mOffsetY;

    CognitoCredentialsProvider credentialsProvider;

    public FloatingWindowsService() {
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

    public void startNativeBot(){
        InteractiveVoiceUtils interactiveVoiceUtils=InteractiveVoiceUtils.getInstance();
//        interactiveVoiceUtils.setVoiceListener(this);
//        interactiveVoiceUtils.setCredentialProvider(credentialsProvider);
//        interactiveVoiceUtils.setInteractionConfig(
//                new InteractionConfig(context.getResources().getString(R.string.bot_name),
//                        context.getResources().getString(R.string.bot_alias)));
//        interactiveVoiceUtils.setAwsRegion(context.getResources().getString(R.string.aws_region));
        interactiveVoiceUtils.start(this,this,this);
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

        mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;

        Context context = CallSecretaryApplication.getContext();
        if (CommonSharedPref.getInstance(CallSecretaryApplication.getContext()).getFloatingWindowsLocationX() == -1) {
            mLayoutParams.x = mWindowManager.getDefaultDisplay().getWidth() / 6;
            mLayoutParams.y = mWindowManager.getDefaultDisplay().getHeight() / 3;
        } else {
            mLayoutParams.x = CommonSharedPref.getInstance(context).getFloatingWindowsLocationX();
            mLayoutParams.y = CommonSharedPref.getInstance(context).getFloatingWindowsLocationY();
        }

        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.width = mWindowManager.getDefaultDisplay().getWidth() * 2 / 3;
        mLayoutParams.height = mWindowManager.getDefaultDisplay().getHeight() / 3;
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

        mFloatingView.setOnTitleTouchListener(new View.OnTouchListener() {
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

    public void hideFloatingWindows() {
        if (mFloatingView != null && mWindowManager != null && hasFloatingShowing) {
            mWindowManager.removeView(mFloatingView);
            hasFloatingShowing = false;
            CommonSharedPref.getInstance(CallSecretaryApplication.getContext()).setFloatingWindowsLocationX(mLayoutParams.x);
            CommonSharedPref.getInstance(CallSecretaryApplication.getContext()).setFloatingWindowsLocationY(mLayoutParams.y);
        }
    }

    public void onDestroy() {
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

    @Override
    public void dialogReadyForFulfillment(Map<String, String> slots, String intent) {

    }

    @Override
    public void onResponse(Response response) {
        mFloatingView.getMessageAdapter().addCallerMessage(response.getResult().getInputTranscript());
        mFloatingView.getMessageAdapter().addSecretaryMessage(response.getResult().getMessage());
    }

    @Override
    public void onError(String responseText, Exception e) {

    }
}
