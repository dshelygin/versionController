package frontend;

import akka.actor.ActorRef;
import akka.dispatch.MessageDispatcher;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;


import akka.pattern.PatternsCS;
import backend.Manager.*;
import backend.data.requests.GetLastClassVersionData;
import backend.data.requests.RegisterClassVersionData;
import backend.services.MesageIdGenerator;

import com.softwaremill.session.BasicSessionEncoder;
import com.softwaremill.session.CheckHeader;
import com.softwaremill.session.RefreshTokenStorage;
import com.softwaremill.session.Refreshable;
import com.softwaremill.session.SessionConfig;
import com.softwaremill.session.SessionEncoder;
import com.softwaremill.session.SessionManager;
import com.softwaremill.session.SetSessionTransport;
import com.softwaremill.session.javadsl.HttpSessionAwareDirectives;
import com.softwaremill.session.javadsl.InMemoryRefreshTokenStorage;
import static com.softwaremill.session.javadsl.SessionTransports.*;

import frontend.data.User;
import frontend.data.VSession;


import org.apache.log4j.Logger;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.marshallers.jackson.Jackson;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;



/**
 * Created by dshelygin on 21.04.2018.
 */
public class Router extends HttpSessionAwareDirectives<VSession> {
    final private ActorRef manager;
    final static Logger logger = Logger.getLogger(Router.class);
    private MesageIdGenerator mesageIdGenerator = MesageIdGenerator.INSTANCE;

    private static final String SECRET = "sdfs23xcvisdfu290834890jsaklfdgjp[asues0igojdsflgm23i90jasdg;d'gkdpofgikdfgdfj324590-jsdfs";
    private static final SessionEncoder<VSession> BASIC_ENCODER = new BasicSessionEncoder<>(VSession.getSerializer());

    private Refreshable<VSession> refreshable;
    private SetSessionTransport sessionTransport;
    private CheckHeader<VSession> checkHeader;

    public Router(ActorRef manager, MessageDispatcher dispatcher) {
        super(new SessionManager<>(
                SessionConfig.defaultConfig(SECRET),
                BASIC_ENCODER
        ));
        this.manager = manager;
        refreshable = new Refreshable(getSessionManager(), REFRESH_TOKEN_STORAGE, dispatcher);
        sessionTransport = HeaderST;
        checkHeader = new CheckHeader<>(getSessionManager());

    }

 // in-memory refresh token storage
    private static final RefreshTokenStorage<VSession> REFRESH_TOKEN_STORAGE = new InMemoryRefreshTokenStorage<VSession>() {
        @Override
        public void log(String msg) {
         logger.info(msg);
        }
    };

    Timeout timeout = new Timeout(Duration.create(5, TimeUnit.SECONDS));

    public Route routes() {
        return  route (

            route (pathPrefix("api", () ->
                route ( //randomTokenCsrfProtection(checkHeader, () ->
                    post( () ->
                        path("login", () ->
                            login()
                        )
                    ),
                    route (pathPrefix("requests", () ->
                         requiredSession(refreshable, sessionTransport, session ->
                             route (
                                post( () ->
                                    path("getLastClassVersion", () ->
                                        getLastClassVersion(session)
                                    )
                                ),
                                post( () ->
                                    path("registerClassVersion", () ->
                                        registerClassVesrion(session)
                                    )
                                )
                             )
                         )
                    )),
                    path("logout", () ->
                            requiredSession(refreshable, sessionTransport, session ->
                                    logout(session)
                            )
                    )
                )
            ))

       );
    }

    private Route login() {
        return
             entity(Jackson.unmarshaller(User.class), user -> {
                    logger.info("Logging in " + user.getUsername());
                    return setSession(refreshable, sessionTransport, new VSession(user.getUsername()), () ->
                            setNewCsrfToken(checkHeader, () ->
                                    extractRequestContext(ctx ->
                                            onSuccess(() -> ctx.completeWith(HttpResponse.create()), routeResult ->
                                                    complete(successResponse())
                                            )
                                    )
                            )
                    );
             });
    }

    private Route logout(VSession session) {
        return
            invalidateSession(refreshable, sessionTransport, () ->
                extractRequestContext(ctx -> {
                    logger.info("User  " + session.getUsername() + " logged out ");
                    return onSuccess(() -> ctx.completeWith(HttpResponse.create()), routeResult ->
                        complete(successResponse())
                    );
                })
            );
    }

    private Route getLastClassVersion(VSession session) {
        logger.info("getClassVersion request received from user: " + session.getUsername() +
                    " session id: " + session.getSessionId());

        return
             entity(Jackson.unmarshaller(GetLastClassVersionData.class), getLastClassVersionData -> {
                 CompletionStage<GetLastClassVersionResult> getLastClassVersionResult = PatternsCS
                         .ask(manager, new GetLastClassVersion(mesageIdGenerator.getId(),getLastClassVersionData), timeout)
                         .thenApply(obj -> (GetLastClassVersionResult) obj);

                 return onSuccess(() -> getLastClassVersionResult,
                         version -> complete(StatusCodes.OK, version, Jackson.marshaller())
                 );

             });
    }

    private Route registerClassVesrion(VSession session){
        logger.info("getClassVersion request received from user: " + session.getUsername() +
                " session id: " + session.getSessionId());
        return
            entity(Jackson.unmarshaller(RegisterClassVersionData.class), registerClassVersionData -> {
            CompletionStage<RegisterClassVersionResult> registerClassVersionResult = PatternsCS
                    .ask(manager, new RegisterClassVersion(mesageIdGenerator.getId(),registerClassVersionData), timeout)
                    .thenApply(obj -> (RegisterClassVersionResult) obj);

            return onSuccess(() -> registerClassVersionResult,
                    version -> complete(StatusCodes.OK, version, Jackson.marshaller())
            );

        });
    }

    private String successResponse() {
        return "{\"result\" : \"success\" }";
    }

}
