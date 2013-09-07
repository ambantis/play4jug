package threads;

/**
 * hello2akka
 * <p/>
 * User: Alexandros Bantis
 * Date: 9/7/13
 * Time: 9:48 AM
 */
public class BankAccount {
    private int balance;

    public BankAccount() {
        balance = 100;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    public int getBalance() {
        return balance;
    }

}
