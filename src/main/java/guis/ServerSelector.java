package guis;

import apps.AppClient;
import myClasses.ListServer;
import myClasses.Server;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class ServerSelector extends JFrame {

    private JPanel mainPanel;
    private JScrollPane serverScroll;
    private JTable serverTable;
    private JLabel welcomeLabel;
    private JButton refreshServerButton;
    private serverTableModel serverModel;
    private String username;

    public ServerSelector(String username) {
        this.setTitle("Server selector");
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(700,500);
        this.username = username;

        welcomeLabel.setText("Welcome " + username);
        serverScroll.setViewportView(serverTable);
        serverModel = new serverTableModel();
        serverTable.setModel(serverModel);
        serverTable.setFont(new Font(Font.SERIF, Font.PLAIN, 18));
        serverTable.getTableHeader().setFont(new Font(Font.SERIF, Font.BOLD, 20));
        ButtonColumn buttonColumn = new ButtonColumn(serverTable, joinServer, 3);

        try {
            Image image1 = ImageIO.read(getClass().getResource("/green-dot.png"));
            ImageIcon greenDot = new ImageIcon(image1.getScaledInstance(10, 10, Image.SCALE_SMOOTH));
            Image image2 = ImageIO.read(getClass().getResource("/red-dot.png"));
            ImageIcon redDot = new ImageIcon(image2.getScaledInstance(10, 10, Image.SCALE_SMOOTH));
            serverModel.setGreenDot(greenDot);
            serverModel.setRedDot(redDot);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        refreshServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverModel.changeData();
            }
        });
    }

    public JFrame getFrame() {
        return this;
    }

    Action joinServer = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int modelRow = Integer.valueOf(e.getActionCommand());
            List<myClasses.Server> servers = serverModel.getServers();
            myClasses.Server server = servers.get(modelRow);

            int confirm = JOptionPane.showConfirmDialog(getFrame(), "Are you sure you want to join this server?", "Join confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == 0) {
                getFrame().dispose();
                AppClient appClient = new AppClient(server.getServer_name(), server.getPort(), username);
                appClient.run();
            }
        }
    };

    class serverTableModel extends AbstractTableModel {
        private ListServer listServer = new ListServer();

        private ImageIcon greenDot;
        private ImageIcon redDot;

        public List<Server> getServers() {
            return listServer.list;
        }

        public void setServers(List<myClasses.Server> servers) {
            listServer.list = servers;
            listServer.saveList();
        }

        public void setGreenDot(ImageIcon greenDot) {
            this.greenDot = greenDot;
        }

        public void setRedDot(ImageIcon redDot) {
            this.redDot = redDot;
        }

        @Override
        public int getRowCount() {
            return listServer.list.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public String getColumnName(int col) {
            switch (col) {
                case 0: return "Server name";
                case 1: return "Port";
                case 2: return "Status";
                case 3: return "";
            }
            return null;
        }

        @Override
        public Class getColumnClass(int col) {
            switch (col) {
                case 0:
                case 1:
                case 3:
                    return String.class;
                case 2: return ImageIcon.class;
            }
            return Object.class;
        }

        @Override
        public Object getValueAt(int row, int col) {
            myClasses.Server server = listServer.list.get(row);
            switch (col) {
                case 0: return server.getServer_name();
                case 1: return server.getPort();
                case 2: return server.isOnline() ? greenDot : redDot;
                case 3: return "JOIN";
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            switch (col) {
                case 0:
                case 1:
                case 2:
                    return false;
                case 3:
                    return true;
            }
            return false;
        }

        public void changeData() {
            listServer.loadList();
            fireTableDataChanged();
        }
    }
}
