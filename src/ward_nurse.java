import java.util.ArrayList;

public class ward_nurse {

    int ID;
    int area;
    String name;
    String password;
    int max_patient_num;
    int actual_patient_num;

    public ward_nurse(int ID,String name,String password,int area, int max_patient_num,int actual_patient_num){
        this.ID=ID;
        this.area=area;
        this.name=name;
        this.password=password;
        this.max_patient_num=max_patient_num;
        this.actual_patient_num=actual_patient_num;
    }
    public ward_nurse(){}
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getArea() {
        return area;
    }

    public String getName() {
        return name;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getActual_patient_num() {
        return actual_patient_num;
    }

    public int getMax_patient_num() {
        return max_patient_num;
    }

    public void setActual_patient_num(int actual_patient_num) {
        this.actual_patient_num = actual_patient_num;
    }

    public void setMax_patient_num(int max_patient_num) {
        this.max_patient_num = max_patient_num;
    }

}
