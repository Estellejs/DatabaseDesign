import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class chief_nurse {
    int ID;
    int area;
    String name;
    String password;

    public chief_nurse(int ID,String name,String password,int area){
        this.ID=ID;
        this.area=area;
        this.name=name;
        this.password=password;
    }
    public chief_nurse(){}
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
}
