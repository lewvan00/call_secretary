package call.ai.com.callsecretary;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import call.ai.com.callsecretary.utils.CommonSharedPref;

public class SettingModeActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackButton(true);
        setBackClickFinish();
        setBarTitle(R.string.setting_mode);

        ((RadioButton)findViewById(R.id.common)).setOnCheckedChangeListener(this);
        ((RadioButton)findViewById(R.id.meetting)).setOnCheckedChangeListener(this);
        ((RadioButton)findViewById(R.id.warnning)).setOnCheckedChangeListener(this);

        int mode = getMode();
        if (mode == 0) {
            ((RadioButton)findViewById(R.id.common)).setChecked(true);
        } else if (mode == 1) {
            ((RadioButton)findViewById(R.id.meetting)).setChecked(true);
        } else if (mode == 2) {
            ((RadioButton)findViewById(R.id.warnning)).setChecked(true);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_mode;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isChecked)
            return;
        int id = buttonView.getId();
        if (id == R.id.common) {
            setMode(0);
        } else if (id == R.id.meetting) {
            setMode(1);
        } else if (id == R.id.warnning) {
            setMode(2);
        }
    }

    private int getMode() {
        return CommonSharedPref.getInstance().getWorkMode();
    }

    private void setMode(int i) {
        CommonSharedPref.getInstance().setWorkMode(i);
    }
}
