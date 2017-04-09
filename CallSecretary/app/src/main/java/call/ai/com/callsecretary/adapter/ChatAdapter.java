package call.ai.com.callsecretary.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import call.ai.com.callsecretary.bean.Chat;
import call.ai.com.callsecretary.chat.ChatService;
import call.ai.com.callsecretary.utils.AvatarUtils;

/**
 * Created by Administrator on 2017/4/8.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ChatService.LoaderListener {
    List<Chat> mList = new ArrayList<>();

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Chat chat, int imageResId);
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
                    onItemClickListener.onItemClick(viewHolder.getData(), viewHolder.getImageResId());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChatViewHolder) {
            ((ChatViewHolder) holder).setData(mList.get(position));
            ((ChatViewHolder) holder).setImageResId(AvatarUtils.getRandomAvatarResId());
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onListChange(List<Chat> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }
}
