package ro.ase.ism.sap.ex2;

public class ProgMain {

    public static void main(String[] args) {
        System.out.println("main started");

        ThreadType1 thread1 = new ThreadType1(5, "my message", 1);
        ThreadType1 thread2 = new ThreadType1(3, "another message", 2);

        thread1.start();
        thread2.start();

        Thread t1 = new Thread(new ThreadType2(3, "cool message", 3));
        Thread t2 = new Thread(new ThreadType2(7, "another cool message", 4));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException ex) {
            System.out.println("problem joining threads");
        }

        System.out.println("main ended");
    }
}
