package call.ai.com.callsecretary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.bean.Chat;
import call.ai.com.callsecretary.utils.CallSecretaryApplication;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ChatViewHolder extends RecyclerView.ViewHolder {

    TextView phone;
    TextView time;
    ImageView image;

    private ChatViewHolder(View itemView) {
        super(itemView);
        initViews();
    }

    private void initViews() {
        phone = $(R.id.phone);
        time = $(R.id.time);
        image = $(R.id.image);
    }

    @SuppressWarnings("unchecked")
    private <T> T $(int resd){
        return (T)itemView.findViewById(resd);
    }

    public void setData(Chat chat) {
        phone.setText(chat.getPhone());
        time.setText(String.valueOf(chat.getTime()));
    }

    public static ChatViewHolder create() {
        return new ChatViewHolder(View.inflate(getContext(), R.layout.layout_chat_item, null));
    }

    public static Context getContext() {
        return CallSecretaryApplication.getContext();
    }
}
