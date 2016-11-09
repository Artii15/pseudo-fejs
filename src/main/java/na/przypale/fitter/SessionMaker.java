package na.przypale.fitter;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class SessionMaker {
    private Cluster cluster;

    public SessionMaker(Cluster cluster) {
        this.cluster = cluster;
    }

    public Operations<Operations<Session>> make(String keyspace) {
        return (operations) -> {
            Session session = cluster.connect(keyspace);
            operations.execute(session);
            session.close();
        };
    }
}
