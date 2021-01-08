import java.sql.Date;

public class record {
    int ID;
    int patient_ID;
    int state;
    String test_result;
    float temperature;
    String symptom;
    Date date;

    public record(int ID,int patient_ID,int state,String test_result,float temperature,String symptom,Date date){
        this.ID= ID;
        this.patient_ID= patient_ID;
        this.state= state;
        this.test_result= test_result;
        this.temperature= temperature;
        this.symptom= symptom;
        this.date= date;
    }
    public record(){}


    public int getPatient_ID() {
        return patient_ID;
    }

    public void setPatient_ID(int patient_ID) {
        this.patient_ID = patient_ID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public String getSymptom() {
        return symptom;
    }

    public float getTemperature() {
        return temperature;
    }

    public String getTest_result() {
        return test_result;
    }

    public void setSymptom(String symptom) {
        this.symptom = symptom;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public void setTest_result(String test_result) {
        this.test_result = test_result;
    }
}
