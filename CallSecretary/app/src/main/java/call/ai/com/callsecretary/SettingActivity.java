package call.ai.com.callsecretary;

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
        initFlowLayout();
    }

    private void initFlowLayout() {
        TagFlowLayout flowLayout = (TagFlowLayout) findViewById(R.id.flowlayout);
        String[] list = getResources().getStringArray(R.array.array_interception);
        TagAdapter adapter = new TagAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String value) {
                TextView tv = (TextView) View.inflate(SettingActivity.this, R.layout.layout_tag, null);
                tv.setText(value);
                return tv;
            }
        };
        flowLayout.setAdapter(adapter);
        adapter.setSelectedList(1, 3, 4, 5);
    }

    private void initViews() {
        mCheckBox = (CheckBox) findViewById(R.id.auto_pickup_phone);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CommonSharedPref.getInstance(SettingActivity.this).setAutoPickupPhone(isChecked);
            }
        });
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
