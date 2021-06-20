package guis;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Set;

public class Server extends JFrame {
    private JPanel mainPanel;
    private JLabel serverLabel;
    private JTextPane serverTextPane;
    private JScrollPane serverScroll;
    private JScrollPane usersScroll;
    private JTable usersTable;
    private Set<String> currentUsers = null;

    private ServerSocket serverSocket;

    public Server(String server_name, int port) {
        this.setTitle(server_name);
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setSize(600,400);

        serverLabel.setText("Server " + server_name + " is running on port " + port);
        serverScroll.setViewportView(serverTextPane);
        DefaultCaret caret = (DefaultCaret) serverTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        usersScroll.setViewportView(usersTable);

        DefaultTableModel usersModel = new DefaultTableModel();
        usersModel.addColumn("User");
        usersTable.setModel(usersModel);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
//                int confirm = JOptionPane.showConfirmDialog(getFrame(), "Are you sure you want to turn off the server", "Shut down confirmation", JOptionPane.YES_NO_OPTION);
//                if (confirm == 0) {
//                    try {
//                        serverSocket.close();
//                    } catch (IOException ioException) {
//                        ioException.printStackTrace();
//                    }
//                    getFrame().dispose();
//                }
                JOptionPane.showMessageDialog(getFrame(), "Please use the manager to shut down this server!");
            }
        });
    }

    public JFrame getFrame() {
        return this;
    }

    public void updateInfo(String info) throws BadLocationException{
        StyledDocument document = (StyledDocument) serverTextPane.getStyledDocument();
        document.insertString(document.getLength(), info, null);
    }

    public void updateLabel(String label) {
        serverLabel.setText(label);
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void setCurrentUsers(Set<String> currentUsers) {
        this.currentUsers = currentUsers;
        DefaultTableModel usersModel = (DefaultTableModel) usersTable.getModel();
        usersModel.setRowCount(0);
        for (String currentUser : currentUsers) {
            usersModel.addRow(new Object[] {currentUser});
        }
    }

    public void showDialog() {
        JOptionPane.showMessageDialog(null, "Server or port has already been opened! Please choose another server!!");
    }
}
