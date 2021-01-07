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

        if(is_area_has_space_nurse(level)) {
            System.out.println("需要转入");
            String bed_condition = "where patient_ID=0 and area=" + level;
            ArrayList<bed> beds = select_tools.getBed(bed_condition);
            String nurse_condition = "where max_patient_num > actual_patient_num and area=" + level;

            //有空闲的护士
            ArrayList<ward_nurse> ward_nurses = select_tools.get_ward_nurse(nurse_condition);

            //护士可以接收的病人数
            int nurse_num = 0;
            for(int i=0;i<ward_nurses.size();i++){
                nurse_num += ward_nurses.get(i).getMax_patient_num()-ward_nurses.get(i).getActual_patient_num();
            }

            //隔离区
            ArrayList<patient> patients_isolation = new ArrayList<>();     //在隔离区的病人数量
            ArrayList<patient> patients_level = new ArrayList<>();        //在其他区域的病人数量
            String condition = "where area=4 and level=" + level;
            patients_isolation = select_tools.get_patient_information(condition);
            condition = "where area<4 and area!=" + level + " and level=" + level;
            patients_level = select_tools.get_patient_information(condition);
            int max_space = beds.size();
            if (max_space > nurse_num) {
                max_space = nurse_num;
            }

            int currentNum = 0;
            label:
            for(int i=0;i<ward_nurses.size();i++){
                int tempNum = ward_nurses.get(i).getMax_patient_num()-ward_nurses.get(i).getActual_patient_num();
                for(int j=0;j<tempNum;j++){
                    if(currentNum==max_space)
                        break label;
                    else if(currentNum<patients_isolation.size()){
                        String SQL = "update patient set area=" + level + ",bed_ID=" + beds.get(currentNum).getID() + ",nurse_ID=" +
                                ward_nurses.get(i).getID() + " where ID=" + patients_isolation.get(currentNum).getID();
                        String bed_SQL = "update bed set patient_ID=" + patients_isolation.get(currentNum).getID() + " where ID=" + beds.get(currentNum).getID();
                        String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nurses.get(i).getActual_patient_num() + 1) + " where ID=" + ward_nurses.get(i).getID();
                        ward_nurses.get(i).setActual_patient_num(ward_nurses.get(i).getActual_patient_num()+1);
                        update_pnb(SQL, bed_SQL, SQL_nurse);
                        currentNum++;
                    }else {
                        System.out.println("level size:"+patients_level.size());
                        System.out.println("currentNum:"+currentNum);
                        System.out.println("isolation size:"+patients_isolation.size());
                        //把原先bed和nurse的patientID置0
                        int old_area = patients_level.get(currentNum-patients_isolation.size()).getArea();
                        int old_bed_id = patients_level.get(currentNum-patients_isolation.size()).getBed_ID();
                        String old_bed_SQL = "update bed set patient_ID=0 where ID="+old_bed_id;
                        update_tools.update(old_bed_SQL);
                        int old_nurse_id = patients_level.get(currentNum-patients_isolation.size()).getNurse_ID();
                        String old_nurse_condition = "where ID="+old_nurse_id;
                        ArrayList<ward_nurse> old_nurse = select_tools.get_ward_nurse(old_nurse_condition);
                        old_nurse.get(0).setActual_patient_num(old_nurse.get(0).getActual_patient_num()-1);
                        update_tools.update_nurse(old_nurse.get(0));

                        String SQL = "update patient set area=" + level + ",bed_ID=" + beds.get(currentNum).getID() + ",nurse_ID=" +
                                ward_nurses.get(i).getID() + " where ID=" + patients_level.get(currentNum-patients_isolation.size()).getID();
                        String bed_SQL = "update bed set patient_ID=" + patients_level.get(currentNum-patients_isolation.size()).getID() + " where ID=" + beds.get(currentNum).getID();
                        String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nurses.get(i).getActual_patient_num() + 1) + " where ID=" + ward_nurses.get(i).getID();
                        ward_nurses.get(i).setActual_patient_num(ward_nurses.get(i).getActual_patient_num() + 1);
                        update_pnb(SQL, bed_SQL, SQL_nurse);
                        currentNum++;
                        System.out.println("here");

                        change_area(old_area);
                    }

                }
            }

//
//            if (max_space < patients_isolation.size()) {
//                for (int i = 0; i < max_space; i++) {
//                    String SQL = "update patient set area=" + level + ",bed_ID=" + beds.get(i).getID() + ",nurse_ID=" +
//                            ward_nurses.get(i).getID() + " where ID=" + patients_isolation.get(i).getID();
//                    String bed_SQL = "update bed set patient_ID=" + patients_isolation.get(i).getID() + " where ID=" + beds.get(i).getID();
//                    String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nurses.get(i).getActual_patient_num() + 1) + " where ID=" + ward_nurses.get(i).getID();
//                    ward_nurses.get(i).setActual_patient_num(ward_nurses.get(i).getActual_patient_num()+1);
//                    update_pnb(SQL, bed_SQL, SQL_nurse);
//
//                }
//            } else if (max_space < (patients_isolation.size() + patients_level.size())) {
//                for (int i = 0; i < patients_isolation.size(); i++) {
//                    String SQL = "update patient set area=" + level + ",bed_ID=" + beds.get(i).getID() + ",nurse_ID=" +
//                            ward_nurses.get(i).getID() + " where ID=" + patients_isolation.get(i).getID();
//                    String bed_SQL = "update bed set patient_ID=" + patients_isolation.get(i).getID() + " where ID=" + beds.get(i).getID();
//                    String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nurses.get(i).getActual_patient_num() + 1) + " where ID=" + ward_nurses.get(i).getID();
//                    ward_nurses.get(i).setActual_patient_num(ward_nurses.get(i).getActual_patient_num()+1);
//                    update_pnb(SQL, bed_SQL, SQL_nurse);
//                }
//                for (int i = 0; i < (max_space - patients_isolation.size()); i++) {
//                    String SQL = "update patient set area=" + level + ",bed_ID=" + beds.get(i + patients_isolation.size()).getID() + ",nurse_ID=" +
//                            ward_nurses.get(i + patients_isolation.size()).getID() + " where ID=" + patients_level.get(i).getID();
//                    String bed_SQL = "update bed set patient_ID=" + patients_level.get(i).getID() + " where ID=" + beds.get(i + patients_isolation.size()).getID();
//                    String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nurses.get(i + patients_isolation.size()).getActual_patient_num() + 1) + " where ID=" + ward_nurses.get(i + patients_isolation.size()).getID();
//                    ward_nurses.get(i + patients_isolation.size()).setActual_patient_num(ward_nurses.get(i + patients_isolation.size()).getActual_patient_num() + 1);
//                    update_pnb(SQL, bed_SQL, SQL_nurse);
//                }
//            } else {
//                for (int i = 0; i < patients_isolation.size(); i++) {
//                    String SQL = "update patient set area=" + level + ",bed_ID=" + beds.get(i).getID() + ",nurse_ID=" +
//                            ward_nurses.get(i).getID() + " where ID=" + patients_isolation.get(i).getID();
//                    String bed_SQL = "update bed set patient_ID=" + patients_isolation.get(i).getID() + " where ID=" + beds.get(i).getID();
//                    String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nurses.get(i).getActual_patient_num() + 1) + " where ID=" + ward_nurses.get(i).getID();
//                    ward_nurses.get(i).setActual_patient_num(ward_nurses.get(i).getActual_patient_num()+1);
//                    update_pnb(SQL, bed_SQL, SQL_nurse);
//                }
//                for (int i = 0; i < patients_level.size(); i++) {
//                    String SQL = "update patient set area=" + level + ",bed_ID=" + beds.get(i + patients_isolation.size()).getID() + ",nurse_ID=" +
//                            ward_nurses.get(i + patients_isolation.size()).getID() + " where ID=" + patients_level.get(i).getID();
//                    String bed_SQL = "update bed set patient_ID=" + patients_level.get(i).getID() + " where ID=" + beds.get(i + patients_isolation.size()).getID();
//                    String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nurses.get(i + patients_isolation.size()).getActual_patient_num() + 1) + " where ID=" + ward_nurses.get(i + patients_isolation.size()).getID();
//                    ward_nurses.get(i + patients_isolation.size()).setActual_patient_num(ward_nurses.get(i + patients_isolation.size()).getActual_patient_num() + 1);
//                    update_pnb(SQL, bed_SQL, SQL_nurse);
//                }
//
//            }


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
    public static boolean checkIfRecovery(patient patient){
        if (patient.getNormal_temperature_num()>=3 && patient.getNormal_test_num()>=2 &&patient.getLevel()==1){
            return true;
        }else {
            return false;
        }
    }


}
