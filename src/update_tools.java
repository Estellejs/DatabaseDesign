import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

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

    public static void update_bed(bed bed){
        String SQL="update bed set area='"+bed.getArea()+"' , ward="+bed.getWard()+
                ", bed_index="+bed.getBed_index()+", patient_ID="+bed.getPatient_ID()+" where ID="+bed.getID();
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

    public static void insert_test(patient patient){
        Connection connection = null;
        PreparedStatement ps=null;
        //随机生成核酸检测结果
        Random rand = new Random();
        int randnum = rand.nextInt(2);
        String result = "";
        if(randnum==0){
            result = "阳性";
            String tempSQL = "update patient set normal_test_num=0 where ID="+patient.getID();
            update(tempSQL);
            patient.setNormal_test_num(0);
        }
        else if(randnum==1){
            result = "阴性";
            String tempSQL = "update patient set normal_test_num="+ (patient.getNormal_test_num()+1) +" where ID="+patient.getID();
            update(tempSQL);
            patient.setNormal_test_num(patient.getNormal_test_num()+1);
        }
        Date date = new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        String sql = "insert into test(patient_ID,result,date,current_level) values(?,?,?,?)";
        try {
            connection = JDBCTool.getMySQLConn();
            ps=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,patient.getID());
            ps.setString(2,result);
            ps.setDate(3,sqlDate);
            ps.setInt(4,patient.getLevel());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JDBCTool.releaseDB(null,ps,connection);
        }
    }

    public static void update_personal_information(String table_name,int person_ID){
        Scanner scanner=new Scanner(System.in);
        String sql = "";
        boolean is_change = true;
        while (is_change){
            is_change = false;
            System.out.println("修改用户名：1，修改密码：2");
            String input = scanner.next();
            switch (input){
                case "1":
                   System.out.println("请输入新用户名：");
                   input = scanner.next();
                   sql = "update "+table_name+" set name='"+input+"' where ID="+person_ID;
                   update(sql);
                case "2":
                    System.out.println("请输入新密码：");
                    input = scanner.next();
                    sql = "update " + table_name + " set password='" + input + "' where ID=" + person_ID;
                    update(sql);
                default:
                    is_change = true;
                    System.out.println("输入错误，请重新输入。");
                    break;
            }
        }
    }

    public static void change_area(patient patient,int newArea){
        //原先床位patient_ID置0
        int old_bed_id = patient.getBed_ID();
        String condition = "where ID="+old_bed_id;
        ArrayList<bed> old_bed = select_tools.getBed(condition);
        old_bed.get(0).setPatient_ID(0);
        update_bed(old_bed.get(0));
        //原先的护士actual_patient_num减1
        int old_nurse_id = patient.getNurse_ID();
        condition = "where ID="+old_nurse_id;
        ArrayList<ward_nurse> old_nurse = select_tools.get_ward_nurse(condition);
        old_nurse.get(0).setActual_patient_num(old_nurse.get(0).getActual_patient_num()-1);
        update_nurse(old_nurse.get(0));
        //分配新的床位
        condition = "where area="+newArea+" and patient_ID=0";
        ArrayList<bed> new_beds = select_tools.getBed(condition);
        patient.setBed_ID(new_beds.get(0).getID());
        new_beds.get(0).setPatient_ID(patient.getID());
        String sql = "update patient set bed_ID="+new_beds.get(0).getID()+" where ID="+patient.getID();
        update(sql);
        update_bed(new_beds.get(0));
        //分配新的护士
        condition="where max_patient_num > actual_patient_num and area="+newArea;
        ArrayList<ward_nurse> new_nurses = select_tools.get_ward_nurse(condition);
        patient.setNurse_ID(new_nurses.get(0).getID());
        new_nurses.get(0).setActual_patient_num(new_nurses.get(0).getActual_patient_num()+1);
        sql = "update patient set nurse_ID="+new_nurses.get(0).getID()+" where ID="+patient.getID();
        update(sql);
        update_nurse(new_nurses.get(0));
    }
}
