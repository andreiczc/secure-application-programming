package ro.ase.ism.sap.ex2;

public class ProgMain {

    public static void main(String[] args) {

        ThreadType1 thread1 = new ThreadType1(5, "my message", 1);
        ThreadType1 thread2 = new ThreadType1(3, "another message", 2);

        thread1.start();
        thread2.start();

        new Thread(new ThreadType2(3, "cool message", 3)).start();
        new Thread(new ThreadType2(7, "another cool message", 4)).start();

    }
}
