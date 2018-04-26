package backend.data.messages;


/**
 * предок всех сообщений, связанных с механизмом версионирования
 */
public class  VersionMessages {
    private final int id;

    public VersionMessages(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


}
