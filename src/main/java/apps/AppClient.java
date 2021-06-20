package apps;

import guis.Client;
import guis.ClientLogin;
import guis.ServerSelector;
import threads.ClientInputHandleThread;
import threads.ServerInputHandleThread;

import java.io.IOException;
import java.net.Socket;

public class AppClient {
    private String server_name;
    private int port;
    private String myUsername;

    private ClientInputHandleThread clientInputHandleThread;
    private ServerInputHandleThread serverInputHandleThread;

    public AppClient(String server_name, int port, String myUsername) {
        this.server_name = server_name;
        this.port = port;
        this.myUsername = myUsername;
    }

    public void run() {
        Client frame = new Client(myUsername, server_name, port);
        frame.setVisible(true);
        try {
            Socket socket = new Socket("localhost", port);
            System.out.println("Currently connecting to " + server_name);
            frame.setSocket(socket);

            clientInputHandleThread = new ClientInputHandleThread(this, socket, frame);
            serverInputHandleThread = new ServerInputHandleThread(this, socket, frame);
            clientInputHandleThread.start();
            serverInputHandleThread.start();
            frame.setClientThread(clientInputHandleThread);
            frame.setServerThread(serverInputHandleThread);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            frame.showDialog("Server is currently offline! Please check back later!");
            frame.dispose();
            ServerSelector serverSelector = new ServerSelector(myUsername);
            serverSelector.setVisible(true);
            clientInputHandleThread.stopThread();
            serverInputHandleThread.stopThread();
        }
    }

    public String getMyUsername() {
        return myUsername;
    }

    public static void main(String[] args) {
        ClientLogin frame = new ClientLogin();
        frame.setVisible(true);
    }
}
