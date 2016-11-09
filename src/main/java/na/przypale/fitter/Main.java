package na.przypale.fitter;

public class Main {
    public static void main(String[] args) {
        Connector connector = new Connector();
        connector.performClusterOperations((SessionMaker sessionMaker) -> {
            sessionMaker.make("test").execute((session) -> {

            });
        });
    }
}
