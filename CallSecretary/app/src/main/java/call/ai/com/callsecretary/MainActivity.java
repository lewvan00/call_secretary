package call.ai.com.callsecretary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import call.ai.com.callsecretary.floating.TestActivity;

/**
 * Created by lewvan on 2017/3/27.
 */

public class MainActivity extends Activity implements View.OnClickListener {
    CheckBox mCheckBox;
    EditText mFilterEditText;
    List<String> mFilterContentList = new ArrayList<>();
    ListView mFilterContentListView;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.setting);

        mCheckBox = (CheckBox) findViewById(R.id.auto_pickup_phone);
        Button setDone = (Button) findViewById(R.id.set_done);

        mCheckBox.setOnClickListener(this);
        setDone.setOnClickListener(this);

        mFilterEditText = (EditText) findViewById(R.id.editor);

        View addTv = findViewById(R.id.add_filter);
        addTv.setOnClickListener(this);

        mFilterContentListView = (ListView) findViewById(R.id.filter_content_listview);
        mFilterContentListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mFilterContentList.size();
            }

            @Override
            public Object getItem(int position) {
                return mFilterContentList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = new TextView(parent.getContext());
                textView.setText((CharSequence) getItem(position));
                return textView;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.auto_pickup_phone:
                CommonSharedPref.getInstance(this).setAutoPickupPhone(mCheckBox.isChecked());
                break;
            case R.id.set_done:
//                finish();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, TestActivity.class);
                startActivity(intent);
                break;
            case R.id.add_filter:
                String filterItem = mFilterEditText.getText().toString();
                if (!TextUtils.isEmpty(filterItem)) {
                    mFilterContentList.add(filterItem);
                    BaseAdapter adapter = (BaseAdapter) mFilterContentListView.getAdapter();
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    mFilterEditText.setText("");
                }
                break;
        }
    }
}
