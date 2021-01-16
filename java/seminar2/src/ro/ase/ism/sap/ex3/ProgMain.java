package ro.ase.ism.sap.ex3;

public class ProgMain {
    public static void main(String[] args) {
        BankAccount account = new BankAccount(50);
        ClientThread client1 = new ClientThread("andrei", account, 1);
        ClientThread client2 = new ClientThread("robert", account, 2);

        Thread t1 = new Thread(client1);
        Thread t2 = new Thread(client2);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();

            System.out.println("Final balance: " + account.getBalance());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
