import org.junit.Before;
import org.junit.Test;

public class RocTest {
     private ProbabilityCalculator probabilityCalculator;
     private Roc1 roc1;
     @Before
     public void init() {
          probabilityCalculator = new ProbabilityCalculator();
          roc1 = new Roc1();
     }

     @Test
     public void test() {
          probabilityCalculator.start();
          roc1.start(probabilityCalculator);
     }
}
