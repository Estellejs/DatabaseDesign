import java.util.ArrayList;

public class check_tools {
    public static boolean isPatientRecovery(){
        return true;
    }

    public static boolean is_area_has_space_nurse(int level){
        String bed_condition="where patient_ID=0 and area="+level;
        ArrayList<bed> beds=select_tools.getBed(bed_condition);
        String nurse_condition="where max_patient_num > actual_patient_num and area="+level;
        ArrayList<ward_nurse> ward_nurses=select_tools.get_ward_nurse(nurse_condition);
        if (beds.size()>0&&ward_nurses.size()>0){
            return true;
        }else {
            return false;
        }
    }


}
