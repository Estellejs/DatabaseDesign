import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.sql.*;

public class select_tools {
    public static int getArea(int id,String table_name) {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        int area=-1;
        try {
            String SQL = "select area from "+table_name+" where ID = '" + id+"' ";
            connection = JDBCTool.getMySQLConn();
            ps = connection.prepareStatement(SQL);
            rs = ps.executeQuery();
            while (rs.next()) {
                area = rs.getInt(1);
                System.out.println(area);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.toString());
        }
        JDBCTool.releaseDB(rs,ps,connection);
        return area;
    }
    public static ArrayList<patient> get_patient_information(String filtrate){
        String SQL="";
        SQL="select * from patient "+filtrate;
        ArrayList<patient> patient_list= new ArrayList<>();
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        try {
            connection = JDBCTool.getMySQLConn();
            ps = connection.prepareStatement(SQL);
            rs = ps.executeQuery();
            while (rs.next()) {
                int ID=rs.getInt("ID");
                String name=rs.getString("name");
                int level=rs.getInt("level");
                int area=rs.getInt("area");
                int bed_ID=rs.getInt("bed_ID");
                String temperature=rs.getString("temperature");
                int normal_temperature_num=rs.getInt("normal_temperature_num");
                int normal_test_num=rs.getInt("normal_test_num");
                int nurse_ID=rs.getInt("nurse_ID");
                int state=rs.getInt("state");
                patient patient=new patient( ID,  name,  level,  area,  bed_ID,  temperature,
                 normal_temperature_num, normal_test_num,  nurse_ID,state);
                patient_list.add(patient);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.toString());
        }
        JDBCTool.releaseDB(rs,ps,connection);
        return patient_list;
    }
    public static void print_patients(ArrayList<patient> patients){
        if (patients.size()==0){
            System.out.println("空");
        }
        if (patients.size()>0){
            for (int i=0; i<patients.size();i++){
                patient patient=patients.get(i);
                String strArea="";
                switch (patient.getArea()){
                    case 1:
                        strArea="轻症区域";
                        break;
                    case 2:
                        strArea="重症区域";
                        break;
                    case 3:
                        strArea="危重症区域";
                        break;
                    case 4:
                        strArea="隔离区";
                        break;
                }
                String level="";
                switch (patient.getLevel()){
                    case 1:
                        level="轻症";
                        break;
                    case 2:
                        level="重症";
                        break;
                    case 3:
                        level="危重症";
                        break;
                }
                String state="";
                switch (patient.getState()){
                    case 1:
                        state="康复出院";
                        break;
                    case 0:
                        state="在院治疗";
                        break;
                    case -1:
                        state="病亡";
                        break;
                }
                ArrayList<test> tests=getTest(patient.getID());
                String result="";
                if (tests.size()>0) {
                    for (int j = 0; j < tests.size(); j++) {
                        result += tests.get(j).getDate() + tests.get(j).getResult() + ";";
                    }
                }
                System.out.println(patient.getID()+"  病情评级："+level+"  区域："+strArea+"  温度："+patient.getTemperature()+"  生命状态："+state+"  核酸检测结果:"+result);
            }
        }
    }
    public static ArrayList<test> getTest(int patientID){
        ArrayList<test> tests=new ArrayList<>();
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        String SQL="select * from test where patient_ID="+patientID;
        try {
            connection = JDBCTool.getMySQLConn();
            ps = connection.prepareStatement(SQL);
            rs = ps.executeQuery();
            while (rs.next()) {
                int ID=rs.getInt("ID");
                String result=rs.getString("result");
                Date date=rs.getDate("date");
                int current_level=rs.getInt("current_level");
                test test=new test(ID,patientID,result,date,current_level);
                tests.add(test);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.toString());
        }
        JDBCTool.releaseDB(rs,ps,connection);
        return tests;
    }
    public static ArrayList<ward_nurse> get_ward_nurse(String condition){
        String SQL="select * from ward_nurse "+condition;
        ArrayList<ward_nurse> ward_nurses= new ArrayList<>();
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        try {
            connection = JDBCTool.getMySQLConn();
            ps = connection.prepareStatement(SQL);
            rs = ps.executeQuery();
            while (rs.next()) {
                int ID=rs.getInt("ID");
                int area=rs.getInt("area");
                String name=rs.getString("name");
                String password=rs.getString("password");
                int max_patient_num=rs.getInt("max_patient_num");
                int actual_patient_num=rs.getInt("actual_patient_num");
                ward_nurse ward_nurse=new ward_nurse( ID, name, password, area,  max_patient_num, actual_patient_num);
                ward_nurses.add(ward_nurse);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.toString());
        }
        JDBCTool.releaseDB(rs,ps,connection);
        return ward_nurses;
    }
    public static chief_nurse getChief_nurse(String SQL){
        chief_nurse chief_nurse=new chief_nurse();
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        try {
            connection = JDBCTool.getMySQLConn();
            ps = connection.prepareStatement(SQL);
            rs = ps.executeQuery();
            while (rs.next()) {
                int ID=rs.getInt("ID");
                int area=rs.getInt("area");
                String name=rs.getString("name");
                String password=rs.getString("password");
                chief_nurse=new chief_nurse(ID,name,password,area);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.toString());
        }
        JDBCTool.releaseDB(rs,ps,connection);
        return chief_nurse;
    }
    public static ArrayList<bed> getBed(String condition){
        ArrayList<bed> beds=new ArrayList<>();
        String SQL="select * from bed "+condition;
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        try {
            connection = JDBCTool.getMySQLConn();
            ps = connection.prepareStatement(SQL);
            rs = ps.executeQuery();
            while (rs.next()) {
                int ID=rs.getInt("ID");
                int area =rs.getInt("area");
                int ward=rs.getInt("ward");
                int bed_index=rs.getInt("bed_index");
                int patient_ID=rs.getInt("patient_ID");
                bed bed=new bed(ID,area,ward,bed_index,patient_ID);
                beds.add(bed);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.toString());
        }
        JDBCTool.releaseDB(rs,ps,connection);
        return beds;
    }
}
