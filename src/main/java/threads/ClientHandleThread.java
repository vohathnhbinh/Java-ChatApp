/* Dựa trên https://www.codejava.net
14-06-2021
Tham khảo ý tưởng sử dụng 1 thread xử lí từng client trong server
và 2 thread cho client xử lí read, write
Tổng phần trăm tham khảo: 20%
 */

package threads;

import apps.AppServer;
import guis.Server;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class ClientHandleThread extends Thread {
    private Socket socket;
    private DataOutputStream output;
    private AppServer appServer;
    private volatile boolean isRunning = true;
    private Server frame;

    public void stopThread() {isRunning = false;}

    public ClientHandleThread(Socket socket, AppServer appServer, Server frame) {
        this.socket = socket;
        this.appServer = appServer;
        this.frame = frame;
    }

    @Override
    public void run() {
        try {
            DataInputStream input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            int length = input.readInt();
            byte[] data = new byte[length];
            input.readFully(data);
            String username = new String(data, StandardCharsets.UTF_8);
            appServer.addUser(username);
            appServer.sendAll(username + " has joined. Say hi to them!");
            appServer.sendAll(); // Send new list of users
            frame.setCurrentUsers(appServer.getCurrentUsers());

            while (isRunning) {
                try {
                    length = input.readInt();
                } catch (EOFException eofException) {
                    eofException.printStackTrace();
                    break;
                }
                if (length <= 0)
                    break;
                byte[] anotherData = new byte[length];
                input.readFully(anotherData);
                String theirMessage = new String(anotherData, StandardCharsets.UTF_8);

                if (theirMessage.equals("##SENDING EMOJI##")) {
                    length = input.readInt();
                    byte[] emojiData = new byte[length];
                    input.readFully(emojiData);
                    appServer.sendAll("<" + username + "> ");
                    appServer.sendAll("##SENDING EMOJI##");
                    appServer.sendAll(emojiData);
                    continue;
                } else if (theirMessage.equals("##SENDING FILE##")) {
                    length = input.readInt();
                    byte[] nameData = new byte[length];
                    input.readFully(nameData);

                    length = input.readInt();
                    byte[] fileData = new byte[length];
                    input.readFully(fileData);

                    appServer.sendAll("<" + username + "> ");
                    appServer.sendAll("##SENDING FILE##");
                    appServer.sendAll(nameData);
                    appServer.sendAll(fileData);
                    continue;
                }

                appServer.sendAll("<" + username + "> " + theirMessage);
            }

            appServer.removeUser(username, this);
            socket.close();
            appServer.sendAll(username + " is disconnected");
            appServer.sendAll(); // Send new list of users
            frame.setCurrentUsers(appServer.getCurrentUsers());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void send(String message) throws IOException {
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        output.writeInt(data.length);
        output.write(data);
        output.flush();
    }

    public void send(Set<String> currentUsers) throws IOException {
        byte[] signal = "##SENDING USERS##".getBytes(StandardCharsets.UTF_8);
        output.writeInt(signal.length);
        output.write(signal);
        output.flush();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(currentUsers);
        objectOutputStream.close();
        byte[] data = byteArrayOutputStream.toByteArray();
        output.writeInt(data.length);
        output.write(data);
        output.flush();
    }

    public void send(byte[] data) throws IOException {
        output.writeInt(data.length);
        output.write(data);
        output.flush();
    }
}
