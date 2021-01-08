import java.sql.Date;
import java.sql.Timestamp;

public class test {
    int ID;
    int patient_ID;
    String result;
    Timestamp date;
    int current_level;
    public test(int ID, int patient_ID, String result, Timestamp date, int current_level){
        this.ID=ID;
        this.patient_ID=patient_ID;
        this.result=result;
        this.date=date;
        this.current_level=current_level;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public Timestamp getDate() {
        return date;
    }

    public int getCurrent_level() {
        return current_level;
    }

    public int getPatient_ID() {
        return patient_ID;
    }

    public String getResult() {
        return result;
    }

    public void setCurrent_level(int current_level) {
        this.current_level = current_level;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setPatient_ID(int patient_ID) {
        this.patient_ID = patient_ID;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
