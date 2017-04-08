package call.ai.com.callsecretary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import call.ai.com.callsecretary.R;
import call.ai.com.callsecretary.utils.CallSecretaryApplication;

/**
 * Created by Administrator on 2017/4/7.
 */

public abstract class MessageViewHolder extends RecyclerView.ViewHolder {
    public MessageViewHolder(View itemView) {
        super(itemView);
    }

    public static Context getContext() {
        return CallSecretaryApplication.getContext();
    }

    public static MessageViewHolder createCallerMessage(int resId) {
        return new CallerMessage(View.inflate(getContext(), R.layout.layout_caller_message, null), resId);
    }

    public static MessageViewHolder createSecretaryMessage() {
        return new SecretaryMessage(View.inflate(getContext(), R.layout.layout_secretary_message, null));
    }

    public abstract void setMessageText(String text);

    private static class CallerMessage extends MessageViewHolder {

        CallerMessage(View itemView, int resId) {
            super(itemView);
            ((ImageView)itemView.findViewById(R.id.image)).setImageResource(resId);
        }

        @Override
        public void setMessageText(String text) {
            ((TextView)itemView.findViewById(R.id.message)).setText(text);
        }
    }

    private static class SecretaryMessage extends MessageViewHolder {

        SecretaryMessage(View itemView) {
            super(itemView);
        }

        @Override
        public void setMessageText(String text) {
            ((TextView)itemView.findViewById(R.id.message)).setText(text);
        }
    }
}
