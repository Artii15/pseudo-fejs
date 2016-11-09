package na.przypale.fitter;

import scala.runtime.BoxedUnit;

public class Main {
    public static void main(String[] args) {
        ClusterConnector.doInCluster("127.0.0.1").apply(cluster -> {
            SessionConnector.makeConnector(cluster).apply("test").apply(session -> BoxedUnit.UNIT);
            return BoxedUnit.UNIT;
        });
    }
}
