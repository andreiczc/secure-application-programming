package ro.ase.ism.sap.ex2;

public class TestClasses {

    public static void main(String[] args) {
        String fileName1 = "test.txt";
        String fileName2 = fileName1;

        System.out.println(fileName1 == fileName2);

        fileName1 = "test2.txt";
        System.out.println(fileName1 == fileName2);
    }
}
