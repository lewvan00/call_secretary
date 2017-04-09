package call.ai.com.callsecretary;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import call.ai.com.callsecretary.utils.CommonSharedPref;

public class SettingWhiteActivity extends BaseActivity {

    List<Map<String, String>> maps;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableBackButton(true);
        setBackClickFinish();
        setBarTitle(R.string.whitelist);

        Set<String> list = CommonSharedPref.getInstance().getWhiteList();
        maps = new ArrayList<>();
        for(String num: list) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("num", num);
            maps.add(hashMap);
        }
        ListView listView = (ListView) findViewById(R.id.listview);
        final SimpleAdapter adapter = new SimpleAdapter(this, maps, R.layout.layout_white_item, new String[]{"num"}, new int[]{R.id.text});
        listView.setAdapter(adapter);
        final EditText editText = (EditText) findViewById(R.id.edit);
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("num", editText.getText().toString());
                maps.add(hashMap);
                adapter.notifyDataSetChanged();
                editText.setText("");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HashSet<String> strings = new HashSet<>();
        for(Map<String, String> map:maps) {
            strings.add(map.get("num"));
        }
        CommonSharedPref.getInstance().setWhiteList(strings);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting_white;
    }
}
