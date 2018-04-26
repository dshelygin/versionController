package backend.data.requests;

import java.util.ArrayList;

/**
 * данные запроса на регистрацию версии
 */
public class RegisterClassVersionData {
    private String classKey;
    private String classCode;
    private String oparationCode;

    private ArrayList<Attribute> data = new ArrayList<>();

    public RegisterClassVersionData() {
    }

  /*  public RegisterClassVersionData(String classKey, String classCode, String oparationCode, ArrayList<Attribute> data){
        this.classKey = classKey;
        this.classCode = classCode;
        this.oparationCode = oparationCode;
        this.data = data;
    }
*/

    public String getClassKey() {
        return classKey;
    }

    public void setClassKey(String classKey) {
        this.classKey = classKey;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getOparationCode() {
        return oparationCode;
    }

    public void setOparationCode(String oparationCode) {
        this.oparationCode = oparationCode;
    }

    public ArrayList<Attribute> getData() {
        return data;
    }

    public void setData(ArrayList<Attribute> data) {
        this.data = data;
    }
}
