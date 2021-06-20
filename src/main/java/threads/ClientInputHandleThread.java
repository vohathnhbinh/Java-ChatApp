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
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClientInputHandleThread extends Thread {
    private DataOutputStream output;
    private AppClient appClient;
    private Socket socket;
    private Client frame;
    private volatile boolean isRunning = true;

    public void stopThread() {
        isRunning = false;
    }

    public ClientInputHandleThread(AppClient appClient, Socket socket, Client frame) {
        this.appClient = appClient;
        this.socket = socket;
        this.frame = frame;

        try {
            output = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] data = appClient.getMyUsername().getBytes(StandardCharsets.UTF_8);
        try {
            output.writeInt(data.length);
            output.write(data);
            output.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        frame.setOutput(output);
        while(isRunning) {
            try {
                frame.waitForInput();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ImageIcon emoji = frame.getEmoji();
            File theirFile = frame.getTheirFile();
            if (emoji != null) {
                byte[] anotherData = "##SENDING EMOJI##".getBytes(StandardCharsets.UTF_8);
                try {
                    output.writeInt(anotherData.length);
                    output.write(anotherData);
                    output.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                BufferedImage bufferedImage = new BufferedImage(
                        emoji.getIconWidth(),
                        emoji.getIconHeight(),
                        BufferedImage.TYPE_INT_ARGB
                );
                Graphics graphics = bufferedImage.createGraphics();
                emoji.paintIcon(null, graphics, 0, 0);
                graphics.dispose();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                    byte[] emojiData = byteArrayOutputStream.toByteArray();
                    output.writeInt(emojiData.length);
                    output.write(emojiData);
                    output.flush();
                    byteArrayOutputStream.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                frame.setEmoji(null);
                continue;
            } else if (theirFile != null) {
                byte[] anotherData = "##SENDING FILE##".getBytes(StandardCharsets.UTF_8);
                try {
                    output.writeInt(anotherData.length);
                    output.write(anotherData);
                    output.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                byte[] nameData = theirFile.getName().getBytes(StandardCharsets.UTF_8);
                try {
                    output.writeInt(nameData.length);
                    output.write(nameData);
                    output.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

                try {
                    byte[] fileData = Files.readAllBytes(Path.of(theirFile.getAbsolutePath()));
                    output.writeInt(fileData.length);
                    output.write(fileData);
                    output.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                frame.setTheirFile(null);
                continue;
            }
            if (frame.getChat() == null)
                break;
            byte[] anotherData = frame.getChat().getBytes(StandardCharsets.UTF_8);
            try {
                output.writeInt(anotherData.length);
                output.write(anotherData);
                output.flush();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            frame.emptyTextArea();
        }

        try {
            socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
