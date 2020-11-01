package ro.ase.ism.sap.crypto01;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

public class ProgMain {
    final static String BOUNCY_CASTLE_PROVIDER = "BC";

    public static void main(String[] args) throws NoSuchProviderException, NoSuchAlgorithmException, IOException,
            InvalidKeyException, InvalidKeySpecException {

        // load Bouncy Castle provider
        Providers.loadBCProvider();

        Providers.checkProvider(BOUNCY_CASTLE_PROVIDER);

        byte[] shaValue = HashFunction.getHash("message.txt", "SHA-256", BOUNCY_CASTLE_PROVIDER);
        Util.printHex(shaValue, "SHA-256: ");

        byte[] mdValue = HashFunction.getHash("message.txt", "MD5", BOUNCY_CASTLE_PROVIDER);
        Util.printHex(mdValue, "MD5: ");

        byte[] shaValue2 = HashFunction.getHash("This is a secret message".getBytes(), "SHA-256", BOUNCY_CASTLE_PROVIDER);
        Util.printHex(shaValue2, "SHA-256 for 2nd time: ");

        byte[] hmac = HashMACFunction.getHashMAC("message.txt", "password".getBytes(), "HmacSHA1", BOUNCY_CASTLE_PROVIDER);
        Util.printHex(hmac, "Hmac value is: ");

        byte[] secretPass = PBKDF2HashFunction.getHash("hello".toCharArray(), SaltFunction.getRandomSalt(32),
                "PBKDF2WithHmacSHA256", 80);
        Util.printHex(secretPass, "Secret pass is: ");
    }
}
