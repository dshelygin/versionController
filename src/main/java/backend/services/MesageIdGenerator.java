package backend.services;

/**
 * Created by dshelygin on 26.04.2018.
 */
public enum MesageIdGenerator {
    INSTANCE;
    int lastId = 1;

    public int getId() {
        if (lastId == Integer.MAX_VALUE) {
            lastId = 1;
        }
        return lastId++;
    }
}
