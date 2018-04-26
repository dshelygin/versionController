package backend.storage;

import akka.actor.AbstractActor;
import akka.actor.Props;
import backend.Manager;
import org.apache.log4j.Logger;

import static akka.japi.pf.UnitMatch.match;

/**
 * Created by dshelygin on 20.04.2018.
 */
public class Register extends AbstractActor {

    final static Logger logger = Logger.getLogger(Register.class);

    static public Props props() {
        return Props.create(Register.class, () -> new Register());
    }

    public Register() {
        logger.info("Register " + self().path().name() + " started (path: " +  self().path().toString() +  ")" );
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(Manager.GetLastClassVersion.class, message -> {
                logger.info("GetLastClassVersion request with id: " + message.getId() +
                        " received by Register: " + self().path().name());
                getSender().tell(new Manager.GetLastClassVersionResult(message.getId(),1),getSelf());
            })
            .match(Manager.RegisterClassVersion.class, message -> {
                logger.info("RegisterClassVersion request with id: " + message.getId() +
                        " received by Register: " + self().path().name());
                getSender().tell(new Manager.RegisterClassVersionResult(message.getId(),true), getSelf());
            })
            .matchAny(o -> logger.info("Register received unknown message"))
            .build();
    }
}
