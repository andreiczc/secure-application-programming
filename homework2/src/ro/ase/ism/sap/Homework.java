package ro.ase.ism.sap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Homework {
    public static void printHex(String description, byte[] value) {
        System.out.print(description);
        for (byte b : value) {
            System.out.printf("%02x ", b);
        }
        System.out.println();
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] iv = new byte[16];
        for (int i = 0; i < iv.length; ++i) {
            iv[i] = 1;
        }

        printHex("IV is: ", iv);

        byte[] hashValue = Base64.getDecoder().decode("3dskqgW08YbmMVbYvE7KLU32gkJz9M6jlvODx/XVwt0=");
        printHex("The hash value is: ", hashValue);

        File keysFolder = new File("keys");
        String[] keyFiles = keysFolder.list();

        String fileName = null;

        for (String keyFile : keyFiles) {
            File currFile = new File("keys/" + keyFile);
            BufferedInputStream reader = new BufferedInputStream(new FileInputStream(currFile));
            byte[] fileContent = reader.readAllBytes();
            reader.close();

            byte[] currHash = MessageDigest.getInstance("SHA-256").digest(fileContent);

            boolean isEqual = true;
            for (int i = 0; i < currHash.length; ++i) {
                if (currHash[i] != hashValue[i]) {
                    isEqual = false;
                }
            }

            if (isEqual) {
                fileName = "keys/" + keyFile;
                System.out.println("My file is: " + keyFile);
                break;
            }
        }

        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(fileName));
        byte[] key = reader.readAllBytes();
        reader.close();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

        String msgName = fileName.substring(5, fileName.lastIndexOf('.')) + ".secret";
        System.out.println("My secret is: " + msgName);

        reader = new BufferedInputStream(new FileInputStream(msgName));
        byte[] fileContent = reader.readAllBytes();
        reader.close();

        String secretMsg = new String(cipher.doFinal(fileContent));
        System.out.println(secretMsg);
    }
}
