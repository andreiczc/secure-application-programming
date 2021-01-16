package ro.ase.ism.sap;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

class ComputeHashThread extends Thread {
    @Override
    public void run() {
        try {
            int keyLength = originalHash.length * 8;
            for (int i = lowerBound; i < upperBound; ++i) {
                byte[] currHash = skf.generateSecret(new PBEKeySpec(passwords.get(i).toCharArray(), salt, noIterations, keyLength))
                        .getEncoded();

                boolean isEqual = true;
                for (int j = 0; j < currHash.length; j++) {
                    if (currHash[j] != originalHash[j]) {
                        isEqual = false;
                    }
                }

                if (isEqual) {
                    foundPassword = passwords.get(i);
                    i = upperBound;
                }
            }
        } catch (Exception e) {
            System.out.println("err on thread " + threadId);
        }
    }

    public ComputeHashThread(ArrayList<String> passwords, SecretKeyFactory skf, byte[] salt, int noIterations,
                             byte[] originalHash, int lowerBound, int upperBound) {
        this.passwords = passwords;
        this.skf = skf;
        this.salt = salt;
        this.noIterations = noIterations;
        this.originalHash = originalHash;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.threadId = THREAD_ID++;
    }

    private ArrayList<String> passwords;
    private SecretKeyFactory skf;
    private byte[] salt;
    private int noIterations;
    private byte[] originalHash;
    private int lowerBound;
    private int upperBound;
    private int threadId;

    public String foundPassword;

    private static int THREAD_ID = 1;
}

public class Homework {

    public static int NO_THREADS = 16;

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader("passwords.txt"));

        ArrayList<String> passwords = new ArrayList<>();
        String curr;

        while ((curr = reader.readLine()) != null) {
            passwords.add(curr);
        }

        reader.close();

        byte[] salt = "ismprefix".getBytes();
        int noIterations = 200;
        String algorithm = "PBKDF2WithHmacSHA1";

        final byte[] originalHash = new byte[]{0x26, 0x3D, (byte) 0xAF, 0x03, 0x1C, (byte) 0xE6, 0x36, (byte) 0xDF, (byte) 0x86, 0x0F,
                (byte) 0xAB, 0x30, (byte) 0xD5, (byte) 0x88, 0x6D, (byte) 0xFE};

        SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);

        ArrayList<ComputeHashThread> threads = new ArrayList<>();

        int calcPerThread = passwords.size() / NO_THREADS;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < NO_THREADS; ++i) {
            int upperBound = (i == (NO_THREADS - 1)) ? passwords.size() : (i + 1) * calcPerThread;

            ComputeHashThread currThread = new ComputeHashThread(passwords, skf, salt, noIterations, originalHash,
                    i * calcPerThread, upperBound);
            currThread.start();
            threads.add(currThread);
        }

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        long endTime = System.currentTimeMillis();

        String foundPassword = null;
        for (ComputeHashThread thread : threads) {
            if (thread.foundPassword != null) {
                foundPassword = thread.foundPassword;
            }
        }

        System.out.println("It took " + (endTime - startTime) + " ms to find the password " + foundPassword);
    }
}
