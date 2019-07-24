package scenes.history.core;

import android.content.Context;
import android.content.DialogInterface;
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

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import scenes.chat.core.ChatFragment;
import scenes.chat.model.ChatOpenMode;
import scenes.history.model.ChatModel;

public class ChatsRecyclerViewAdapter extends RecyclerView.Adapter {

    private List<ChatModel> chats;

    private HistoryContractor.Presenter presenter;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd - hh:mm:ss", Locale.US);

    public ChatsRecyclerViewAdapter(Context context, HistoryContractor.Presenter presenter) {
        super();
        this.presenter = presenter;
    }

    public void setChats(List<ChatModel> notes) {
        this.chats = notes;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_chat_info, viewGroup, false);
        final ChatModel chat = chats.get(i);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ChatFragment();
                Bundle args = new Bundle();
                args.putInt("id", chat.getId());
                args.putSerializable("mode", ChatOpenMode.VIEW);
                fragment.setArguments(args);
                FragmentManager fragmentManager = ((AppCompatActivity) v.getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.scene, fragment);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle(v.getContext().getResources().getString(R.string.confirm))
                        .setMessage(String.format(v.getContext().getResources().getString(R.string.confirmClearHistory), chat.getDeviceName()))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(v.getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                presenter.removeChatButtonClicked(chat.getId());
                            }
                        })
                        .setNegativeButton(v.getContext().getResources().getString(R.string.yes), null).show();
                return true;
            }
        });
        return new ChatsRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ChatsRecyclerViewHolder holder = (ChatsRecyclerViewHolder) viewHolder;
        ChatModel chat = chats.get(i);

        holder.chatDeviceName.setText(chat.getDeviceName());
        holder.chatTime.setText(sdf.format(chat.getTime()));
        holder.chatMessagesCount.setText(chat.getMessagesCount());
    }

    @Override
    public int getItemCount() {
        return chats != null ? chats.size() : 0;
    }

}
