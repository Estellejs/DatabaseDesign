public class doctor {
    int id;
    int area;
    String name;
    String password;

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public int getArea() {
        return area;
    }
    public void setArea(int area) {
        this.area=area;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public doctor(int id){
        this.id=id;
        this.area= select_tools.getArea(id,"doctor");
    }

}
