package scenes.chat.core;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saba.wifidirectchat.R;

import java.util.ArrayList;
import java.util.List;

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
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int i) {
        MessageModel model = data.get(i);
        messageViewHolder.message.setText(model.getText());
        messageViewHolder.date.setText(model.getTime().toString());
        // TODO (Levan)
        // left/right side
        if(model.isResponse()) {

        } else {

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
