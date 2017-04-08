package call.ai.com.callsecretary.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import call.ai.com.callsecretary.R;


public final class KeyPadView extends LinearLayout {

	private TextView mAlphaTv;
	private TextView mNumberTv;
	private String mValue;
	private int mToneId = -1;

	public KeyPadView(Context context) {
		super(context);
		initView();
	}

	public KeyPadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		getCustomAttrs(attrs);
	}

	@SuppressLint("NewApi")
	public KeyPadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
		getCustomAttrs(attrs);
	}

	private void initView() {
		setAttrs();

		View.inflate(getContext(), R.layout.view_dail_key, this);
		mAlphaTv = (TextView) findViewById(R.id.tv_alpha);
		mNumberTv = (TextView) findViewById(R.id.tv_number);
		LayoutParams lParams = (LayoutParams) mAlphaTv.getLayoutParams();
		lParams.weight = 1;
		mAlphaTv.setLayoutParams(lParams);
		
		LayoutParams rParams = (LayoutParams) mNumberTv.getLayoutParams();
		rParams.weight = 1;
		mNumberTv.setLayoutParams(rParams);
		
	}

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mAlphaTv.setEnabled(enabled);
    }

    private void setAttrs() {
		setBackgroundResource(R.drawable.dial_btn_bg);
		setClickable(true);
		setSoundEffectsEnabled(false);
		
		setOrientation(HORIZONTAL);
		setBaselineAligned(false);
		setGravity(Gravity.CENTER);
	}
	
	private void getCustomAttrs(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.KeyPadView);
			String appha = ta.getString(R.styleable.KeyPadView_alph);
			String number = ta.getString(R.styleable.KeyPadView_number);
			setText(appha, number);

            int textSize = ta.getInteger(R.styleable.KeyPadView_text_size, 0);
            if(textSize!=0){
                LayoutParams lParams = (LayoutParams) mAlphaTv.getLayoutParams();
                lParams.gravity = Gravity.CENTER;
                mAlphaTv.setTextSize(textSize);
                mAlphaTv.setGravity(Gravity.CENTER);
                mAlphaTv.setLayoutParams(lParams);
                mAlphaTv.setTypeface(Typeface.DEFAULT_BOLD);
                mNumberTv.setVisibility(View.GONE);
            }

			mValue = appha;
			mToneId = ta.getInt(R.styleable.KeyPadView_tone, -1);


			ta.recycle();
		}
	}
    public void setText(String alpha, String number) {
		mAlphaTv.setText(alpha);
		mNumberTv.setText(number);
	}
	
	public int getToneId() {
		return mToneId;
	}
	
	public String getValue() {
		return mValue;
	}

}
