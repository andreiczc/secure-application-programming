package ro.ase.ism.sap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;

public class ProgMain {

    public static void printHex(String description, byte[] value) {
        System.out.printf("%s", description);
        for (byte b : value) {
            System.out.printf("%02x ", b);
        }
        System.out.println();
    }

    public static void main(String[] args) throws CertificateException, NoSuchAlgorithmException,
            KeyStoreException, IOException, UnrecoverableKeyException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchPaddingException, SignatureException {
        KeyStore keyStore = KeyStore.getInstance(new File("keys/ismkeystore.ks"), "passks".toCharArray());

        KeyStoreManager.list("keys/ismkeystore.ks", "passks");

        printHex("Public key for ismkey1 cert is: ",
                KeyStoreManager.getPublicKey(keyStore, "ismkey1").getEncoded());
        printHex("Private key for ismkey1 is: ",
                KeyStoreManager.getPrivateKey(keyStore, "ismkey1", "passks").getEncoded());

        printHex("Public key extracted from certificate.cer: ",
                KeyStoreManager.extractPublicKey("keys/certificate.cer").getEncoded());

        byte[] randomAESkey = KeyStoreManager.generateRandomKey("AES");
        printHex("Generated AES key: ", randomAESkey);

        PublicKey publicKey = KeyStoreManager.getPublicKey(keyStore, "ismkey1");
        PrivateKey privateKey = KeyStoreManager.getPrivateKey(keyStore, "ismkey1", "passks");

        byte[] encryptedKey = KeyStoreManager.encryptRSA(randomAESkey, publicKey);
        byte[] decryptedKey = KeyStoreManager.decryptRSA(encryptedKey, privateKey);

        System.out.println("The keys are equals: " + Arrays.equals(decryptedKey, randomAESkey));

        // to provide authentication: encrypt with private key, decrypt with public (anyone can decrypt, but for sure
        // it's from you)

        // to provide security: encrypt with public key, decrypt with private

        byte[] message = "Hello! How are you?".getBytes();
        byte[] encMessage = KeyStoreManager.encryptRSA(message, privateKey);
        byte[] decMessage = KeyStoreManager.decryptRSA(encMessage, publicKey);
        System.out.println("The message is: " + new String(decMessage));

        byte[] digitalSignature = KeyStoreManager.signDigitalSignature("keys/ismkeystore.ks", privateKey);
        System.out.println("Signature: " + KeyStoreManager.verifiyDigitalSignature("keys/ismkeystore.ks",
                digitalSignature, publicKey));


        byte[] ds = KeyStoreManager.signDigitalSignature("message.txt", privateKey);
        printHex("The digital signature is: ", ds);

        System.out.println("Signature: " + KeyStoreManager.verifiyDigitalSignature("message.txt",
                ds, publicKey));
    }
}
