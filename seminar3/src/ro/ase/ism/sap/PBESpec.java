package ro.ase.ism.sap;

import javax.crypto.spec.PBEKeySpec;

public class PBESpec {

    public PBESpec(byte[] salt, int iterationCount, byte[] iv, PBEKeySpec keySpec) {
        this.salt = salt.clone();
        this.iterationCount = iterationCount;
        this.iv = iv.clone();
        this.keySpec = keySpec;
    }

    public byte[] getSalt() {
        return salt;
    }

    public int getIterationCount() {
        return iterationCount;
    }

    public byte[] getIv() {
        return iv;
    }

    public PBEKeySpec getKeySpec() {
        return keySpec;
    }

    private byte[] salt;
    private int iterationCount;
    private byte[] iv;
    private PBEKeySpec keySpec;
}
