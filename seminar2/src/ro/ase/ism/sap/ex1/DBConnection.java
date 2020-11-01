package ro.ase.ism.sap.ex1;

import java.io.*;
import java.util.Arrays;

public class DBConnection implements Serializable {

    public void serialize(File file) {
        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream(file))) {
            writer.writeUTF(serverName);
            writer.writeInt(port);
            writer.writeInt(password.length);
            writer.write(password);
            writer.writeUTF(userId);
            writer.writeBoolean(useSecureConnection);
        } catch (IOException ex) {
            System.out.println("error in serialization");
        }
    }

    public static DBConnection deserialize(File file) {
        try (DataInputStream reader = new DataInputStream(new FileInputStream(file))) {
            String serverName = reader.readUTF();
            int serverPort = reader.readInt();
            byte[] password = new byte[reader.readInt()];
            password = reader.readNBytes(password.length);
            String userId = reader.readUTF();
            boolean useSecureConnection = reader.readBoolean();

            return new DBConnection(serverName, serverPort, password, userId, useSecureConnection);
        } catch (IOException ex) {
            System.out.println("error in deserialization");

            return null;
        }
    }

    @Override
    public String toString() {
        return "DBConnection{" +
                "serverName='" + serverName + '\'' +
                ", port=" + port +
                ", password=" + Arrays.toString(password) +
                ", userId='" + userId + '\'' +
                ", useSecureConnection=" + useSecureConnection +
                '}';
    }

    // called once when the class is loaded by JVM
    static {
        System.out.println("DBConnection has been loaded");
    }

    // before each constructor
    {
        System.out.println("Pre-constructor called");
    }

    public DBConnection(String serverName, int port, byte[] password, String userId, boolean useSecureConnection) {
        this.serverName = serverName;
        this.port = port;
        this.password = password;
        this.userId = userId;
        this.useSecureConnection = useSecureConnection;
    }

    public String getServerName() {
        return serverName;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        if (password == null) {
            return "";
        }

        return new String(password);
    }

    public String getUserId() {
        return userId;
    }

    public boolean isUseSecureConnection() {
        return useSecureConnection;
    }

    private String serverName;
    private int port;
    private transient byte[] password;
    private String userId;
    private boolean useSecureConnection;
}
