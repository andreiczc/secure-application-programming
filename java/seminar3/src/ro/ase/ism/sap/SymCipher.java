package ro.ase.ism.sap;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;

public class SymCipher {

    public static void encryptECB(String inputFileName, String outputFileName, String password, String algorithm)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            throw new FileNotFoundException();
        }

        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(inputFile));
        byte[] fileContent = reader.readAllBytes();
        reader.close();

        Cipher ciperAlgo = Cipher.getInstance(algorithm + "/ECB/PKCS5Padding");
        byte[] paddedKey = MessageDigest.getInstance("MD5").digest(password.getBytes());
        ciperAlgo.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(paddedKey, algorithm));

        byte[] encrpytedContent = ciperAlgo.doFinal(fileContent);

        File outputFile = new File(outputFileName);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.createNewFile();

        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(outputFile));
        writer.write(encrpytedContent);
        writer.close();
    }

    public static void decryptECB(String encryptedFileName, String outputFileName, String password, String algorithm)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        File encryptedFile = new File(encryptedFileName);
        if (!encryptedFile.exists()) {
            throw new FileNotFoundException();
        }

        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(encryptedFile));
        byte[] fileContent = reader.readAllBytes();
        reader.close();

        byte[] paddedKey = MessageDigest.getInstance("MD5").digest(password.getBytes());

        Cipher ciperAlgo = Cipher.getInstance(algorithm + "/ECB/PKCS5Padding");
        ciperAlgo.init(Cipher.DECRYPT_MODE, new SecretKeySpec(paddedKey, algorithm));

        byte[] decrpytedContent = ciperAlgo.doFinal(fileContent);

        File outputFile = new File(outputFileName);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.createNewFile();

        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(outputFile));
        writer.write(decrpytedContent);
        writer.close();
    }

    public static void encryptCBC(String inputFileName, String outputFileName, String key, String algorithm)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            throw new FileNotFoundException();
        }

        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(inputFile));
        byte[] fileContent = reader.readAllBytes();
        reader.close();

        File outputFile = new File(outputFileName);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.createNewFile();

        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(outputFile));
        // CBC requires IV value (not a secret)
        // option 1 - use predefined value
        // option 2 - generate a random one

        // decide how to handle it
        // option 1 - hardcode a value
        // option 2 - write the iv into the cipher text file - at the beginning
        Cipher algo = Cipher.getInstance(algorithm + "/CBC/PKCS5Padding");

        int blockSize = algo.getBlockSize();

        // generate random IV
        byte[] iv = new byte[blockSize];
        new SecureRandom().nextBytes(iv);

        algo.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), algorithm), new IvParameterSpec(iv));


        writer.write(iv);

        writer.write(algo.doFinal(fileContent));

        writer.close();
    }

    public static void decryptCBC(String encryptedFileName, String outputFileName, String key, String algorithm)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher algo = Cipher.getInstance(algorithm + "/CBC/PKCS5Padding");

        File encryptedFile = new File(encryptedFileName);
        if (!encryptedFile.exists()) {
            throw new FileNotFoundException();
        }

        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(encryptedFile));
        byte[] iv = reader.readNBytes(algo.getBlockSize());

        algo.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), algorithm), new IvParameterSpec(iv));

        byte[] fileContent = reader.readAllBytes();
        reader.close();

        File decryptedFile = new File(outputFileName);
        if (decryptedFile.exists()) {
            decryptedFile.delete();
        }
        decryptedFile.createNewFile();

        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(decryptedFile));

        writer.write(algo.doFinal(fileContent));

        writer.close();
    }

    public static void encryptPBE(String inputFileName, String outputFileName, String key, byte[] salt, byte[] iv,
                                  int iterationCount, String algorithm)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        File inputFile = new File(inputFileName);
        if (!inputFile.exists()) {
            throw new FileNotFoundException();
        }

        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(inputFile));
        byte[] fileContent = reader.readAllBytes();
        reader.close();

        File outputFile = new File(outputFileName);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.createNewFile();

        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(outputFile));

        Cipher alg = Cipher.getInstance(algorithm);

        SecretKey generatedKey = SecretKeyFactory.getInstance(algorithm)
                .generateSecret(new PBEKeySpec(key.toCharArray()));

        alg.init(Cipher.ENCRYPT_MODE, generatedKey, new PBEParameterSpec(salt, iterationCount, new IvParameterSpec(iv)));

        writer.write(salt);
        writer.write(iv);
        writer.write(iterationCount);
        writer.write(alg.doFinal(fileContent));

        writer.close();
    }

    public static void decryptPBE(String encryptedFileName, String outputFileName, String key, String algorithm)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        File encryptedFile = new File(encryptedFileName);
        if (!encryptedFile.exists()) {
            throw new FileNotFoundException();
        }

        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(encryptedFile));

        byte[] salt = reader.readNBytes(16);
        byte[] iv = reader.readNBytes(16);
        int iterationCount = reader.read();
        byte[] fileContent = reader.readAllBytes();

        reader.close();

        File outputFile = new File(outputFileName);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        outputFile.createNewFile();

        BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(outputFile));

        Cipher alg = Cipher.getInstance(algorithm);

        SecretKey generatedKey = SecretKeyFactory.getInstance(algorithm)
                .generateSecret(new PBEKeySpec(key.toCharArray()));

        alg.init(Cipher.DECRYPT_MODE, generatedKey, new PBEParameterSpec(salt, iterationCount, new IvParameterSpec(iv)));

        writer.write(alg.doFinal(fileContent));
        writer.close();
    }
}
