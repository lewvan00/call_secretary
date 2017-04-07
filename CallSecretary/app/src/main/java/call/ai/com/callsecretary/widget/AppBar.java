package call.ai.com.callsecretary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import call.ai.com.callsecretary.R;

/**
 * Created by Administrator on 2017/4/7.
 */

public class AppBar extends FrameLayout{
    private View leftView;
    private TextView title;
    private ViewGroup menuLayout;

    public AppBar(Context context) {
        this(context, null);
    }

    public AppBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_appbar, this);
        initViews();
    }

    private void initViews() {
        leftView = findViewById(R.id.layout_left);
        title = (TextView)findViewById(R.id.title);
        menuLayout = (ViewGroup) findViewById(R.id.menu_layout);
    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public void enableBackButton(boolean isable) {
        if (isable) {
            leftView.setVisibility(VISIBLE);
        } else {
            leftView.setVisibility(GONE);
        }
    }

    public void setOnBackClickListener(View.OnClickListener listener) {
        leftView.setOnClickListener(listener);
    }

    public void addMenuLayout(View view) {
        menuLayout.addView(view);
    }
}
