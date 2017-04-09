package call.ai.com.callsecretary;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

public class SettingInterceptionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackButton(true);
        setBackClickFinish();
        setBarTitle(R.string.setting_interception);
        initFlowLayout();
    }

    private void initFlowLayout() {
        TagFlowLayout flowLayout = (TagFlowLayout) findViewById(R.id.flowlayout);
        String[] list = getResources().getStringArray(R.array.array_interception);
        TagAdapter adapter = new TagAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String value) {
                TextView tv = (TextView) View.inflate(SettingInterceptionActivity.this, R.layout.layout_tag, null);
                tv.setText(value);
                return tv;
            }
        };
        flowLayout.setAdapter(adapter);
        adapter.setSelectedList(1, 3, 4, 5);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_interception;
    }
}
