package na.przypale.fitter;

import na.przypale.fitter.repositories.UsersRepository;
import na.przypale.fitter.repositories.cassandra.CassandraUsersRepository;
import scala.runtime.BoxedUnit;

public class Main {
    public static void main(String[] args) {
        ClusterConnector.connect("127.0.0.1").apply(cluster -> {
            SessionConnector.connect(cluster).apply("test").apply(session -> {
                UsersRepository usersRepository = new CassandraUsersRepository(session);
                usersRepository.insert(new User("user1"));
                return BoxedUnit.UNIT;
            });
            return BoxedUnit.UNIT;
        });
    }
}
