package myClasses;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ListServer implements Serializable {
    public List<Server> list = new ArrayList<>();

    public ListServer() {
        try {
            File myfile = new File("./server_configs.dat");
            myfile.createNewFile();
            boolean empty = (myfile.length() == 0);
            if (!empty) {
                FileInputStream fileIn = new FileInputStream("./server_configs.dat");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                list = (ArrayList<Server>) in.readObject();
                in.close();
                fileIn.close();
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void loadList() {
        try {
            File myfile = new File("./server_configs.dat");
            myfile.createNewFile();
            boolean empty = (myfile.length() == 0);
            if (!empty) {
                FileInputStream fileIn = new FileInputStream("./server_configs.dat");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                list = (ArrayList<Server>) in.readObject();
                in.close();
                fileIn.close();
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void saveList() {
        try {
            File myfile = new File("./server_configs.dat");
            myfile.createNewFile();
            FileOutputStream fileOut = new FileOutputStream("./server_configs.dat");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.list);
            out.close();
            fileOut.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
