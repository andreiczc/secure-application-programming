package ro.ase.ism.sap;

import org.jetbrains.annotations.NotNull;

import javax.crypto.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

public class KeyStoreManager {
    public static void list(String keyStoreFile, String password) throws CertificateException, NoSuchAlgorithmException,
            KeyStoreException, IOException {
        File ksFile = new File(keyStoreFile);
        if (!ksFile.exists()) {
            throw new FileNotFoundException();
        }

        KeyStore keyStore = KeyStore.getInstance(ksFile, password.toCharArray());

        System.out.printf("Keystore %s contains: \n", keyStoreFile);
        keyStore.aliases().asIterator().forEachRemaining(s -> {
            try {
                byte[] hashValue = MessageDigest.getInstance("SHA-256").digest(
                        keyStore.getCertificate(s).getEncoded()
                );

                if (keyStore.isCertificateEntry(s)) {
                    System.out.printf("%s - trustedCertEntry - ", s);
                } else {
                    System.out.printf("%s - PrivateKeyEntry - ", s);
                }

                for (byte b : hashValue) {
                    System.out.printf("%02x ", b);
                }
                System.out.println();
            } catch (KeyStoreException | NoSuchAlgorithmException | CertificateEncodingException e) {
                e.printStackTrace();
            }
        });
    }

    public static PublicKey getPublicKey(@NotNull KeyStore keyStore, String alias)
            throws KeyStoreException {
        return keyStore.getCertificate(alias).getPublicKey();
    }

    public static PrivateKey getPrivateKey(@NotNull KeyStore keyStore, String alias, String password)
            throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
    }

    public static PublicKey extractPublicKey(String certPath) throws IOException, CertificateException {
        File certFile = new File(certPath);
        if (!certFile.exists()) {
            throw new FileNotFoundException();
        }

        FileInputStream fis = new FileInputStream(certFile);
        PublicKey key = CertificateFactory.getInstance("X.509")
                .generateCertificate(fis).getPublicKey();
        fis.close();

        return key;
    }

    public static byte[] generateRandomKey(String encAlgorithm) throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance(encAlgorithm).generateKey().getEncoded();
    }

    public static byte[] encryptRSA(byte[] input, Key key) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(input);
    }

    public static byte[] decryptRSA(byte[] input, Key key) throws InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(input);
    }

    public static byte[] signDigitalSignature(String filePath, PrivateKey privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] fileContent = reader.readAllBytes();
        reader.close();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(fileContent);

        return signature.sign();
    }

    public static boolean verifiyDigitalSignature(String filePath, byte[] digitalSignature, PublicKey publicKey) throws NoSuchAlgorithmException,
            InvalidKeyException, SignatureException, IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] fileContent = reader.readAllBytes();
        reader.close();

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(fileContent);

        return signature.verify(digitalSignature);
    }

    public static void storeData(String fileName, byte[] values) throws IOException {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
        writer.write(values);
        writer.close();
    }

    public static byte[] readData(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
        byte[] content = reader.readAllBytes();
        reader.close();

        return content;
    }
}
