package scenes.chat.core;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.saba.wifidirectchat.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView messageLeft;
    public TextView messageRight;
    public TextView dateLeft;
    public TextView dateRight;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageLeft = itemView.findViewById(R.id.chat_message_left);
        messageRight = itemView.findViewById(R.id.chat_message_right);
        dateRight = itemView.findViewById(R.id.chat_right_message_date);
        dateLeft = itemView.findViewById(R.id.chat_left_message_date);
    }

}
