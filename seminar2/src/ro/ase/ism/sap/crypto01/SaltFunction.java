package ro.ase.ism.sap.crypto01;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SaltFunction {

    public static byte[] getRandomSalt(int saltSize) throws NoSuchAlgorithmException {
        byte[] randomSalt = new byte[saltSize];
        SecureRandom generator = SecureRandom.getInstance("SHA1PRNG");
        generator.nextBytes(randomSalt);

        return randomSalt;
    }
}
