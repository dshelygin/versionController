package backend.data.messages;

import akka.actor.ActorRef;
import backend.data.messages.VersionMessages;

/**
 * Created by dshelygin on 20.04.2018.
 */
public class MessageDescription {
    private final VersionMessages message;
    private final Long created;
    private final ActorRef origin;

    public MessageDescription(VersionMessages message, ActorRef origin) {
        this.message = message;
        this.origin = origin;
        this.created = (new java.util.Date()).getTime();
    }

    public VersionMessages getMessage() {
        return message;
    }

    public Long getCreated() {
        return created;
    }

    public ActorRef getOrigin() {
        return origin;
    }
}
