package frontend.services;

/**
 * генератор идентификатора сессии
 */

public enum SessionIdGenerator {
        INSTANCE;
        int lastId = 1;

        public int getId() {
            if (lastId == Integer.MAX_VALUE) {
                lastId = 1;
            }
            return lastId++;
        }
}

