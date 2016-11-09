package na.przypale.fitter;

import com.datastax.driver.core.Cluster;

public class Connector {

	private static final String DEFAULT_CONTACT_POINT = "127.0.0.1";

    private String contactPoint;

    public Connector() {
       this(DEFAULT_CONTACT_POINT);
    }

    public Connector(String contactPoint) {
        this.contactPoint = contactPoint;
    }

    public void performClusterOperations(Operations<SessionMaker> operations) {
        Cluster cluster = Cluster.builder().addContactPoint(contactPoint).build();
        operations.execute(new SessionMaker(cluster));
        cluster.close();
    }
}
