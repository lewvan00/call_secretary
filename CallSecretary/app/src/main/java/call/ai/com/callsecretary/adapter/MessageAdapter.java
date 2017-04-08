package call.ai.com.callsecretary.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import call.ai.com.callsecretary.bean.Chat;
import call.ai.com.callsecretary.bean.ChatMessage;

/**
 * Created by Administrator on 2017/4/7.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_CALLER_MESSAGE = 1;
    private static final int TYPE_SECRETARY_MESSAGE = 2;
    private Chat chat;

    public MessageAdapter(Chat chat) {
        this.chat = chat;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chat.getMessages().get(position);
        if (message.getUserType() == ChatMessage.TYPE_CALLER) {
            return TYPE_CALLER_MESSAGE;
        }
        return TYPE_SECRETARY_MESSAGE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CALLER_MESSAGE) {
            return MessageViewHolder.createCallerMessage();
        } else if (viewType == TYPE_SECRETARY_MESSAGE) {
            return MessageViewHolder.createSecretaryMessage();
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageViewHolder) {
            ((MessageViewHolder) holder).setMessageText(chat.getMessages().get(position).getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chat.getMessages().size();
    }

    public void addCallerMessage(String text) {
        chat.addCallerMessage(text);
        notifyDataSetChanged();
    }

    public void addSecretaryMessage(String text) {
        chat.addSecretaryMessage(text);
        notifyDataSetChanged();
    }

    public void setChat(Chat chat) {
        this.chat = chat;
        notifyDataSetChanged();
    }

    public Chat getChat() {
        return chat;
    }
}
