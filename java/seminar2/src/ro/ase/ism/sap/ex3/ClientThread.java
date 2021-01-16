package ro.ase.ism.sap.ex3;

import java.util.Random;

public class ClientThread implements Runnable {

    @Override
    public void run() {
        try {
            while (true) {
                Random generator = new Random();
                System.out.println(this.name + " is trying to make a payment");
                account.pay(generator.nextInt(50));
            }
        } catch (Exception e) {
            System.out.println("Thread " + threadId + " has stopped");
        }
    }

    public ClientThread(String name, BankAccount account, int threadId) {
        this.name = name;
        this.account = account;
        this.threadId = threadId;
    }

    private String name;
    private BankAccount account;
    private int threadId;
}
