package backend.data.requests;

/**
 * Created by dshelygin on 25.04.2018.
 */
public class Attribute {
    private String value;
    private String type;
    private Boolean ifVersion;

    public Attribute() {
    }

    public Attribute(String value, String type, Boolean ifVersion) {
        this.value = value;
        this.type = type;
        this.ifVersion = ifVersion;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIfVersion() {
        return ifVersion;
    }

    public void setIfVersion(Boolean ifVersion) {
        this.ifVersion = ifVersion;
    }
}
