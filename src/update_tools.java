import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class update_tools {

    public static void update_temperature(patient new_patient){
        String temperature="";
        for (int i=0;i<new_patient.getTemperature().size();i++){
            temperature+=new_patient.getTemperature().get(i)+",";
        }
        temperature=temperature.substring(0,temperature.length()-1);
        String SQL="update patient set temperature='"+temperature+"' , normal_temperature_num="+new_patient.getNormal_temperature_num()+" where ID="+new_patient.getID();

        update(SQL);

    }

    public static void insert_patient(String name,int level){
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        Connection nurse_conn=null;
        Connection bed_conn=null;
        ResultSet nurse_rs=null;
        ResultSet bed_rs=null;
        PreparedStatement nurse_ps=null;
        PreparedStatement bed_ps=null;
        String SQL="insert into patient(name,level,area,bed_ID,normal_temperature_num,normal_test_num,nurse_ID,state) values(?,?,?,?,0,0,?,0)";
        try {
            connection= JDBCTool.getMySQLConn();
            ps=connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,name);
            ps.setInt(2,level);
            boolean is_area_has=check_tools.is_area_has_space_nurse(level);
            if (is_area_has){
                ps.setInt(3,level);
                String bed_condition="where patient_ID=0 and area="+level;
                ArrayList<bed> beds=select_tools.getBed(bed_condition);
                String nurse_condition="where max_patient_num > actual_patient_num and area="+level;
                ArrayList<ward_nurse> ward_nurses=select_tools.get_ward_nurse(nurse_condition);
                ps.setInt(4,beds.get(0).getID());
                ps.setInt(5,ward_nurses.get(0).getID());
                nurse_conn= JDBCTool.getMySQLConn();
                String SQL_nurse="update ward_nurse set actual_patient_num="+(ward_nurses.get(0).getActual_patient_num()+1)+" where ID="+ward_nurses.get(0).getID();
                nurse_ps=nurse_conn.prepareStatement(SQL_nurse);
                nurse_ps.executeUpdate();


                ps.executeUpdate();
                rs=ps.getGeneratedKeys();
                if (rs.next()){
                    int id=rs.getInt(1);
                    bed_conn=JDBCTool.getMySQLConn();
                    String bed_SQL="update bed set patient_ID="+id+" where ID="+beds.get(0).getID();
                    bed_ps=bed_conn.prepareStatement(bed_SQL);
                    bed_ps.executeUpdate();
                }
                JDBCTool.releaseDB(bed_rs,bed_ps,bed_conn);
                JDBCTool.releaseDB(nurse_rs,nurse_ps,nurse_conn);
            } else {
                ps.setInt(3,4);
                ps.setInt(4,0);
                ps.setInt(5,0);
                ps.executeUpdate();
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.toString());
        }
        JDBCTool.releaseDB(rs,ps,connection);



    }

    public static void update_nurse(ward_nurse ward_nurse){

        String SQL="update ward_nurse set area='"+ward_nurse.getArea()+"' , max_patient_num="+ward_nurse.getMax_patient_num()+
                ", actual_patient_num="+ward_nurse.getActual_patient_num()+" where ID="+ward_nurse.getID();
        update(SQL);
    }

    public static void update(String SQL){
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        try {
            connection = JDBCTool.getMySQLConn();
            ps = connection.prepareStatement(SQL);
            int result = ps.executeUpdate();
            if (result==1){
                System.out.println("更新成功！");
            }else {
                System.out.println("更新失败！");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.toString());
        }
        JDBCTool.releaseDB(rs,ps,connection);
    }


}
