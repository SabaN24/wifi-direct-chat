package scenes.chat.core;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.saba.wifidirectchat.R;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import p2p.Client;
import p2p.Pipe;
import p2p.Server;
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
    private WifiP2pManager.PeerListListener peerListListener;
    private List<WifiP2pDevice> peers = new ArrayList<>();
    private IntentFilter intentFilter;
    private ChatContractor.Presenter presenter;
    private WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    private ChatAdapter adapter;
    private Handler handler;
    private Server server;
    private Client client;
    private Pipe pipe;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initListeners();

        manager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(getActivity(), getActivity().getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, peerListListener, connectionInfoListener);

        initIntentFilter();
        discoverPeers();

        setupUIElements(view);
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            int chatId = bundle.getInt("CHAR_ID", -1);
            String deviceName = bundle.getString("DEVICE_NAME", "");
            presenter = new ChatPresenter(this, chatId, deviceName);
        } else {
            presenter = new ChatPresenter(this, -1, "");
        }
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

    @Override
    public void sendMessage(String text) {
        pipe.write(text.getBytes());
    }

    private void initListeners() {
        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                List<WifiP2pDevice> refreshedPeers = (List<WifiP2pDevice>) peerList.getDeviceList();
                Log.d("@SABA@", "HERE");
                if (!refreshedPeers.equals(peers)) {
                    peers.clear();
                    peers.addAll(refreshedPeers);

                    // Perform any other updates needed based on the new list of
                    // peers connected to the Wi-Fi P2P network.
                    Log.d("@SABA@: ", peers.get(0).deviceName);
                }

                if (peers.size() == 0) {
                    //TODO SHOW THAT NO PEERS ARE AVAILABLE
                } else {
                    WifiP2pDevice device = peers.get(0);
                    WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = device.deviceAddress;

                    manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(int reason) {

                        }
                    });
                }
            }
        };

        connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                final InetAddress groupOwner = info.groupOwnerAddress;
                if (info.groupFormed) {
                    if (info.isGroupOwner) {
                        server = new Server(pipe, handler);
                        server.start();
                    } else {
                        client = new Client(groupOwner.getHostAddress(), pipe, handler);
                        client.start();
                    }
                }
            }
        };

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 1) {
                    byte[] buff = (byte[]) msg.obj;
                    String text = new String(buff, 0, msg.arg1);
                    presenter.messageRecieved(text);
                }
                return true;
            }
        });
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
                //TODO SHOW ERROR ON UI (COULD NOT DISCOVER PEERS)
            }
        });
    }
}
