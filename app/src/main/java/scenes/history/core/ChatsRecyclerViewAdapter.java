package scenes.history.core;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saba.wifidirectchat.R;

import java.util.List;

import common.Utils;
import scenes.chat.core.ChatActivity;
import scenes.chat.core.ChatFragment;
import scenes.history.model.ChatModel;

public class ChatsRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<ChatModel> chats;

    private HistoryContractor.Presenter presenter;
    private Context context;

    public ChatsRecyclerViewAdapter(Context context, HistoryContractor.Presenter presenter) {
        super();
        this.presenter = presenter;
        this.context = context;
    }

    public void setChats(List<ChatModel> notes) {
        this.chats = notes;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_chat_info, viewGroup, false);
        return new ChatsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ChatsRecyclerViewHolder holder = (ChatsRecyclerViewHolder) viewHolder;
        final ChatModel chat = chats.get(i);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("chatId", chat.getId());
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle(v.getContext().getResources().getString(R.string.confirm))
                        .setMessage(String.format(v.getContext().getResources().getString(R.string.confirmDeleteChat), chat.getDeviceName()))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(v.getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                presenter.removeChatButtonClicked(chat.getId());
                            }
                        })
                        .setNegativeButton(v.getContext().getResources().getString(R.string.no), null).show();
                return true;
            }
        });

        holder.chatDeviceName.setText(chat.getDeviceName());
        holder.chatTime.setText(Utils.SDF.format(chat.getTime()));

        String count = chat.getMessagesCount() + "";
        holder.chatMessagesCount.setText(count);
    }

    @Override
    public int getItemCount() {
        return chats != null ? chats.size() : 0;
    }

}
