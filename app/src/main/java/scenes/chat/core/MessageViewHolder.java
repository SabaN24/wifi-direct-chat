package scenes.chat.core;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.saba.wifidirectchat.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView message;
    public TextView date;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        message = itemView.findViewById(R.id.chat_message);
        date = itemView.findViewById(R.id.chat_message_date);
    }
}
