package ro.ase.ism.sap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class ProgMain {
    public static void printHex(byte[] values, String description) {
        System.out.print(description + " ");
        for (byte value : values) {
            System.out.printf("%02x ", value);
        }
        System.out.println();
    }

    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        SymCipher.encryptECB("test.txt", "test.enc", "key", "AES");
        SymCipher.decryptECB("test.enc", "decrypt.txt", "key", "AES");

        SymCipher.encryptCBC("test.txt", "test2.enc", "password", "DES");
        SymCipher.decryptCBC("test2.enc", "decrypt2.txt", "password", "DES");

        // solution to extend/reduce passwords to required size
        // solution to convert string based passwords to binary values
        // password based encryption (PBE)
        AlgorithmParameters params = SymCipher.encryptPBE("test.txt", "test3.enc", "a", "salt",
                10, "PBEWithMD5AndDES");
        SymCipher.decryptPBE("test3.enc", "decrypt3.txt", "pass", "PBEWithMD5AndDES",
                new PBESpec("salt".getBytes(), 10), params);
    }
}
