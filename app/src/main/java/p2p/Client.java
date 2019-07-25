package p2p;

import android.os.Handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client extends Thread {

    private Socket socket;
    private String hostAddress;
    private Pipe pipe;
    private Handler handler;

    public Client(String hostAddress, Pipe pipe, Handler handler) {
        this.socket = new Socket();
        this.hostAddress = hostAddress;
        this.pipe = pipe;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            socket.connect(new InetSocketAddress(hostAddress, ConnectionConstants.PORT), ConnectionConstants.TIMEOUT);
            pipe = new Pipe(socket, handler);
            pipe.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
