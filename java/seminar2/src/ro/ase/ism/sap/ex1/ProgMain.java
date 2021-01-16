package ro.ase.ism.sap.ex1;

import java.io.*;

public class ProgMain {

    public static void main(String[] args) {
        System.out.println("ProgMain has started");

        DBConnection conn1 = new DBConnection("server", 80, "1234".getBytes(), "andrei", true);
        DBConnection conn2 = new DBConnection("server", 82, "password".getBytes(), "robert", false);

        try {
            File file = new File("./data.dat");
            if (!file.exists()) {
                file.createNewFile();
            }

            conn1.serialize(file);

            DBConnection conn3 = DBConnection.deserialize(file);

            System.out.println(conn3);

            // use the Java serialization mechanism
            File javaFile = new File("./data2.dat");
            if (!javaFile.exists()) {
                javaFile.createNewFile();
            }

            ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(javaFile));
            writer.writeObject(conn2);
            writer.close();

            ObjectInputStream reader = new ObjectInputStream(new FileInputStream(javaFile));
            DBConnection conn4 = (DBConnection) reader.readObject();

            System.out.println(conn4);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
