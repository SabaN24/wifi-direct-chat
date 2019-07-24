package scenes.chat.core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.saba.wifidirectchat.R;

import java.util.List;

import scenes.chat.model.MessageModel;

public class ChatFragment extends Fragment
                          implements ChatContractor.View {

    private RecyclerView recyclerView;
    private Button btnSend;
    private EditText etMessage;

    private ChatContractor.Presenter presenter;
    private ChatAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUIElements(view);
        presenter = new ChatPresenter(this);
        setupRecycler();
        seuptBtnSendOnClickAction();
        presenter.start();
    }

    private void setupUIElements(View view) {
        recyclerView = view.findViewById(R.id.recycler_chat);
        etMessage = view.findViewById(R.id.et_message);
        btnSend = view.findViewById(R.id.btn_send);
    }

    private void setupRecycler() {
        adapter = new ChatAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void seuptBtnSendOnClickAction() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.btnSendTapped(etMessage.getText().toString());
            }
        });
    }

    @Override
    public void draw(List<MessageModel> data) {
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clearInput() {
        etMessage.setText("");
    }
}
