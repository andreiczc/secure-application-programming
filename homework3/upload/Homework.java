package ro.ase.ism.sap;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;

public class Homework {

    public static void printHex(String description, byte[] value) {
        System.out.printf("%s", description);
        for (byte b : value) {
            System.out.printf("%02x ", b);
        }
        System.out.println();
    }

    public static void main(String[] args) throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            IOException, NoSuchPaddingException, UnrecoverableKeyException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, InvalidAlgorithmParameterException, SignatureException {
        KeyStore keyStore = KeyStore.getInstance(new File("studentstore.ks"), "passks".toCharArray());
        PrivateKey privateKey = (PrivateKey) keyStore.getKey("private1", "passism1".toCharArray());

        FileInputStream fis = new FileInputStream("studentCertificate.cer");
        PublicKey publicKey = CertificateFactory.getInstance("X.509")
                .generateCertificate(fis).getPublicKey();
        fis.close();

        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);

        BufferedInputStream reader = new BufferedInputStream(new FileInputStream("Session.key"));
        byte[] sessionFileContent = reader.readAllBytes();
        reader.close();

        byte[] sessionKey = rsaCipher.doFinal(sessionFileContent);
        System.out.printf("My session key is: %s\n", new String(sessionKey));

        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[aesCipher.getBlockSize()];
        Arrays.fill(iv, (byte) 1);
        aesCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(sessionKey, "AES"), new IvParameterSpec(iv));

        reader = new BufferedInputStream(new FileInputStream("signature.ds"));
        byte[] digitalSignature = reader.readAllBytes();
        reader.close();

        String question = "";

        fis = new FileInputStream("ISM_PGP.cer");
        PublicKey ismPublicKey = CertificateFactory.getInstance("X.509")
                .generateCertificate(fis).getPublicKey();
        fis.close();

        String[] messagesList = new File("messages").list();
        for (String s : messagesList) {
            reader = new BufferedInputStream(new FileInputStream("messages/" + s));
            byte[] currMsgContent = reader.readAllBytes();
            reader.close();

            byte[] decryptedMsg = aesCipher.doFinal(currMsgContent);

            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(ismPublicKey);
            signature.update(decryptedMsg);
            if (signature.verify(digitalSignature)) {
                question = new String(decryptedMsg);
            }
        }

        System.out.println("The question is: " + question);

        aesCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(sessionKey, "AES"), new IvParameterSpec(iv));

        reader = new BufferedInputStream(new FileInputStream("response.txt"));
        byte[] responseContent = reader.readAllBytes();
        reader.close();

        byte[] encryptedResponse = aesCipher.doFinal(responseContent);

        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream("response.enc"));
        writer.write(encryptedResponse);
        writer.close();

        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(responseContent);

        byte[] mySignature = signature.sign();

        writer = new BufferedOutputStream(new FileOutputStream("response.ds"));
        writer.write(mySignature);
        writer.close();

        // checking if it's ok
        aesCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(sessionKey, "AES"), new IvParameterSpec(iv));
        reader = new BufferedInputStream(new FileInputStream("response.enc"));
        byte[] encryptedRead = reader.readAllBytes();
        reader.close();

        byte[] decryptedResp = aesCipher.doFinal(encryptedRead);

        signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(decryptedResp);

        if (signature.verify(mySignature)) {
            System.out.println("The file is correct");
        } else {
            System.out.println("Check your program");
        }
    }
}
