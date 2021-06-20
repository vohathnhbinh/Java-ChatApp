package guis;

import apps.AppServer;
import myClasses.ListServer;
import myClasses.Server;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class Manager extends JFrame {
    private JPanel mainPanel;
    private JTable serverTable;
    private JScrollPane serverScroll;
    private JTextField servernameTextField;
    private JLabel servernameLabel;
    private JLabel portLabel;
    private JTextField portTextField;
    private JButton addButton;
    private serverTableModel serverModel;
    private List<AppServer> listApp = new ArrayList<>();

    public Manager() {
        this.setTitle("Server manager");
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setSize(800,600);

        serverScroll.setViewportView(serverTable);
        serverModel = new serverTableModel();
        serverTable.setModel(serverModel);
        serverTable.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        serverTable.getTableHeader().setFont(new Font(Font.SERIF, Font.BOLD, 20));

        ButtonColumn buttonColumn1 = new ButtonColumn(serverTable, turnOnOffServer, 2);
        ButtonColumn buttonColumn2 = new ButtonColumn(serverTable, editServer, 3);
        ButtonColumn buttonColumn3 = new ButtonColumn(serverTable, deleteServer, 4);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String server_name = servernameTextField.getText();
                int port = 0;
                try {
                    port = Integer.parseInt(portTextField.getText());
                } catch (NumberFormatException numberFormatException) {
                    numberFormatException.printStackTrace();
                    JOptionPane.showMessageDialog(getFrame(), "Enter a valid port number");
                    return;
                }
                if (server_name == null || port == 0) {
                    JOptionPane.showMessageDialog(getFrame(), "Enter server name and port");
                    return;
                }

                List<myClasses.Server> servers = serverModel.getServers();
                for (myClasses.Server server : servers) {
                    if (server_name.equals(server.getServer_name()) || port == server.getPort()) {
                        JOptionPane.showMessageDialog(getFrame(), "Server name or port already exists");
                        return;
                    }
                }
                myClasses.Server server = new myClasses.Server(server_name, port);
                serverModel.add(server);
                serverModel.changeData();
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                List<myClasses.Server> servers = serverModel.getServers();
                for (myClasses.Server server : servers) {
                    if (server.isOnline()) {
                        JOptionPane.showMessageDialog(getFrame(), "Please shut down all servers before closing the app!");
                        return;
                    }
                }
                getFrame().dispose();
            }
        });
    }

    public JFrame getFrame() {
        return this;
    }

    Action deleteServer = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int modelRow = Integer.valueOf(e.getActionCommand());
            List<myClasses.Server> servers = serverModel.getServers();
            myClasses.Server server = servers.get(modelRow);
            if (server.isOnline()) {
                JOptionPane.showMessageDialog(getFrame(), "Please shut down server before deleting!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(getFrame(), "Are you sure you want to delete this server?", "Delete confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == 0)
                serverModel.remove(server);
        }
    };

    Action turnOnOffServer = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int modelRow = Integer.valueOf(e.getActionCommand());
            List<myClasses.Server> servers = serverModel.getServers();
            myClasses.Server server = servers.get(modelRow);

            // Turn off
            if (server.isOnline()) {
                int confirm = JOptionPane.showConfirmDialog(getFrame(), "Are you sure you want to shut down this server?", "Shutdown confirmation", JOptionPane.YES_NO_OPTION);
                if (confirm == 0) {
                    servers.get(modelRow).setOnline(false);
                    serverModel.setServers(servers);
                    for (AppServer appServer : listApp) {
                        if (server.getServer_name().equals(appServer.getServer_name())) {
                            appServer.shutDown();
                            listApp.remove(appServer);
                            break;
                        }
                    }
                    serverModel.changeData();
                }
            } else {
                servers.get(modelRow).setOnline(true);
                serverModel.setServers(servers);
                AppServer appServer = new AppServer(server.getServer_name(), server.getPort());
                appServer.start();
                listApp.add(appServer);
                serverModel.changeData();
            }
        }
    };

    Action editServer = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int modelRow = Integer.valueOf(e.getActionCommand());
            List<myClasses.Server> servers = serverModel.getServers();
            myClasses.Server server = servers.get(modelRow);
            if (server.isOnline()) {
                JOptionPane.showMessageDialog(getFrame(), "Please shut down server before deleting!");
                return;
            }

            ServerEdit serverEdit = new ServerEdit(server, serverModel, modelRow);
            serverEdit.setVisible(true);
        }
    };

    class serverTableModel extends AbstractTableModel {
        private ListServer listServer = new ListServer();

        public List<myClasses.Server> getServers() {
            return listServer.list;
        }

        public void setServers(List<myClasses.Server> servers) {
            listServer.list = servers;
            listServer.saveList();
        }

        @Override
        public int getRowCount() {
            return listServer.list.size();
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case 0: return "Server name";
                case 1: return "Port";
                case 2:
                case 3:
                case 4: return "";
            }
            return null;
        }


        @Override
        public Object getValueAt(int row, int col) {
            myClasses.Server server = listServer.list.get(row);
            switch (col) {
                case 0: return server.getServer_name();
                case 1: return server.getPort();
                case 2: return server.isOnline() ? "SHUT DOWN" : "TURN ON";
                case 3: return "EDIT";
                case 4: return "DELETE";
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            switch (col) {
                case 0:
                case 1:
                    return false;
                case 2:
                case 3:
                case 4:
                    return true;
            }
            return false;
        }

        public void changeData() {
            fireTableDataChanged();
        }

        public void add(myClasses.Server server) {
            listServer.list.add(server);
        }

        public void remove(myClasses.Server server) {
            if (listServer.list.contains(server)) {
                int row = listServer.list.indexOf(server);
                listServer.list.remove(row);
                fireTableRowsDeleted(row, row);
                listServer.saveList();
            }
        }
    }
}
