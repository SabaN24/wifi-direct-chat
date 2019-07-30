package scenes.chat.core;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import common.Utils;
import p2p.ConnectionConstants;
import p2p.WiFiDirectBroadcastReceiver;
import scenes.chat.model.MessageModel;
import scenes.history.core.HistoryFragment;

public class ChatActivity extends AppCompatActivity implements ChatContractor.View {

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
    private ChatActivity.Server server;
    private ChatActivity.Client client;
    private ChatActivity.Pipe pipe;
    private String connectedDeviceName;
    private List<Thread> threads = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setupUIElements();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int chatId = bundle.getInt("chatId", -1);
            presenter = new ChatPresenter(this, chatId);
        } else {
            presenter = new ChatPresenter(this, -1);
        }
        setupRecycler();
        setupBtnSendOnClickAction();
        setupBtnCancelOnClickAction();
        showBackButton();
        setTitle(getString(R.string.chat));
        presenter.start();
    }

    private void showBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (receiver != null) {
            registerReceiver(receiver, intentFilter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (p2pManager != null && channel != null) {
            p2pManager.removeGroup(channel, null);
        }
        try {
            for (Thread t : threads) {
                if (t instanceof ChatActivity.Server) {
                    ((ChatActivity.Server) t).getServerSocket().close();
                    ((ChatActivity.Server) t).getSocket().close();
                } else if (t instanceof ChatActivity.Client) {
                    ((ChatActivity.Client) t).getSocket().close();
                }
                t.stop();
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.confirm))
                    .setMessage(getString(R.string.confirmDeleteThisChat))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            presenter.btnDeleteTapped();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), null).show();
        }
        return true;
    }

    private void setupUIElements() {
        progressBar = findViewById(R.id.progress_bar);
        viewLoad = findViewById(R.id.view_load);
        loadingConnectionText = findViewById(R.id.loading_connection_text);
        btnCancel = findViewById(R.id.button_cancel);
        recyclerView = findViewById(R.id.recycler_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        sendSeparator = findViewById(R.id.send_separator);
        sendImage = findViewById(R.id.send_button);

        etMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == R.id.et_message && !hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                }
            }
        });
    }

    private void setupRecycler() {
        adapter = new ChatAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        if (pipe != null) {
            pipe.write(text.getBytes());
        }
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
        onBackPressed();
    }

    @Override
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void setSubtitle(String subtitle) {
        getSupportActionBar().setSubtitle(subtitle);
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
        p2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = p2pManager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(p2pManager, channel, peerListListener, connectionInfoListener);

        initIntentFilter();
        discoverPeers();

        registerReceiver(receiver, intentFilter);
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
                                setTitle(String.format(getString(R.string.connectingTo), connectedDeviceName));
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
                        server = new ChatActivity.Server();
                        server.start();
                        threads.add(server);
                    } else {
                        client = new ChatActivity.Client(groupOwner.getHostAddress());
                        client.start();
                        threads.add(client);
                    }
                    hideLoader();
                    presenter.createNewChat(connectedDeviceName);
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

        Socket getSocket() {
            return socket;
        }

        ServerSocket getServerSocket() {
            return serverSocket;
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(ConnectionConstants.PORT);
                socket = serverSocket.accept();
                pipe = new ChatActivity.Pipe(socket);
                threads.add(pipe);
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

        Socket getSocket() {
            return socket;
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAddress, ConnectionConstants.PORT), ConnectionConstants.TIMEOUT);
                pipe = new ChatActivity.Pipe(socket);
                threads.add(pipe);
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

            while (socket != null && !socket.isClosed()) {
                try {
                    bytes = inputStream.read(buffer);
                    if (bytes > 0) {
                        handler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Thread.currentThread().interrupt();
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
