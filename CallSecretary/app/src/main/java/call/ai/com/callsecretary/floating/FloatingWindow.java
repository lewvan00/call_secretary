package call.ai.com.callsecretary.floating;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.lex.interactionkit.ui.InteractiveVoiceView;
import com.amazonaws.services.lexrts.model.PostContentResult;

import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.adapter.MessageAdapter;
import call.ai.com.callsecretary.bean.Chat;
import call.ai.com.callsecretary.utils.AvatarUtils;
import call.ai.com.callsecretary.widget.DiffuseView;

/**
 * Created by Administrator on 2017/4/7.
 */

public class FloatingWindow extends FrameLayout {

    public Chat getChat() {
        return chat;
    }

    public int getResImageId() {
        return resImageId;
    }

    public interface UiInterface {
        void onClose();
        void onTitleClick();
    }

    private TextView mTitleTv;
    private RecyclerView mRecyclerView;
    private Chat chat;
    private int resImageId;

    public MessageAdapter getMessageAdapter() {
        return mAdapter;
    }

    private MessageAdapter mAdapter;

    private InteractiveVoiceView mInteractiveVoiceView;
    private UiInterface mUiInterface;
    private DiffuseView mDiffuseView;
    private View mChatContentLyt;
    
    public FloatingWindow(Context context) {
        this(context, null);
    }

    public FloatingWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_floating_view, this);
        initViews();
    }

    private void initViews() {
        chat = new Chat();
        chat.setPhone("1586329525");
        chat.setTime(System.currentTimeMillis());
        resImageId = AvatarUtils.getRandomAvatarResId();

        mTitleTv = (TextView) findViewById(R.id.title);
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_list);

        mAdapter = new MessageAdapter(chat, resImageId);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        mTitleTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUiInterface != null) {
                    mUiInterface.onTitleClick();
                }
            }
        });

        findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUiInterface != null) mUiInterface.onClose();
            }
        });

        mInteractiveVoiceView = (InteractiveVoiceView) findViewById(R.id.interactive_voice_view);
        mDiffuseView = (DiffuseView) findViewById(R.id.diffuse_view);
        mChatContentLyt = findViewById(R.id.chat_content_lyt);
        mChatContentLyt.setVisibility(GONE);
//        mDiffuseView.setVisibility(GONE);
        mDiffuseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatingWindowsService.getServiceInstance().callRingoff();
                FloatingWindowsService.getServiceInstance().hideFloatingWindows();
            }
        });
    }

    public void setOnArrowTouchListener(View.OnTouchListener listener) {
        findViewById(R.id.arrow_bar).setOnTouchListener(listener);
    }

    public void setUiInterface(UiInterface uiInterface) {
        this.mUiInterface = uiInterface;
    }

    public void setTitle(String chatName) {
        mTitleTv.setText(chatName);
    }

    public InteractiveVoiceView getInteractiveVoiceView() {
        return mInteractiveVoiceView;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setAlpha(0);
        animate().alpha(1).setDuration(1500).start();
//        post(new Runnable() {
//            @Override
//            public void run() {
//                mDiffuseView.start();
//            }
//        });
        mDiffuseView.start();
    }

    public void ringConnect() {
        mDiffuseView.animate().translationYBy(getHeight()/3).setDuration(500).start();
        mDiffuseView.stop();
        mChatContentLyt.setVisibility(VISIBLE);
        mChatContentLyt.scrollTo(0, mChatContentLyt.getHeight());
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mChatContentLyt.scrollTo(0, (int) (mChatContentLyt.getHeight()*(1-value)));
            }
        });
        animator.start();
    }

    public void addMessage(PostContentResult result){
        if(result==null||mAdapter==null||mRecyclerView==null||
                TextUtils.isEmpty(result.getInputTranscript())||
                TextUtils.isEmpty(result.getMessage())
                ) return;
        mAdapter.addCallerMessage(result.getInputTranscript());
        mAdapter.addSecretaryMessage(result.getMessage());
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }
}
