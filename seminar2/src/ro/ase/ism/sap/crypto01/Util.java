package ro.ase.ism.sap.crypto01;

public class Util {

    public static void printHex(byte[] array, String description) {
        System.out.print(description);
        for (byte b : array) {
            System.out.printf("%02x ", b);
        }
        System.out.println();
    }
}
