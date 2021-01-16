package ro.ase.ism.sap.crypto01;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class HashFunction {

    public static byte[] getHash(String fileName, String algorithm, String provider) throws IOException,
            NoSuchProviderException, NoSuchAlgorithmException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        DataInputStream reader = new DataInputStream(new FileInputStream(file));

        byte[] inputBuffer;

        MessageDigest md = MessageDigest.getInstance(algorithm, provider);

        while ((inputBuffer = reader.readNBytes(1024)).length > 0) {
            md.update(inputBuffer);
        }

        reader.close();

        return md.digest();
    }

    public static byte[] getHash(byte[] values, String algorithm, String provider) throws NoSuchProviderException,
            NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm, provider);

        return md.digest(values);
    }
}
