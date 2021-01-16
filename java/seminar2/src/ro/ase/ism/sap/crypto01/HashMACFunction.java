package ro.ase.ism.sap.crypto01;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class HashMACFunction {

    public static byte[] getHashMAC(String fileName, byte[] key, String algorithm, String provider) throws IOException,
            NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        DataInputStream reader = new DataInputStream(new FileInputStream(file));

        Mac hmac = Mac.getInstance(algorithm, provider);
        hmac.init(new SecretKeySpec(key, algorithm));

        byte[] buffer;
        while ((buffer = reader.readNBytes(1024)).length > 0) {
            hmac.update(buffer);
        }

        reader.close();

        return hmac.doFinal();
    }
}
