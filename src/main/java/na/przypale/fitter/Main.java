package na.przypale.fitter;

import com.datastax.driver.core.Session;
import na.przypale.fitter.connectors.ClusterConnector;
import na.przypale.fitter.connectors.SessionConnector;
import na.przypale.fitter.entities.User;
import na.przypale.fitter.repositories.UsersRepository;
import na.przypale.fitter.repositories.cassandra.CassandraUsersRepository;
import scala.runtime.BoxedUnit;

public class Main {
    public static void main(String[] args) {
        ClusterConnector.connect("127.0.0.1").apply(cluster -> {
            SessionConnector.connect(cluster).apply("test").apply(session -> {
                testRepositories(session);
                return BoxedUnit.UNIT;
            });
            return BoxedUnit.UNIT;
        });
    }

    private static void testRepositories(Session session) {
        UsersRepository usersRepository = new CassandraUsersRepository(session);
        usersRepository.insert(new User("user1"));
    }
}
