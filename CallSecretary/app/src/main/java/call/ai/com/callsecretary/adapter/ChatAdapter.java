package call.ai.com.callsecretary.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Chat chat);
    }

    private OnItemClickListener onItemClickListener;


    public ChatAdapter() {
        mList = new ArrayList<>(ChatService.getInstance().getChatList());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final ChatViewHolder viewHolder = ChatViewHolder.create();
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(viewHolder.getData());
            }
        });
        return viewHolder;
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