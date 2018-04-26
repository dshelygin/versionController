package frontend.services.auth;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.util.Timeout;
import backend.data.messages.MessageDescription;
import backend.data.messages.VersionMessages;
import frontend.data.User;
import frontend.services.auth.authorizers.TestAuthorizer;
import org.apache.log4j.Logger;
import scala.concurrent.duration.Duration;

import java.util.HashMap;

import java.util.concurrent.TimeUnit;


/**
 * Менеджер механизмов авторизации.
 */
public class AuthManager extends AbstractActor {
    final static Logger logger = Logger.getLogger(AuthManager.class);
    private HashMap<Integer,MessageDescription> requests = new HashMap<>();

    static public Props props() {
        return Props.create(AuthManager.class, () -> new AuthManager());
    }

    public AuthManager() {
        getContext().actorOf(Props.create(TestAuthorizer.class), "TestAuthorizer_1" );
    }

    public static class AuthRequestUser extends VersionMessages {
        private final User user;

        public AuthRequestUser(int id, User user) {
            super(id);
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    public static class AuthRequestUserResult extends VersionMessages {
        private final Boolean result;

        public AuthRequestUserResult(int id, Boolean result) {
            super(id);
            this.result = result;
        }

        public Boolean getResult() {
            return result;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AuthRequestUser.class, message -> {
                    forwardToAuthorizer(message);
                })
                .match(AuthRequestUserResult.class, message -> {
                    receivedFromAthorizer(message);
                })
                .matchAny(o -> logger.info("Manager received unknown message"))
                .build();
    }

    private void forwardToAuthorizer (AuthRequestUser message) {
        requests.put(message.getId(),new MessageDescription(message, getSender()));
        getAuthorizer().tell(message,getSelf());
    }

    private void receivedFromAthorizer(AuthRequestUserResult message) {
        if (requests.get(message.getId()) != null ) {
            requests.get(message.getId()).getOrigin().tell(message, getSelf());
            requests.remove(message.getId());
        } else {
            logger.error("AuthManager received response for not registered request");
        }
    }

    //выбираем метод авторизации
    private ActorSelection getAuthorizer(){
        return getContext().actorSelection("TestAuthorizer_1");
    }
}
