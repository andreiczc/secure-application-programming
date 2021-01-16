package ro.ase.ism.sap.ex3;

public class BankAccount {

    public void pay(double amount) throws NotEnoughFundsException {
        if (amount <= balance) {
            balance -= amount;
        } else {
            throw new NotEnoughFundsException();
        }
    }

    public double getBalance() {
        return balance;
    }

    public BankAccount(double balance) {
        this.balance = balance;
    }

    private double balance;
}
