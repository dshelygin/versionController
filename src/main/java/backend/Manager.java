package backend;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;

import backend.data.requests.GetLastClassVersionData;
import backend.data.requests.RegisterClassVersionData;
import backend.data.messages.MessageDescription;
import backend.storage.Register;
import org.apache.log4j.Logger;
import backend.data.messages.VersionMessages;

import java.util.HashMap;
import java.util.Random;

/**
 * Менеджер для сообщений, приходящих на бэкенд
 */
public class Manager  extends AbstractActor {
    private final static Logger logger = Logger.getLogger(Manager.class);
    private final static Integer START_REGISTER_NUBMER = 3;
    private Integer registerNumber = 0;
    //поддержка повторной пересылки сообщений.
    private HashMap<Integer,MessageDescription> requests = new HashMap<>();

    static public Props props() {
        return Props.create(Manager.class, () -> new Manager());
    }

    private  Manager(){
        for (int i = 0; i < START_REGISTER_NUBMER; i++) {
            getContext().actorOf(Props.create(Register.class), "Register_" + i);
            registerNumber++;
        }
    }

    static public class RegisterClassVersion extends VersionMessages {
        public final RegisterClassVersionData registerClassVersionData ;
        public RegisterClassVersion(Integer id, RegisterClassVersionData registerClassVersionData) {
            super(id);
            this.registerClassVersionData = registerClassVersionData;
        }
    }

    static public class RegisterClassVersionResult extends VersionMessages {
        public final Boolean result;
        public RegisterClassVersionResult(Integer id, Boolean result) {
            super(id);
            this.result = result;
        }
    }

    static public class GetLastClassVersion extends VersionMessages {
        private final GetLastClassVersionData getLastClassVersionData;
        public GetLastClassVersion(Integer id, GetLastClassVersionData getLastClassVersionData) {
            super(id);
            this.getLastClassVersionData = getLastClassVersionData;
        }
    }

    static public class GetLastClassVersionResult extends VersionMessages {
        public final Integer classVersion;
        public GetLastClassVersionResult(Integer id, Integer classVersion) {
            super(id);
            this.classVersion = classVersion;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RegisterClassVersion.class, message ->  forwardToRegister(message))
                .match(GetLastClassVersion.class, message ->   forwardToRegister(message))
                .match(RegisterClassVersionResult.class, message ->  receivedFromRegister(message))
                .match(GetLastClassVersionResult.class, message ->  receivedFromRegister(message))
                .matchAny(o -> logger.info("Manager received unknown message"))
                .build();
    }

    /*
    * выбирает актор для обработки запроса. На данный момент используется рандом, но можно реализовать, например,
    * проверку состояния, загруженность и.т.д.
    */
    private ActorSelection getRegister(){
        return getContext().actorSelection("Register_" + (new Random().nextInt(this.registerNumber)));
    }

    private void forwardToRegister(VersionMessages message ) {
        logger.info("sending to register message with id:" + message.getId() +" message type: " + message.getClass());
        requests.put(message.getId(),new MessageDescription(message, getSender()));
        getRegister().tell(message,getSelf());
    }

    private void receivedFromRegister(VersionMessages message) {
        logger.info("response for message with id:" + message.getId() + " received on Manager" +
                " message type: " + message.getClass());
        if (requests.get(message.getId()) != null ) {
            requests.get(message.getId()).getOrigin().tell(message, getSelf());
            requests.remove(message.getId());
        } else {
            logger.error("Manager received response for not registered request");
        }
    }



}
