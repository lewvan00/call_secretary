package call.ai.com.callsecretary;

import android.os.Bundle;

public class SettingWhiteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackButton(true);
        setBackClickFinish();
        setBarTitle(R.string.whitelist);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_white;
    }
}
