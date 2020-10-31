package ro.ase.ism.sap.ex4;

public class TestEnums {

    enum AlgorithmNames {
        AES(0), DES(1), DESede(2), RC5(3);

        final int label;

        AlgorithmNames(int label) {
            this.label = label;
        }
    }

    public static void main(String[] args) {
        AlgorithmNames alg = AlgorithmNames.AES;
        System.out.println(alg);
    }
}
