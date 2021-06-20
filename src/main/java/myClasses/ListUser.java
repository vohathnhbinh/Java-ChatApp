package myClasses;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ListUser {
    public List<User> list = new ArrayList<>();

    public ListUser() {
        try {
            File myfile = new File("./users.dat");
            myfile.createNewFile();
            boolean empty = (myfile.length() == 0);
            if (!empty) {
                FileInputStream fileIn = new FileInputStream("./users.dat");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                list = (ArrayList<User>) in.readObject();
                in.close();
                fileIn.close();
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void loadList() {
        try {
            File myfile = new File("./users.dat");
            myfile.createNewFile();
            boolean empty = (myfile.length() == 0);
            if (!empty) {
                FileInputStream fileIn = new FileInputStream("./users.dat");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                list = (ArrayList<User>) in.readObject();
                in.close();
                fileIn.close();
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void saveList() {
        try {
            File myfile = new File("./users.dat");
            myfile.createNewFile();
            FileOutputStream fileOut = new FileOutputStream("./users.dat");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this.list);
            out.close();
            fileOut.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
