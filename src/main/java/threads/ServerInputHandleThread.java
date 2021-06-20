/* Dựa trên https://www.codejava.net
14-06-2021
Tham khảo ý tưởng sử dụng 1 thread xử lí từng client trong server
và 2 thread cho client xử lí read, write
Tổng phần trăm tham khảo: 20%
 */

package threads;

import apps.AppClient;
import guis.Client;

import javax.imageio.ImageIO;
import javax.swing.text.BadLocationException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class ServerInputHandleThread extends Thread {
    private DataInputStream input;
    private AppClient appClient;
    private Socket socket;
    private Client frame;
    private volatile boolean isRunning = true;

    public void stopThread() {
        isRunning = false;
    }

    public ServerInputHandleThread(AppClient appClient, Socket socket, Client frame) {
        this.appClient = appClient;
        this.socket = socket;
        this.frame = frame;

        try {
            input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void run() {
//        Thread thisThread = Thread.currentThread();
        while(isRunning) {
            try {
                int length = input.readInt();
                byte[] data = new byte[length];
                input.readFully(data);
                String res = new String(data, StandardCharsets.UTF_8);

                if (res.equals("##SENDING EMOJI##")) {
                    length = input.readInt();
                    byte[] emojiData = new byte[length];
                    input.readFully(emojiData);
                    InputStream inputStream = new ByteArrayInputStream(emojiData);
                    BufferedImage bufferedImage = ImageIO.read(inputStream);
                    frame.insertEmoji(bufferedImage);
                    inputStream.close();
                    continue;
                } else if (res.equals("##SENDING FILE##")) {
                    length = input.readInt();
                    byte[] nameData = new byte[length];
                    input.readFully(nameData);
                    String fileName = new String(nameData, StandardCharsets.UTF_8);

                    length = input.readInt();
                    byte[] fileData = new byte[length];
                    input.readFully(fileData);

                    frame.insertFile(fileName, fileData);
                    continue;
                } else if (res.equals("##SENDING USERS##")) {
                    length = input.readInt();
                    byte[] userData = new byte[length];
                    input.readFully(userData);

                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(userData);
                    ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                    Set<String> currentUsers = null;
                    currentUsers = (Set<String>) objectInputStream.readObject();
                    objectInputStream.close();

                    frame.setCurrentUsers(currentUsers);
                    continue;
                }

                frame.updateChat("\n" + res);
            } catch (BadLocationException | IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
                frame.setServerLabel("Connection is terminated");
                frame.showDialog("Connection is terminated");
                break;
            }
        }
    }
}
