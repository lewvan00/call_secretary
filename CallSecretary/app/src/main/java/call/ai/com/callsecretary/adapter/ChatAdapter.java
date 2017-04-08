package call.ai.com.callsecretary.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import call.ai.com.callsecretary.bean.Chat;
import call.ai.com.callsecretary.chat.ChatService;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ChatService.LoaderListener {
    List<Chat> mList = new ArrayList<>();

    public ChatAdapter() {
        mList = new ArrayList<>(ChatService.getInstance().getChatList());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ChatViewHolder.create();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChatViewHolder) {
            ((ChatViewHolder) holder).setData(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onLoadDone(List<Chat> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }
}
