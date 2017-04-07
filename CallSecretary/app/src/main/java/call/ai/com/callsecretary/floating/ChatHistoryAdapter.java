package call.ai.com.callsecretary.floating;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.bean.ChatMessage;

/**
 * Created by Administrator on 2017/3/31.
 */

public class ChatHistoryAdapter extends BaseAdapter {

    List<ChatMessage> mChatMessageList = new ArrayList<>();

    private Context mContext;


    public ChatHistoryAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mChatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return mChatMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.layout_chat_item, null);
            viewHolder = new ViewHolder();
            viewHolder.leftMessage = (TextView) convertView.findViewById(R.id.message_left);
            viewHolder.rightMessage = (TextView) convertView.findViewById(R.id.message_right);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position < mChatMessageList.size()) {
            ChatMessage chatMessage = mChatMessageList.get(position);
            if (chatMessage != null) {
                if (chatMessage.getUserType() == ChatMessage.TYPE_CALLER) {
                    viewHolder.leftMessage.setVisibility(View.VISIBLE);
                    viewHolder.leftMessage.setText(chatMessage.getMessage());
                    viewHolder.rightMessage.setVisibility(View.GONE);
                } else {
                    viewHolder.leftMessage.setVisibility(View.GONE);
                    viewHolder.rightMessage.setText(chatMessage.getMessage());
                    viewHolder.rightMessage.setVisibility(View.VISIBLE);
                }
            }
        }
        return convertView;
    }


    public void addChatMessage(ChatMessage chatMessage) {
        if (chatMessage == null) return;
        mChatMessageList.add(chatMessage);
        notifyDataSetChanged();
    }

    public void addChatMessages(List<ChatMessage> chatMessages) {
        if (chatMessages == null) return;
        mChatMessageList.addAll(chatMessages);
        notifyDataSetChanged();
    }

    public void clearMessages() {
        if (mChatMessageList == null || mChatMessageList.isEmpty()) return;
        mChatMessageList.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView leftMessage;
        TextView rightMessage;
    }

}
