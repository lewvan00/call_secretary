package call.ai.com.callsecretary.floating;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import call.ai.com.callsecretary.bean.Chat;
import call.ai.com.callsecretary.bean.ChatMessage;
import call.ai.com.callsecretary.utils.CallSecretaryApplication;
import call.ai.com.callsecretary.utils.CommonSharedPref;
import call.ai.com.callsecretary.utils.PhoneUtils;

/**
 * Created by Administrator on 2017/3/28.
 */

public class FloatingWindowsService implements AudioPlaybackListener, InteractionListener, InteractiveVoiceView.InteractiveVoiceListener {
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
    InteractiveVoiceView mInteractiveVoiceView;
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
    }

    public void startBot() {
        Context context = CallSecretaryApplication.getContext();
        mInteractiveVoiceView.setInteractiveVoiceListener(this);
        mInteractiveVoiceView.getViewAdapter().setCredentialProvider(credentialsProvider);
        mInteractiveVoiceView.getViewAdapter().setInteractionConfig(
                new InteractionConfig(context.getResources().getString(R.string.bot_name),
                context.getResources().getString(R.string.bot_alias)));
        mInteractiveVoiceView.getViewAdapter().setAwsRegion(context.getResources().getString(R.string.aws_region));
        mInteractiveVoiceView.performClick();
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
            CommonSharedPref.getInstance(CallSecretaryApplication.getContext()).setFloatingWindowsLocationX(mLayoutParams.x);
            CommonSharedPref.getInstance(CallSecretaryApplication.getContext()).setFloatingWindowsLocationY(mLayoutParams.y);
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
        if (mFloatingView == null) {
            mFloatingView = (LinearLayout) LayoutInflater.from(CallSecretaryApplication.getContext()).inflate(R.layout.layout_floating_view, null);
            mTitleTv = (TextView) mFloatingView.findViewById(R.id.title);
            mCloseBtn = (TextView) mFloatingView.findViewById(R.id.close_btn);

            TextView emptyTv = (TextView) mFloatingView.findViewById(R.id.empty_tv);
            mDetailListView = (ListView) mFloatingView.findViewById(R.id.chat_list);
            mDetailListView.setEmptyView(emptyTv);

            mAdapter = new ChatHistoryAdapter(CallSecretaryApplication.getContext());
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
            mInteractiveVoiceView = (InteractiveVoiceView) mFloatingView.findViewById(R.id.interactive_voice_view);
        }
    }

    public void addChatMessage(ChatMessage message) {
        if (mAdapter == null || message == null) return;
        mAdapter.addChatMessage(message);
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
        addChatMessage(ChatMessage.createCallerMessage(response.getResult().getInputTranscript(), new Chat()));
        addChatMessage(ChatMessage.createSecretaryMessage(response.getResult().getMessage(), new Chat()));
    }

    @Override
    public void onInteractionError(Response response, Exception e) {

    }

    @Override
    public void dialogReadyForFulfillment(Map<String, String> slots, String intent) {

    }

    @Override
    public void onResponse(Response response) {

    }

    @Override
    public void onError(String responseText, Exception e) {

    }
}
