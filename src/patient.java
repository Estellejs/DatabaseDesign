import java.util.ArrayList;

public class patient {
    int ID;
    String name;
    int level;
    int area;
    int normal_temperature_num;
    int normal_test_num;
    int nurse_ID;
    int state;

    public void setArea(int area) {
        this.area = area;
    }

    public int getArea() {
        return area;
    }



    public int getID() {
        return ID;
    }


    public void setID(int ID) {
        this.ID = ID;
    }

    public int getLevel() {
        return level;
    }

    public int getNormal_temperature_num() {
        return normal_temperature_num;
    }

    public int getNormal_test_num() {
        return normal_test_num;
    }

    public int getNurse_ID() {
        return nurse_ID;
    }

    public String getName() {
        return name;
    }


    public void setLevel(int level) {
        this.level = level;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNormal_temperature_num(int normal_temperature_num) {
        this.normal_temperature_num = normal_temperature_num;
    }


    public void setNormal_test_num(int normal_test_num) {
        this.normal_test_num = normal_test_num;
    }

    public void setNurse_ID(int nurse_ID) {
        this.nurse_ID = nurse_ID;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public patient(int ID, String name, int level, int area,
                   int normal_temperature_num, int normal_test_num, int nurse_ID, int state) {
        this.ID = ID;
        this.name = name;
        this.level = level;
        this.area = area;
        this.state = state;
        this.normal_temperature_num = normal_temperature_num;
        this.normal_test_num = normal_test_num;
        this.nurse_ID = nurse_ID;
    }
    public patient(){}

}
