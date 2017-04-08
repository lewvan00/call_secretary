package call.ai.com.callsecretary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import call.ai.com.callsecretary.utils.CommonSharedPref;
import call.ai.com.callsecretary.widget.AlertDialog;
import call.ai.com.callsecretary.widget.AppBar;

/**
 * 公共activity，包含共用方法
 */

public abstract class BaseActivity extends AppCompatActivity {
    private AppBar appBar;
    private int mColorEggCount;
    private View mIpSettingView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        appBar = (AppBar) findViewById(R.id.appbar);
        mIpSettingView = findViewById(R.id.ip_setting_layout);
        if (mIpSettingView != null) {
            mIpSettingView.setVisibility(View.GONE);
            initIpSetting();
            appBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mColorEggCount++;
                    if (mColorEggCount == 5) {
                        mColorEggCount = 0;
                        mIpSettingView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void initIpSetting() {
        final EditText editText = (EditText) findViewById(R.id.service_ip);
        String serviceIp = CommonSharedPref.getInstance(getApplicationContext()).getServiceIp();
        editText.setText(serviceIp);
        Button btn = (Button) findViewById(R.id.set_ip_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText())) {
                    CommonSharedPref.getInstance(getApplicationContext()).setServiceIp(editText.getText().toString());
                    mIpSettingView.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void setBarTitle(int id) {
        setBarTitle(getString(id));
    }

    protected void setBarTitle(String text) {
        if (appBar == null) return;
        appBar.setTitle(text);
    }

    protected void enableBackButton(boolean isable) {
        if (appBar == null) return;
        appBar.enableBackButton(isable);
    }

    protected void setOnBackClickListener(View.OnClickListener listener) {
        if (appBar == null) return;
        appBar.setOnBackClickListener(listener);
    }
    
    protected void setBackClickFinish() {
        setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void addMenuLayout(View view) {
        if (appBar == null) return;
        appBar.addMenuLayout(view);
    }

    protected abstract int getLayoutId();

    private AlertDialog mAlertDlg;

    protected void showCommonAlert(int titleResId, int msgResId, int posTxtResId, int nagTxtResId,
                                View.OnClickListener l) {
        if (mAlertDlg == null) {
            mAlertDlg = new AlertDialog(this);
        }
        if (titleResId != 0) {
            mAlertDlg.setTitle(getText(titleResId));
        }
        mAlertDlg.setMessage(getText(msgResId));
        mAlertDlg.setPositiveButton(getText(posTxtResId), l);
        mAlertDlg.setNegativeButton(getText(nagTxtResId), l);
        mAlertDlg.show();
    }

    protected void hideCommonAlert() {
        if (mAlertDlg != null) {
            if (mAlertDlg.isShowing()) {
                mAlertDlg.dismiss();
            }
            mAlertDlg = null;
        }
    }
}
