package backend.data.requests;

/**
 * данные запроса на получение последней версии класса
 */
public class GetLastClassVersionData {
    private String className;

    public GetLastClassVersionData() {
    }

  /*  public GetLastClassVersionData(String className) {
        this.className = className;
    }
*/
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
