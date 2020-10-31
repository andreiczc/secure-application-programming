package ro.ase.ism.sap;

import java.util.Base64;

public class DataTypes {

    public static String convertToHex(byte value) {
        String[] hexSymbols = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

        StringBuilder result = new StringBuilder();

        int temp = (int) value;

        if (temp < 0) {
            temp += 0x100;
        }

        result.insert(0, hexSymbols[temp % 16]);
        temp = temp >> 4; // faster than the division
        result.insert(0, hexSymbols[temp % 16]);

        return result.toString();
    }

    public static void main(String[] args) {
        int intValue = 10;
        System.out.println(intValue);

        intValue = 0xA;
        System.out.println(intValue);

        intValue = 0b1010;
        System.out.println(intValue);

        // define a byte with the 10101010... pattern
        byte byteValue = (byte) 0b10101010;
        System.out.println(byteValue);

        byteValue = (byte) 0xAA;
        System.out.println(byteValue);

        byteValue = (byte) (1 << 7 | 1 << 5 | 1 << 3 | 1 << 1);
        System.out.println(byteValue);

        // check for a bit
        int simpleValue = 23;
        // check the 5th bit in the int value
        byte mask = 1 << 4;

        System.out.println(String.format("The 5th bit is 1: %b", simpleValue & mask));

        // printing values in Hex strings
        byte[] values = new byte[]{10, 45, 23, 6, 0, 127};

        System.out.print("Byte values: ");
        for (byte value : values) {
            System.out.print(convertToHex(value) + " | ");
        }
        System.out.println();

        intValue = 34674;
        System.out.println("The bit representation is of 34674: " + Integer.toHexString(intValue));

        // get byte array from integer
        byte[] byteRepresentation = new byte[Integer.BYTES];
        int maskFull = 0xFF;
        for (int i = 0; i < byteRepresentation.length; ++i) {
            int idx = byteRepresentation.length - i - 1;
            byteRepresentation[idx] = (byte) (intValue & maskFull);
            intValue = intValue >> 8;
        }

        System.out.println();
        System.out.print("The Hex representation is: ");
        for (byte value : byteRepresentation) {
            System.out.format("0x%02x | ", value);
        }

        System.out.println();
        // base 64 encoding
        System.out.println("Base64 representation of 34674: " + Base64.getEncoder().encodeToString(byteRepresentation));
    }
}
