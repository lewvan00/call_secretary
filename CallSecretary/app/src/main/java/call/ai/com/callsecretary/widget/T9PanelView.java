package call.ai.com.callsecretary.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Method;

import call.ai.com.callsecretary.BaseActivity;
import call.ai.com.callsecretary.R;

public final class T9PanelView extends LinearLayout implements TextWatcher, OnClickListener, View.OnLongClickListener {

    private Context mContext;
    // 删除键
    private ImageButton mDeleteBtn;
    // 清除键
    private TextView mClearBtn;

    private EditText mPhoneNumEt;

    private ToneGenerator mToneGenerator;
    private boolean isEnableKeypadTone;

    public T9PanelView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public T9PanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @SuppressLint("NewApi")
    public T9PanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mToneGenerator != null) {
            mToneGenerator.release();
            mToneGenerator = null;
        }
        super.onDetachedFromWindow();
    }

    private void init() {
        View.inflate(getContext(), R.layout.layout_t9_panel, this);
        setOrientation(VERTICAL);

        findViewById(R.id.dialNum1).setOnClickListener(this);
        findViewById(R.id.dialNum2).setOnClickListener(this);
        findViewById(R.id.dialNum3).setOnClickListener(this);
        findViewById(R.id.dialNum4).setOnClickListener(this);
        findViewById(R.id.dialNum5).setOnClickListener(this);
        findViewById(R.id.dialNum6).setOnClickListener(this);
        findViewById(R.id.dialNum7).setOnClickListener(this);
        findViewById(R.id.dialNum8).setOnClickListener(this);
        findViewById(R.id.dialNum8).setOnClickListener(this);
        findViewById(R.id.dialNum9).setOnClickListener(this);
        findViewById(R.id.dialNum0).setOnClickListener(this);
        findViewById(R.id.dialNum0).setOnLongClickListener(this);
        findViewById(R.id.dialx).setOnClickListener(this);
        findViewById(R.id.dialx).setOnLongClickListener(this);
        findViewById(R.id.dialj).setOnClickListener(this);
        findViewById(R.id.dialj).setOnLongClickListener(this);

        // 清除键
        mClearBtn = (TextView) findViewById(R.id.tv_t9panel_clear_btn);
        // 删除键
        mDeleteBtn = (ImageButton) findViewById(R.id.ib_t9panel_delete_btn);
        mDeleteBtn.setOnClickListener(this);

        initToneGenerator();
    }

    private String getCurrentInput() {
        return mPhoneNumEt.getText().toString();
    }

    public void replaceViews(EditText phoneNumEt) {
        if (phoneNumEt != null) {
            this.mPhoneNumEt = phoneNumEt;
        }
    }

    private void initToneGenerator() {
        try {
            mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, (int) (ToneGenerator.MAX_VOLUME * 0.8));
        } catch (Exception ex) {
            mToneGenerator = null;
        }
    }

    public void setupInputEt() {
        // 使软键盘不弹出
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            mPhoneNumEt.setInputType(InputType.TYPE_NULL);
        } else {
            BaseActivity act = (BaseActivity) mContext;
            act.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(mPhoneNumEt, false);
            } catch (Exception e) {
            }
        }

        mPhoneNumEt.addTextChangedListener(this);
    }


    public void clearInput() {
        if (mPhoneNumEt != null) {
            // 清除键 和 删除键 置灰
            mClearBtn.setTextColor(mContext.getResources().getColor(R.color.color_c1c1c1));
            mDeleteBtn.setImageResource(R.drawable.ic_dial_delete_gray);

            mPhoneNumEt.setCursorVisible(false);
            mPhoneNumEt.setText("");
        }
    }

    public boolean isInputEmpty() {
        if (mPhoneNumEt == null) {
            return true;
        }
        return mPhoneNumEt.getText().toString().isEmpty();
    }

    public void setInputString(String inputString) {
        if (mPhoneNumEt != null) {
            mPhoneNumEt.setText(inputString);
            if (inputString != null) {
                mPhoneNumEt.setSelection(inputString.length());
            }
        }
    }

    public void appendChat(String str) {
        if (mPhoneNumEt == null) {
            return;
        }
        String text = mPhoneNumEt.getText().toString() + str;
        mPhoneNumEt.setText(text);
        mPhoneNumEt.setSelection(text.length());
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(getCurrentInput())) {
            // 输入为空, 清除键 和 删除键 置灰
            mClearBtn.setTextColor(mContext.getResources().getColor(R.color.color_c1c1c1));
            mDeleteBtn.setImageResource(R.drawable.ic_dial_delete_gray);
        } else {
            mClearBtn.setTextColor(mContext.getResources().getColor(R.color.text_color_sector_new));
            mDeleteBtn.setImageResource(R.drawable.dial_delete);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialNum0:
            case R.id.dialNum1:
            case R.id.dialNum2:
            case R.id.dialNum3:
            case R.id.dialNum4:
            case R.id.dialNum5:
            case R.id.dialNum6:
            case R.id.dialNum7:
            case R.id.dialNum8:
            case R.id.dialNum9:
            case R.id.dialx:
            case R.id.dialj:
                KeyPadView kv = (KeyPadView) v;
                appendChat(kv.getValue());
                playTone(kv.getToneId());
                break;
            case R.id.ib_t9panel_delete_btn:
                clearInput();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.dialj:
                // TODO: 2017/4/9
                break;
            case R.id.dialNum0:
                appendChat("+");
                break;
            case R.id.dialx:
                appendChat(",");
                break;
        }
        return true;
    }

    private void playTone(int toneId) {
        if (mToneGenerator != null && toneId != -1 && isEnableKeypadTone) {
            mToneGenerator.startTone(toneId, 180);
        }
    }

    public void enableKeypadTone(boolean isEnable) {
        isEnableKeypadTone = isEnable;
    }

    public boolean isEnableKeypadTone() {
        return isEnableKeypadTone;
    }

}