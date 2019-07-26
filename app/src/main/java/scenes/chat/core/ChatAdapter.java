package scenes.chat.core;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.saba.wifidirectchat.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import common.Utils;
import scenes.chat.model.MessageModel;

public class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private List<MessageModel> data;

    public ChatAdapter() {
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_cell, viewGroup, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {
        MessageModel model = data.get(i);
        messageViewHolder.dateLeft.setVisibility(View.INVISIBLE);
        messageViewHolder.dateRight.setVisibility(View.INVISIBLE);
        final String date = Utils.SDF.format(data.get(i).getTime());
        if(model.isResponse()) {
            messageViewHolder.messageLeft.setVisibility(View.INVISIBLE);
            messageViewHolder.messageRight.setText(data.get(i).getText());
            messageViewHolder.messageRight.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    messageViewHolder.dateRight.setText(date);
                    messageViewHolder.dateRight.setVisibility(View.INVISIBLE);
                    return false;
                }
            });
        } else {
            messageViewHolder.messageRight.setVisibility(View.INVISIBLE);
            messageViewHolder.messageLeft.setText(data.get(i).getText());
            messageViewHolder.messageLeft.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    messageViewHolder.dateLeft.setText(date);
                    messageViewHolder.dateLeft.setVisibility(View.VISIBLE);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<MessageModel> data) {
        this.data = data;
    }
}
