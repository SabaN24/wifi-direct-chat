package p2p;

import android.os.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {

    private Pipe pipe;
    private Socket socket;
    private ServerSocket serverSocket;
    private Handler handler;

    public Server(Pipe pipe, Handler handler) {
        this.pipe = pipe;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(ConnectionConstants.PORT);
            socket = serverSocket.accept();
            pipe = new Pipe(socket, handler);
            pipe.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
