package frontend.data;

import com.softwaremill.session.SessionSerializer;
import com.softwaremill.session.SingleValueSessionSerializer;
import com.softwaremill.session.javadsl.SessionSerializers;
import frontend.services.SessionIdGenerator;
import org.apache.log4j.Logger;
import scala.compat.java8.JFunction0;
import scala.compat.java8.JFunction1;
import scala.util.Try;

public class VSession {
    final static Logger logger = Logger.getLogger(VSession.class);

    /**
     * This session serializer converts a session type into a value (always a String type). The first two arguments are just conversion functions.
     * The third argument is a serializer responsible for preparing the data to be sent/received over the wire. There are some ready-to-use serializers available
     * in the com.softwaremill.session.SessionSerializer companion object, like stringToString and mapToString, just to name a few.
     */
   private static final SessionSerializer<VSession, String> serializer = new SingleValueSessionSerializer<>(
            (JFunction1<VSession, String>) (session) -> (session.getUsername())
            ,
            (JFunction1<String, Try<VSession>>) (login) -> Try.apply((JFunction0<VSession>) (() -> new VSession(login)))
            ,
            SessionSerializers.StringToStringSessionSerializer
    );

    private final String username;
    private final Integer sessionId;

    public VSession(String username) {
        this.username = username;
        this.sessionId = SessionIdGenerator.INSTANCE.getId();
        logger.info("Session with id: " + this.sessionId + " created");
    }

    public static SessionSerializer<VSession, String> getSerializer() {
        return serializer;
    }

    public String getUsername() {
        return username;
    }

    public Integer getSessionId() {
        return sessionId;
    }
}