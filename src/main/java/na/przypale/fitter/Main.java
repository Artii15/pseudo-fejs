package na.przypale.fitter;

import scala.runtime.BoxedUnit;

public class Main {
    public static void main(String[] args) {
        ClusterConnector.connect("127.0.0.1").apply(cluster -> {
            SessionConnector.connect(cluster).apply("test").apply(session -> BoxedUnit.UNIT);
            return BoxedUnit.UNIT;
        });
    }
}
