import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class check_tools {
    public static ArrayList<patient> getPatientRecovery(int area){
        ArrayList<patient> patients=new ArrayList<>();
        String condition="where normal_temperature_num>=3 and normal_test_num>=2 and state=0 and level=1 and area="+area;
        patients=select_tools.get_patient_information(condition);
        return patients;
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
//增加护士，有病人出院或病故时，从其他区域转入，level
    public static void change_area(int level){


        if(is_area_has_space_nurse(level)){
            String bed_condition="where patient_ID=0 and area="+level;
            ArrayList<bed> beds=select_tools.getBed(bed_condition);
            String nurse_condition="where max_patient_num > actual_patient_num and area="+level;
            ArrayList<ward_nurse> ward_nursesl=select_tools.get_ward_nurse(nurse_condition);
            ArrayList<ward_nurse> ward_nurses=select_tools.get_ward_nurse(nurse_condition);

            for (int i=0;i<ward_nursesl.size();i++){
                for (int j=ward_nursesl.get(i).getActual_patient_num();j<ward_nursesl.get(i).getMax_patient_num();j++){
                    ward_nurses.add(ward_nursesl.get(i));
                }
            }
            System.out.println(ward_nurses.size()+"  "+ward_nursesl.size());
            //隔离区
            ArrayList<patient> patients_isolation=new ArrayList<>();
            ArrayList<patient> patients_level=new ArrayList<>();
            String condition="where area=4 and level="+level;
            patients_isolation=select_tools.get_patient_information(condition);
            condition="where area<4 and area!="+level+" and level="+level;
            patients_level=select_tools.get_patient_information(condition);
            int max_space=beds.size();
            if (max_space>ward_nurses.size()){
                max_space=ward_nurses.size();
            }


                if (max_space < patients_isolation.size()) {
                    for (int i = 0; i < max_space; i++) {
                        String SQL = "update patient set area=" + level + ",bed_ID=" + beds.get(i).getID() + ",nurse_ID=" +
                                ward_nurses.get(i).getID() + " where ID=" + patients_isolation.get(i).getID();
                        String bed_SQL = "update bed set patient_ID=" + patients_isolation.get(i).getID() + " where ID=" + beds.get(i).getID();
                        String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nurses.get(i).getActual_patient_num() + 1) + " where ID=" + ward_nurses.get(i).getID();
                        update_pnb(SQL,bed_SQL,SQL_nurse);

                    }
                } else if (max_space < (patients_isolation.size() + patients_level.size())) {
                    for (int i = 0; i < patients_isolation.size(); i++) {
                        String SQL = "update patient set area=" + level + ",bed_ID=" + beds.get(i).getID() + ",nurse_ID=" +
                                ward_nurses.get(i).getID() + " where ID=" + patients_isolation.get(i).getID();
                        String bed_SQL = "update bed set patient_ID=" + patients_isolation.get(i).getID() + " where ID=" + beds.get(i).getID();
                        String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nurses.get(i).getActual_patient_num() + 1) + " where ID=" + ward_nurses.get(i).getID();
                        update_pnb(SQL,bed_SQL,SQL_nurse);
                    }
                    for (int i=0;i<(max_space-patients_isolation.size());i++){
                        String SQL = "update patient set area=" + level + ",bed_ID=" + beds.get(i+patients_isolation.size()).getID() + ",nurse_ID=" +
                                ward_nurses.get(i+patients_isolation.size()).getID() + " where ID=" + patients_level.get(i).getID();
                        String bed_SQL = "update bed set patient_ID=" + patients_level.get(i).getID() + " where ID=" + beds.get(i+patients_isolation.size()).getID();
                        String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nurses.get(i+patients_isolation.size()).getActual_patient_num() + 1) + " where ID=" + ward_nurses.get(i+patients_isolation.size()).getID();
                        update_pnb(SQL,bed_SQL,SQL_nurse);
                    }
                } else {
                    for (int i = 0; i < patients_isolation.size(); i++) {
                        String SQL = "update patient set area=" + level + ",bed_ID=" + beds.get(i).getID() + ",nurse_ID=" +
                                ward_nurses.get(i).getID() + " where ID=" + patients_isolation.get(i).getID();
                        String bed_SQL = "update bed set patient_ID=" + patients_isolation.get(i).getID() + " where ID=" + beds.get(i).getID();
                        String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nurses.get(i).getActual_patient_num() + 1) + " where ID=" + ward_nurses.get(i).getID();
                        update_pnb(SQL,bed_SQL,SQL_nurse);
                    }
                    for (int i=0;i<patients_level.size();i++){
                        String SQL = "update patient set area=" + level + ",bed_ID=" + beds.get(i+patients_isolation.size()).getID() + ",nurse_ID=" +
                                ward_nurses.get(i+patients_isolation.size()).getID() + " where ID=" + patients_level.get(i).getID();
                        String bed_SQL = "update bed set patient_ID=" + patients_level.get(i).getID() + " where ID=" + beds.get(i+patients_isolation.size()).getID();
                        String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nurses.get(i+patients_isolation.size()).getActual_patient_num() + 1) + " where ID=" + ward_nurses.get(i+patients_isolation.size()).getID();
                        update_pnb(SQL,bed_SQL,SQL_nurse);
                    }

                }



        }
    }

    public static void update_pnb(String SQL,String bed_SQL,String SQL_nurse){
        Connection nurse_conn=null;
        Connection bed_conn=null;
        ResultSet nurse_rs=null;
        ResultSet bed_rs=null;
        PreparedStatement nurse_ps=null;
        PreparedStatement bed_ps=null;
        try {
            bed_conn = JDBCTool.getMySQLConn();
            bed_ps = bed_conn.prepareStatement(bed_SQL);
            bed_ps.executeUpdate();
            nurse_conn = JDBCTool.getMySQLConn();
            nurse_ps = nurse_conn.prepareStatement(SQL_nurse);
            nurse_ps.executeUpdate();
            update_tools.update(SQL);
            JDBCTool.releaseDB(bed_rs,bed_ps,bed_conn);
            JDBCTool.releaseDB(nurse_rs,nurse_ps,nurse_conn);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.toString());
        }

    }


}
