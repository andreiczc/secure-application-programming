package ro.ase.ism.sap.crypto01;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class PBKDF2HashFunction {

    public static byte[] getHash(char[] userPassword, byte[] salt, String algorithm, int keyLength) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);
        PBEKeySpec spec = new PBEKeySpec(userPassword, salt, noIterations, keyLength);
        SecretKey key = skf.generateSecret(spec);

        return key.getEncoded();
    }

    static int noIterations = 1024;
}
