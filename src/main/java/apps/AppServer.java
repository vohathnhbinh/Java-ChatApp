package apps;

import guis.Server;
import threads.ClientHandleThread;

import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

public class AppServer extends Thread
{
    private String server_name;
    private int port;
    private Set<String> currentUsers = new HashSet<>();
    private Set<ClientHandleThread> clientHandleThreads = new HashSet<>();
    private volatile Thread isRunning;
    private Server frame;
    private ServerSocket serverSocket;

    public AppServer(String server_name, int port) {
        this.server_name = server_name;
        this.port = port;
    }

    public void stopThread() {
        isRunning = null;
    }

    @Override
    public void run() {
        frame = new Server(server_name, port);
        frame.setVisible(true);
        try {
//            Thread thisThread = Thread.currentThread();
//            isRunning = thisThread;
            serverSocket = new ServerSocket(port);
            frame.setServerSocket(serverSocket);
            System.out.println(server_name + " start on port " + port);

            frame.updateInfo(server_name + " start on port " + port);

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("A user just joined. Say hi to them!");
                    frame.updateInfo("\nA user just joined. Say hi to them!");

                    ClientHandleThread clientHandleThread = new ClientHandleThread(socket, this, frame);
                    clientHandleThreads.add(clientHandleThread);
                    clientHandleThread.start();
                } catch (SocketException socketException) {
                    socketException.printStackTrace();
                    for (ClientHandleThread clientHandleThread : clientHandleThreads)
                        clientHandleThread.stopThread();
                    break;
                }
            }
        } catch (IOException | BadLocationException ex) {
            ex.printStackTrace();
            frame.updateLabel("Server or port has already been opened! Please choose another server!!");
            frame.showDialog();
            frame.dispose();
        }
    }

    public void addUser(String username) {
        currentUsers.add(username);
    }

    public Set<String> getCurrentUsers() {
        return currentUsers;
    }

    public void removeUser(String username, ClientHandleThread clientHandleThread) {
        currentUsers.remove(username);
        clientHandleThreads.remove(clientHandleThread);
        System.out.println(username + " is disconnected");
        try {
            frame.updateInfo("\n" + username + " is disconnected");
        } catch(BadLocationException badLocationException) {
            badLocationException.printStackTrace();
        }
    }

    public void sendAll(String message) {
        try {
            for (ClientHandleThread clientHandleThread : clientHandleThreads) {
                clientHandleThread.send(message);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void sendAll(byte[] data) {
        try {
            for (ClientHandleThread clientHandleThread : clientHandleThreads) {
                clientHandleThread.send(data);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void sendAll() {
        try {
            for (ClientHandleThread clientHandleThread : clientHandleThreads) {
                clientHandleThread.send(currentUsers);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void shutDown() {
        try {
            serverSocket.close();
            frame.dispose();
            for (ClientHandleThread clientHandleThread : clientHandleThreads)
                clientHandleThread.stopThread();
        } catch(IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public String getServer_name() {
        return server_name;
    }
}
