package ro.ase.ism.sap.ex6;

import java.io.*;

public class TestFiles {

    public static void dir(String path) {
        File currDir = new File(path);
        if (currDir.exists()) {
            File[] files = currDir.listFiles();
            for (File file : files) {
                System.out.println(file.isDirectory() ?
                        "Folder: " + file.getAbsolutePath()
                        : "File: " + file.getAbsolutePath());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // text files
        File messageFile = new File("test.txt");
        if (!messageFile.exists()) {
            messageFile.createNewFile();
        }

        PrintWriter writer = new PrintWriter(messageFile);
        writer.println("i just wrote this");
        writer.println("writing some more");
        writer.close();

        BufferedReader reader = new BufferedReader(
                new FileReader("test.txt"));
        String result;
        while ((result = reader.readLine()) != null) {
            System.out.println(result);
        }
        reader.close();

        // binary files
        File binaryFile = new File("file.bin");
        if (!binaryFile.exists()) {
            binaryFile.createNewFile();
        }

        DataOutputStream binaryWriter = new DataOutputStream(
                new FileOutputStream(binaryFile));
        binaryWriter.writeUTF("hello world");
        byte[] bytes = {0xA, 0x1, 0x2};
        binaryWriter.write(bytes);

        binaryWriter.close();

        DataInputStream binaryReader = new DataInputStream(
                new FileInputStream(binaryFile));
        String res2;
        while(binaryReader.available() != 0) {
            res2 = binaryReader.readUTF();
            bytes = binaryReader.readNBytes(3);
            System.out.println(res2);
            for (byte aByte : bytes) {
                System.out.format("%02x ", aByte);
            }
        }

        binaryReader.close();
    }
}
