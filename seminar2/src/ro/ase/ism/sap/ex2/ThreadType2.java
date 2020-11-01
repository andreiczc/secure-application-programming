package ro.ase.ism.sap.ex2;

public class ThreadType2 implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < noIterations; ++i) {
            System.out.println(this.id + " - " + this.message);
        }
    }

    public ThreadType2(int noIterations, String message, int id) {
        this.noIterations = noIterations;
        this.message = message;
        this.id = id;
    }

    private int noIterations;
    private String message;
    private int id;
}
