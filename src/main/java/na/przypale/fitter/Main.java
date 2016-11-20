package na.przypale.fitter;

import com.datastax.driver.core.Session;
import na.przypale.fitter.connectors.ClusterConnector;
import na.przypale.fitter.connectors.SessionConnector;
import scala.runtime.BoxedUnit;

public class Main {
    public static void main(String[] args) {
        ClusterConnector.connect("127.0.0.1").apply(cluster -> {
            SessionConnector.connect(cluster).apply("test").apply(session -> {
                startApp(session);
                return BoxedUnit.UNIT;
            });
            return BoxedUnit.UNIT;
        });
    }

    private static void startApp(Session session) {
        App.start(session);
    }
}
