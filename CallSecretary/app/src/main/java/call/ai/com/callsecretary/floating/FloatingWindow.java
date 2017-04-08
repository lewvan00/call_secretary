package call.ai.com.callsecretary.floating;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.lex.interactionkit.ui.InteractiveVoiceView;

import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.adapter.MessageAdapter;
import call.ai.com.callsecretary.utils.AvatarUtils;
import call.ai.com.callsecretary.utils.ChatUtils;
import call.ai.com.callsecretary.widget.DiffuseView;

/**
 * Created by Administrator on 2017/4/7.
 */

public class FloatingWindow extends FrameLayout {

    public interface UiInterface {
        void onClose();
    }

    private TextView mTitleTv;
    private RecyclerView mRecyclerView;

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
        mTitleTv = (TextView) findViewById(R.id.title);
        mRecyclerView = (RecyclerView) findViewById(R.id.chat_list);

        mAdapter = new MessageAdapter(ChatUtils.createTestChat(), AvatarUtils.getRandomAvatarResId());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

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
        post(new Runnable() {
            @Override
            public void run() {
                mDiffuseView.start();
            }
        });
    }
}
