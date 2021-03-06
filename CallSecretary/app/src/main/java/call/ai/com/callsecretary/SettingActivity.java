package call.ai.com.callsecretary;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import call.ai.com.callsecretary.utils.CommonSharedPref;

public class SettingActivity extends BaseActivity {

    CheckBox mCheckBox;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initAppbar();
        initViews();
    }

    private void initViews() {
        mCheckBox = (CheckBox) findViewById(R.id.auto_pickup_phone);
        boolean is_auto = CommonSharedPref.getInstance().getAutoPickupPhone();
        mCheckBox.setChecked(is_auto);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CommonSharedPref.getInstance(SettingActivity.this).setAutoPickupPhone(isChecked);
            }
        });

        findViewById(R.id.layout_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SettingModeActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.layout_whilelist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SettingWhiteActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.layout_interception).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SettingInterceptionActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        TextView tvMode = (TextView) findViewById(R.id.tv_mode);
        int mode = CommonSharedPref.getInstance().getWorkMode();
        if (mode == 0) {
            tvMode.setText("正常模式");
        } else if (mode == 1) {
            tvMode.setText("会议模式");
        } else if (mode == 2) {
            tvMode.setText("勿扰模式");
        }
    }

    private void initAppbar() {
        enableBackButton(true);
        setBackClickFinish();
        setBarTitle(R.string.setting_title);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

}
