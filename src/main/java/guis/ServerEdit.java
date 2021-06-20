package guis;

import myClasses.ListServer;
import myClasses.Server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ServerEdit extends JDialog {
    private JPanel mainPanel;
    private JLabel servernameLabel;
    private JLabel portLabel;
    private JTextField portTextField;
    private JButton editservernameButton;
    private JButton editportButton;
    private JTextField editservernameTF;
    private JButton saveservernameButton;
    private JTextField editportTF;
    private JButton saveportButton;
    private JTextField servernameTextField;
    private JPanel ahihiPanel;

    public ServerEdit(myClasses.Server server, Manager.serverTableModel serverModel, int modelRow) {
        this.setTitle("Server edit");
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setSize(500,300);

        servernameTextField.setText(server.getServer_name());
        portTextField.setText(String.valueOf(server.getPort()));
        servernameTextField.setEditable(false);
        portTextField.setEditable(false);

        saveservernameButton.setVisible(false);
        editservernameTF.setVisible(false);
        saveportButton.setVisible(false);
        editportTF.setVisible(false);

        editservernameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editservernameTF.isVisible()) {
                    editservernameTF.setVisible(false);
                    saveservernameButton.setVisible(false);
                } else {
                    editservernameTF.setVisible(true);
                    saveservernameButton.setVisible(true);
                }
            }
        });
        editportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (editportTF.isVisible()) {
                    editportTF.setVisible(false);
                    saveportButton.setVisible(false);
                } else {
                    editportTF.setVisible(true);
                    saveportButton.setVisible(true);
                }
            }
        });
        saveservernameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String server_name = editservernameTF.getText();
                if (server_name.length() <= 0) {
                    JOptionPane.showMessageDialog(getDialog(), "Enter new server name");
                    return;
                }
                List<myClasses.Server> servers = serverModel.getServers();
                for (myClasses.Server server : servers) {
                    if (server_name.equals(server.getServer_name())) {
                        JOptionPane.showMessageDialog(getDialog(), "Server name is not available");
                        return;
                    }
                }
                servers.get(modelRow).setServer_name(server_name);
                serverModel.setServers(servers);
                serverModel.changeData();
                servernameTextField.setText(server_name);
            }
        });
        saveportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int port = 0;
                try {
                    port = Integer.parseInt(editportTF.getText());
                } catch (NumberFormatException numberFormatException) {
                    numberFormatException.printStackTrace();
                    JOptionPane.showMessageDialog(getDialog(), "Enter valid port number");
                    return;
                }
                if (port == 0) {
                    JOptionPane.showMessageDialog(getDialog(), "Enter port number");
                    return;
                }
                List<myClasses.Server> servers = serverModel.getServers();
                for (myClasses.Server server : servers) {
                    if (port == server.getPort()) {
                        JOptionPane.showMessageDialog(getDialog(), "Port is already used");
                        return;
                    }
                }
                servers.get(modelRow).setPort(port);
                serverModel.setServers(servers);
                serverModel.changeData();
                portTextField.setText(String.valueOf(port));
            }
        });
    }

    public JDialog getDialog() {
        return this;
    }
}
