package frontend;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.dispatch.MessageDispatcher;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import backend.Manager;
import akka.http.javadsl.Http;
import frontend.services.auth.AuthManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.CompletionStage;

/**
 * Основной класс фроентенда. HTTP сервер
 */
public class HTTPServer extends AllDirectives {
    private final static Logger logger = Logger.getLogger(HTTPServer.class);
    private final Router router;
    private ActorRef authManager;

    public HTTPServer(ActorSystem system, ActorRef manager ) {
        MessageDispatcher dispatcher = system.dispatchers().lookup("akka.actor.default-dispatcher");
        this.authManager = system.actorOf(AuthManager.props(), "authManager");
        this.router =  new Router(manager, authManager, dispatcher);
    }

    public static void main(String [] args ) {
        ActorSystem system = ActorSystem.create("Version_register_service");

        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);

        ActorRef manager = system.actorOf(Manager.props(), "manager");

        HTTPServer server = new HTTPServer(system, manager);

        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = server.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow,
                ConnectHttp.toHost("localhost", 8080), materializer);

        logger.info("Server online at http://localhost:8080/\nPress RETURN to stop...");
        try {
            System.in.read();
        } catch (IOException e) {
        }

        binding
                .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
                .thenAccept(unbound -> system.terminate()); // and shutdown when done
    }

    Route createRoute() {
        return router.routes();
    }


}
