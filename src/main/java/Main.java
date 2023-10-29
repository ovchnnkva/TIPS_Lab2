import java.util.Scanner;

public class Main {
    private static final ProbabilityCalculator probabilityCalculator = new ProbabilityCalculator();
    private static final Roc1 roc1 = new Roc1();

    public static void main(String[] args) {
        probabilityCalculator.start();
//        roc1.start(probabilityCalculator);
        roc1.startRandom(probabilityCalculator);
    }
}