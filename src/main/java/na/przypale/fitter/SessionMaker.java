package na.przypale.fitter;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class SessionMaker {
    private Cluster cluster;

    public SessionMaker(Cluster cluster) {
        this.cluster = cluster;
    }

    public void perform(Operations<Session> operations, String keyspace) {
        Session session = cluster.connect(keyspace);
        operations.execute(session);
        session.close();
    }
}
