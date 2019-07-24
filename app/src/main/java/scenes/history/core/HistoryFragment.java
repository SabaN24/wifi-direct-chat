package scenes.history.core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saba.wifidirectchat.R;

import java.util.List;
import java.util.Objects;

import scenes.history.model.ChatModel;

public class HistoryFragment extends Fragment
        implements HistoryContractor.View {

    private HistoryContractor.Presenter presenter;

    private RecyclerView chatsRecyclerView;

    private ChatsRecyclerViewAdapter chatsRecyclerViewAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new HistoryPresenter(this);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(R.string.history);
        chatsRecyclerViewAdapter = new ChatsRecyclerViewAdapter(getContext(), presenter);
        chatsRecyclerView = view.findViewById(R.id.chatsRecyclerView);
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatsRecyclerView.setAdapter(chatsRecyclerViewAdapter);
        view.findViewById(R.id.clearHistoryButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clearHistoryButtonClicked();
            }
        });
        presenter.start();
    }

    @Override
    public void drawChats(List<ChatModel> chats) {
        chatsRecyclerViewAdapter.setChats(chats);
        chatsRecyclerViewAdapter.notifyDataSetChanged();
    }

}
