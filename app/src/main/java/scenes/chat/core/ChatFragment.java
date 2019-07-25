package scenes.chat.core;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
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

import p2p.WiFiDirectBroadcastReceiver;
import scenes.chat.model.MessageModel;

public class ChatFragment extends Fragment
        implements ChatContractor.View {

    private RecyclerView recyclerView;
    private Button btnSend;
    private EditText etMessage;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WiFiDirectBroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private ChatContractor.Presenter presenter;
    private ChatAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        manager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(getActivity(), getActivity().getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel);

        initIntentFilter();
        discoverPeers();

        setupUIElements(view);
        presenter = new ChatPresenter(this);
        setupRecycler();
        setupBtnSendOnClickAction();
        presenter.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
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

    private void setupBtnSendOnClickAction() {
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

    private void initIntentFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void discoverPeers() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }
}
