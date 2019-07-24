package scenes.history.core;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.saba.wifidirectchat.R;

public class ChatsRecyclerViewHolder extends RecyclerView.ViewHolder {

    TextView chatDeviceName;
    TextView chatTime;
    TextView chatMessagesCount;

    public ChatsRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        chatDeviceName = itemView.findViewById(R.id.chatDeviceName);
        chatTime = itemView.findViewById(R.id.chatTime);
        chatMessagesCount = itemView.findViewById(R.id.chatMessagesCount);
    }

}
