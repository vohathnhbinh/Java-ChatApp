package guis;

import myClasses.UltimateFile;
import threads.ClientInputHandleThread;
import threads.ServerInputHandleThread;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class Client extends JFrame {
    private JPanel mainPanel;
    private JLabel clientLabel;
    private JScrollPane clientScroll;
    private JTextPane clientTextPane;
    private JTextArea chatTextArea;
    private JButton SENDButton;
    private JButton emoji1;
    private JButton emoji2;
    private JButton emoji3;
    private JButton emoji4;
    private JButton emoji5;
    private JLabel serverLabel;
    private JRadioButton newlineRadioButton;
    private JRadioButton chatRadioButton;
    private JButton setButton;
    private JButton FILEButton;
    private JScrollPane usersScroll;
    private JTable usersTable;
    private JLabel usersLabel;
    private JButton QUITButton;
    private Socket socket;
    private String chat;
    private DataOutputStream output;
    private ImageIcon emoji;
    private boolean isEnterChat = false;
    private JFileChooser fileChooser;
    private File theirFile;
    private Set<UltimateFile> fileList = new HashSet<>();
    private Set<String> currentUsers = null;

    private ClientInputHandleThread clientInputHandleThread;
    private ServerInputHandleThread serverInputHandleThread;

    public void setClientThread(ClientInputHandleThread clientInputHandleThread) {
        this.clientInputHandleThread = clientInputHandleThread;
    }

    public void setServerThread(ServerInputHandleThread serverInputHandleThread) {
        this.serverInputHandleThread = serverInputHandleThread;
    }

    public void setOutput(DataOutputStream output) {
        this.output = output;
    }

    public Client(String username, String server_name, int port) {
        this.setTitle(username);
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setSize(1000,720);

        serverLabel.setText("Server " + server_name + " port " + port);

//        emoji1.setText("\uD83D\uDE21");
//        emoji2.setText("\uD83D\uDE2D");
//        emoji3.setText("\uD83D\uDE03");
//        emoji4.setText("\uD83D\uDE32");
//        emoji5.setText("\uD83D\uDE37");
        try {
            Image image1 = ImageIO.read(getClass().getResource("/slightly-smiling.png"));
            Image image1Resized = image1.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            emoji1.setIcon(new ImageIcon(image1Resized));
            emoji1.setText("");

            Image image2 = ImageIO.read(getClass().getResource("/pepeclown.png"));
            Image image2Resized = image2.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            emoji2.setIcon(new ImageIcon(image2Resized));
            emoji2.setText("");

            Image image3 = ImageIO.read(getClass().getResource("/pepehype.png"));
            Image image3Resized = image3.getScaledInstance(53, 50, Image.SCALE_SMOOTH);
            emoji3.setIcon(new ImageIcon(image3Resized));
            emoji3.setText("");

            Image image4 = ImageIO.read(getClass().getResource("/gesugao.png"));
            Image image4Resized = image4.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            emoji4.setIcon(new ImageIcon(image4Resized));
            emoji4.setText("");

            Image image5 = ImageIO.read(getClass().getResource("/uwu.png"));
            Image image5Resized = image5.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            emoji5.setIcon(new ImageIcon(image5Resized));
            emoji5.setText("");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        clientLabel.setText("Welcome " + username);
        clientScroll.setViewportView(clientTextPane);
        DefaultCaret caret = (DefaultCaret) clientTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        usersScroll.setViewportView(usersTable);

        DefaultTableModel usersModel = new DefaultTableModel();
        usersModel.addColumn("User");
        usersTable.setModel(usersModel);
        usersTable.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        usersTable.getTableHeader().setFont(new Font(Font.SERIF, Font.BOLD, 20));

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                int confirm = JOptionPane.showConfirmDialog(getFrame(), "Are you sure you want to quit", "Exit confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == 0) {
                    clientInputHandleThread.stopThread();
                    serverInputHandleThread.stopThread();
                    getFrame().dispose();

                    synchronized (getFrame()) {
                        getFrame().notifyAll();
                    }
                }
            }
        });
        SENDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chat = chatTextArea.getText();
                if (chat == null || chat.equals("")) {
                    JOptionPane.showMessageDialog(getFrame(), "Please type something before hitting the send button!");
                    return;
                }
                synchronized (getFrame()) {
                    getFrame().notifyAll();
                }
            }
        });
        emoji1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emoji = (ImageIcon) emoji1.getIcon();
                synchronized (getFrame()) {
                    getFrame().notifyAll();
                }
            }
        });
        emoji2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emoji = (ImageIcon) emoji2.getIcon();
                synchronized (getFrame()) {
                    getFrame().notifyAll();
                }
            }
        });
        emoji3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emoji = (ImageIcon) emoji3.getIcon();
                synchronized (getFrame()) {
                    getFrame().notifyAll();
                }
            }
        });
        emoji4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emoji = (ImageIcon) emoji4.getIcon();
                synchronized (getFrame()) {
                    getFrame().notifyAll();
                }
            }
        });
        emoji5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                emoji = (ImageIcon) emoji5.getIcon();
                synchronized (getFrame()) {
                    getFrame().notifyAll();
                }
            }
        });
        setButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chatRadioButton.isSelected())
                    isEnterChat = true;
                else if (newlineRadioButton.isSelected())
                    isEnterChat = false;
            }
        });
        chatTextArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (isEnterChat) {
                        e.consume();
                        SENDButton.doClick();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        FILEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                int fileValue = fileChooser.showOpenDialog(mainPanel);
                if (fileValue == JFileChooser.APPROVE_OPTION) {
                    theirFile = fileChooser.getSelectedFile();
                    synchronized (getFrame()) {
                        getFrame().notifyAll();
                    }
                }
            }
        });
        QUITButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(getFrame(), "Are you sure you want to exit to server selector", "Exit confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == 0) {
                    clientInputHandleThread.stopThread();
                    serverInputHandleThread.stopThread();
                    getFrame().dispose();

                    synchronized (getFrame()) {
                        getFrame().notifyAll();
                    }

                    ServerSelector serverSelector = new ServerSelector(username);
                    serverSelector.setVisible(true);
                }
            }
        });
    }

    public JFrame getFrame() {
        return this;
    }

    public void updateChat(String chat) throws BadLocationException {
        StyledDocument document = clientTextPane.getStyledDocument();
        document.insertString(document.getLength(), chat, null);
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void waitForInput() throws InterruptedException {
        synchronized (this) {
            wait();
        }
    }

    public String getChat() {
        return chat;
    }

    public void emptyTextArea() {
        chatTextArea.setText("");
    }

    public void insertEmoji(BufferedImage bufferedImage) throws BadLocationException {
        Image newImage = bufferedImage.getScaledInstance(
                bufferedImage.getWidth() - 10,
                bufferedImage.getHeight() - 10,
                Image.SCALE_SMOOTH
        );
        ImageIcon icon = new ImageIcon(newImage);
        StyledDocument document = clientTextPane.getStyledDocument();
        Style style = document.addStyle("Stylename", null);
        StyleConstants.setIcon(style, icon);
        document.insertString(document.getLength(), "ignored text", style);
    }

    public void awake() {
        synchronized (this) {
            notifyAll();
        }
    }

    public void setEmoji(ImageIcon emoji) {
        this.emoji = emoji;
    }

    public ImageIcon getEmoji() {
        return emoji;
    }

    public void setServerLabel(String str) {
        serverLabel.setText(str);
    }

    public File getTheirFile() {
        return theirFile;
    }

    public void setTheirFile(File theirFile) {
        this.theirFile = theirFile;
    }

    public MouseListener clickToDownloadFile() {
       return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel fileLabel = (JLabel) e.getSource();
                int file_id = Integer.parseInt(fileLabel.getName());
                System.out.println(file_id);
                for (UltimateFile thisFile : fileList) {
                    if (file_id == thisFile.getFile_id()) {
                        JFileChooser chooseLocation = new JFileChooser();
                        System.out.println(thisFile.getFile_name());
                        chooseLocation.setSelectedFile(new File(thisFile.getFile_name()));
                        int fileValue = chooseLocation.showSaveDialog(mainPanel);
                        if (fileValue == JFileChooser.APPROVE_OPTION) {
                            try {
                                String pathName = chooseLocation.getSelectedFile().getAbsolutePath();
                                Files.write(Path.of(pathName), thisFile.getFile_data());
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
    }

    public void insertFile(String fileName, byte[] fileData) {
        JLabel fileLabel = new JLabel();
        int file_id = fileList.size();
        fileLabel.setName(String.valueOf(file_id));
        fileLabel.setText(fileName);
        fileLabel.addMouseListener(clickToDownloadFile());
        fileLabel.setAlignmentY(0.9f);
        fileLabel.setForeground(Color.BLUE);
        UltimateFile ultimateFile = new UltimateFile(file_id, fileName, fileData);
        fileList.add(ultimateFile);
        clientTextPane.insertComponent(fileLabel);
    }

    public void setCurrentUsers(Set<String> currentUsers) {
        this.currentUsers = currentUsers;
        DefaultTableModel usersModel = (DefaultTableModel) usersTable.getModel();
        usersModel.setRowCount(0);
        for (String currentUser : currentUsers) {
            usersModel.addRow(new Object[] {currentUser});
        }
    }

    public void showDialog(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }
}
