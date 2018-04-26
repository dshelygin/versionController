package frontend.services.auth.authorizers;

import akka.actor.AbstractActor;

import frontend.services.auth.AuthManager.*;
import org.apache.log4j.Logger;

/**
 * тестовый авторизатор, пускает всех
 */
public class TestAuthorizer extends AbstractActor {
    final static Logger logger = Logger.getLogger(TestAuthorizer.class);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AuthRequestUser.class, message -> {
                    logger.info("AuthRequestUser request with id: " + message.getId() +
                            " received by TestAuthorizer: " + self().path().name());
                    getSender().tell(new AuthRequestUserResult(message.getId(),true),getSelf());
                })
                .matchAny(o -> logger.info("TestAuthorizer: " + self().path().name() + " received unknown message"))
                .build();
    }
}
