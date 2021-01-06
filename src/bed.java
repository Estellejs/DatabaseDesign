public class bed {
    int ID;
    int area;
    int ward;
    int bed_index;
    int patient_ID;

    public bed(int ID,int area,int ward,int bed_index,int patient_ID){
        this.ID= ID;
        this.area= area;
        this.ward= ward;
        this.bed_index= bed_index;
        this.patient_ID= patient_ID;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getArea() {
        return area;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public void setPatient_ID(int patient_ID) {
        this.patient_ID = patient_ID;
    }

    public int getPatient_ID() {
        return patient_ID;
    }

    public int getBed_index() {
        return bed_index;
    }

    public int getWard() {
        return ward;
    }

    public void setBed_index(int bed_index) {
        this.bed_index = bed_index;
    }

    public void setWard(int ward) {
        this.ward = ward;
    }
}
