package ro.ase.ism.sap.ex3;

public class TestExceptions {

    public static void generateException(int number) throws SmallIntException {
        if (number < Byte.MAX_VALUE) {
            throw new SmallIntException();
        }
    }

    public static void main(String[] args) {
        System.out.println("start");

        int value = 10;

        // throw an exception
        try {
            System.out.println("starting try");
            value += 2;
            generateException(100);
            value += 2;
            System.out.println("value is " + value);
        } catch (SmallIntException e) {
            System.out.println("exception was thrown");
        }

        System.out.println("end");
    }
}

class SmallIntException extends Exception {
    public SmallIntException(String message) {
        super(message);
    }

    public SmallIntException() {
        super();
    }
}