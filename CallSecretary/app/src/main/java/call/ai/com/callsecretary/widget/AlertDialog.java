package call.ai.com.callsecretary.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import call.ai.com.callsecretary.R;

public class AlertDialog implements View.OnClickListener{

	public final static int BUTTON_POSITIVE = R.id.btn_positive;
	public final static int BUTTON_NEGATIVE = R.id.btn_negative;
	
	protected Dialog mAlertDlg;
	protected TextView mTitleView;
	protected TextView mMessageView;
	protected Button mNegativeButton;
	protected Button mPositiveButton;
    protected CheckBox mCheckBox;
	LinearLayout mLayoutButton;
	View mDelimitBtn;

	public AlertDialog(Context context) {
		mAlertDlg = new Dialog(context, R.style.AlertDialog);
		mAlertDlg.setContentView(R.layout.layout_alert_dialog);
		Window window = mAlertDlg.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(params);
		
		mTitleView=(TextView)window.findViewById(R.id.tv_alert_title);
		mMessageView=(TextView)window.findViewById(R.id.tv_alert_message);
		mDelimitBtn = window.findViewById(R.id.v_delimit_btn);
		mNegativeButton = (Button)window.findViewById(R.id.btn_negative);
		mPositiveButton = (Button)window.findViewById(R.id.btn_positive);
		mLayoutButton=(LinearLayout)window.findViewById(R.id.Layout_btn_alert);
        mCheckBox = (CheckBox)window.findViewById(R.id.cb_selected);
	}
	
	public void setTitle(int resId){
		mTitleView.setVisibility(View.VISIBLE);
		mTitleView.setText(resId);
	}
	
	public void setTitle(CharSequence title) {
		mTitleView.setVisibility(View.VISIBLE);
		mTitleView.setText(title);
	}
	
	public void setMessage(int resId) {
		mMessageView.setVisibility(View.VISIBLE);
		mMessageView.setText(resId);
	}
 
	public void setMessage(CharSequence message){
		mMessageView.setVisibility(View.VISIBLE);
		mMessageView.setText(message);
	}	
	
	public void setMessageGravity(int gravity){
		mMessageView.setGravity(gravity);
	}

	public TextView getmMessageView() {
		return mMessageView;
	}

	public void setPositiveButton(CharSequence text, final View.OnClickListener listener){
		mLayoutButton.setVisibility(View.VISIBLE);
		mPositiveButton.setVisibility(View.VISIBLE);
		if(mNegativeButton.getVisibility() == View.VISIBLE){
			mDelimitBtn.setVisibility(View.VISIBLE);
		}
		
		mPositiveButton.setText(text);
		if(listener != null){
			mPositiveButton.setOnClickListener(listener);			
		}else{
			mPositiveButton.setOnClickListener(this);
		}
	}
 

	public void setNegativeButton(CharSequence text, final View.OnClickListener listener){
		mLayoutButton.setVisibility(View.VISIBLE);
		mNegativeButton.setVisibility(View.VISIBLE);
		
		if(mPositiveButton.getVisibility() == View.VISIBLE){
			mDelimitBtn.setVisibility(View.VISIBLE);
		}
		
		mNegativeButton.setText(text);
		if(listener != null){
			mNegativeButton.setOnClickListener(listener);			
		}else{
			mNegativeButton.setOnClickListener(this);			
		}
	}

    public void setCheckBokMessage(int resId) {
        mCheckBox.setVisibility(View.VISIBLE);
        mCheckBox.setText(resId);
    }

    public void setCheckBokMessage(CharSequence message){
        mCheckBox.setVisibility(View.VISIBLE);
        mCheckBox.setText(message);
    }

    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);
    }

    public boolean getChecked() {
        return mCheckBox.isChecked();
    }

	public void setOnDismissListener(OnDismissListener listener){
		mAlertDlg.setOnDismissListener(listener);
	}
	
	
    public void setCanceledOnTouchOutside(boolean cancle){
    	mAlertDlg.setCanceledOnTouchOutside(cancle);
    }
    
    public void setCancelable(boolean cancelable){
    	mAlertDlg.setCancelable(cancelable);
    }
    
    public void setOnKeyListener(OnKeyListener l){
    	mAlertDlg.setOnKeyListener(l);    	
    }
    
    public void show(){
        try {
			if (mAlertDlg.getWindow()!=null) {
				mAlertDlg.show();
				Window window = mAlertDlg.getWindow();
				WindowManager.LayoutParams params = window.getAttributes();
				params.width = WindowManager.LayoutParams.MATCH_PARENT;
				window.setAttributes(params);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public boolean isShowing(){
		return mAlertDlg.isShowing();
	}
	
	public void dismiss() {
		mAlertDlg.dismiss();
	}
	
	@Override
	public void onClick(View v) {
		dismiss();		
	}

    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        if(mAlertDlg != null) {
            mAlertDlg.setOnCancelListener(listener);
        }
    }
}
