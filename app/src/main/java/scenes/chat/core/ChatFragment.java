package scenes.chat.core;

import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.saba.wifidirectchat.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import common.Utils;
import p2p.ConnectionConstants;
import p2p.WiFiDirectBroadcastReceiver;
import scenes.chat.model.MessageModel;
import scenes.history.core.HistoryFragment;

public class ChatFragment extends Fragment
        implements ChatContractor.View {

    private RecyclerView recyclerView;
    private Button btnSend;
    private EditText etMessage;
    private View viewLoad;
    private ProgressBar progressBar;
    private Button btnCancel;
    private TextView loadingConnectionText;
    private View sendSeparator;
    private ImageView sendImage;

    private WifiP2pManager p2pManager;
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
    private String connectedDeviceName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        setupUIElements(view);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            int chatId = bundle.getInt("chatId", -1);
            presenter = new ChatPresenter(this, chatId);
        } else {
            presenter = new ChatPresenter(this, -1);
        }
        setTitle(getString(R.string.chat));
        setupRecycler();
        setupBtnSendOnClickAction();
        setupBtnCancelOnClickAction();
        presenter.start();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (presenter != null) {
            if (!presenter.isHistory()) {
                menu.clear();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (receiver != null) {
            getActivity().registerReceiver(receiver, intentFilter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getResources().getString(R.string.confirm))
                .setMessage(getString(R.string.confirmDeleteThisChat))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(getActivity().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        presenter.btnDeleteTapped();
                    }
                })
                .setNegativeButton(getActivity().getResources().getString(R.string.no), null).show();
        return true;
    }

    private void setupUIElements(View view) {
        progressBar = view.findViewById(R.id.progress_bar);
        viewLoad = view.findViewById(R.id.view_load);
        loadingConnectionText = view.findViewById(R.id.loading_connection_text);
        btnCancel = view.findViewById(R.id.button_cancel);
        recyclerView = view.findViewById(R.id.recycler_chat);
        etMessage = view.findViewById(R.id.et_message);
        btnSend = view.findViewById(R.id.btn_send);
        sendSeparator = view.findViewById(R.id.send_separator);
        sendImage = view.findViewById(R.id.send_button);

        etMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == R.id.et_message && !hasFocus) {

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                }
            }
        });
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

    private void setupBtnCancelOnClickAction() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.btnCancelTapped();
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
    public void sendMessage(final String text) {
        pipe.write(text.getBytes());
    }

    @Override
    public void hideLoader() {
        btnCancel.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        viewLoad.setVisibility(View.INVISIBLE);
        loadingConnectionText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void moveBack() {
        Fragment fragment = new HistoryFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.scene, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void setTitle(String title) {
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle(title);
    }

    @Override
    public void setSubtitle(String subtitle) {
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setSubtitle(subtitle);
    }

    @Override
    public void hideSendPanel() {
        etMessage.setVisibility(View.INVISIBLE);
        btnSend.setVisibility(View.INVISIBLE);
        sendSeparator.setVisibility(View.INVISIBLE);
        sendImage.setVisibility(View.INVISIBLE);
    }

    @Override
    public void searchForPeers() {
        initListeners();
        p2pManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = p2pManager.initialize(getActivity(), getActivity().getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(p2pManager, channel, peerListListener, connectionInfoListener);

        initIntentFilter();
        discoverPeers();

        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void showChatElements(String deviceName, Date time) {
        etMessage.setVisibility(View.VISIBLE);
        btnSend.setVisibility(View.VISIBLE);
        sendSeparator.setVisibility(View.VISIBLE);
        sendImage.setVisibility(View.VISIBLE);

        setTitle(deviceName);
        setSubtitle(Utils.SDF.format(time));
    }

    private void initListeners() {
        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                List<WifiP2pDevice> refreshedPeers = new ArrayList<>(peerList.getDeviceList());
                if (!refreshedPeers.equals(peers)) {
                    peers.clear();
                    peers.addAll(refreshedPeers);
                }

                if (!peers.isEmpty()) {
                    final WifiP2pDevice device = peers.get(0);
                    if (!device.deviceName.equals(connectedDeviceName)) {
                        WifiP2pConfig config = new WifiP2pConfig();
                        config.deviceAddress = device.deviceAddress;

                        p2pManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                connectedDeviceName = device.deviceName;
                                hideLoader();
                                presenter.createNewChat(device.deviceName);
                            }

                            @Override
                            public void onFailure(int reason) {

                            }
                        });
                    }
                }
            }
        };

        connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                final InetAddress groupOwner = info.groupOwnerAddress;
                if (info.groupFormed) {
                    if (info.isGroupOwner) {
                        server = new Server();
                        server.start();
                    } else {
                        client = new Client(groupOwner.getHostAddress());
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
                    presenter.messageReceived(text);
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
        p2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {
            }
        });
    }

    private class Server extends Thread {

        private Socket socket;
        private ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(ConnectionConstants.PORT);
                socket = serverSocket.accept();
                pipe = new Pipe(socket);
                pipe.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class Client extends Thread {

        private Socket socket;
        private String hostAddress;

        Client(String hostAddress) {
            this.hostAddress = hostAddress;
            this.socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAddress, ConnectionConstants.PORT), ConnectionConstants.TIMEOUT);
                pipe = new Pipe(socket);
                pipe.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class Pipe extends Thread {

        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        Pipe(Socket socket) {
            this.socket = socket;
            try {
                this.inputStream = socket.getInputStream();
                this.outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (socket != null) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        handler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
