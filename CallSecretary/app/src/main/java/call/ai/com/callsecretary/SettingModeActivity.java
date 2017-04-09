package call.ai.com.callsecretary;

import android.os.Bundle;

public class SettingModeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackButton(true);
        setBackClickFinish();
        setBarTitle(R.string.setting_mode);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_mode;
    }
}
