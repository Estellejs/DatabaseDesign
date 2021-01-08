import com.sun.corba.se.impl.orb.ParserTable;

import java.sql.*;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class update_tools {

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
        String SQL="insert into patient(name,level,area,normal_temperature_num,normal_test_num,nurse_ID,state) values(?,?,?,0,0,?,0)";
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
                ps.setInt(4,ward_nurses.get(0).getID());
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
        String result = "";
        Scanner scanner = new Scanner(System.in);
        String input = "";
        boolean is_continue = true;
        Date date = new Date();
        Timestamp sqlDate = new Timestamp(date.getTime());
        while(is_continue){
            is_continue = false;
            System.out.println("请输入核酸检测结果，阳性：1；阴性：2");
            input = scanner.next();
            switch (input){
                case "1":
                    result = "阳性";
                    String tempSQL1 = "update patient set normal_test_num=0 where ID="+patient.getID();
                    update(tempSQL1);
                    patient.setNormal_test_num(0);
                    break;
                case "2":
                    result = "阴性";
                    //num等于0，直接＋1
                    if(patient.getNormal_test_num()==0){
                        String tempSQL2 = "update patient set normal_test_num=" + (patient.getNormal_test_num() + 1) + " where ID=" + patient.getID();
                        update(tempSQL2);
                        patient.setNormal_test_num(patient.getNormal_test_num() + 1);
                    }
                    //num等于1，要判断是否间隔24小时
                    else if(patient.getNormal_test_num()==1) {
                        ArrayList<test> tests = select_tools.getTest(patient.getID());
                        boolean if_add = false;
                        for(int i=0;i<tests.size();i++){
                            //从最后一次开始遍历
                            test tempTest = tests.get(tests.size()-1-i);
                            long time_space = sqlDate.getTime()-tempTest.getDate().getTime();
                            if(tempTest.getResult().equals("阳性"))
                                break;
                            if(time_space>=24*60*60*1000){
                                if_add = true;
                                break;
                            }
                        }
                        if(if_add){
                            String tempSQL2 = "update patient set normal_test_num=" + (patient.getNormal_test_num() + 1) + " where ID=" + patient.getID();
                            update(tempSQL2);
                            patient.setNormal_test_num(patient.getNormal_test_num() + 1);
                        }
                    }
                    //num大于等于2可以不变
                    break;
                default:
                    is_continue = true;
                    System.out.println("输入错误，请重新输入。");
            }
        }

        is_continue = true;
        int level = 0;
        while(is_continue){
            is_continue = false;
            System.out.println("请输入病情评级，轻症：1；重症：2；危重症：3");
            input = scanner.next();
            switch (input){
                case "1":
                    level = 1;
                    break;
                case "2":
                    level = 2;
                    break;
                case "3":
                    level = 3;
                    break;
                default:
                    is_continue = true;
                    System.out.println("输入错误，请重新输入。");
            }
        }

        String sql = "insert into test(patient_ID,result,date,current_level) values(?,?,?,?)";
        try {
            connection = JDBCTool.getMySQLConn();
            ps=connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,patient.getID());
            ps.setString(2,result);
            ps.setTimestamp(3,sqlDate);
            ps.setInt(4,level);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JDBCTool.releaseDB(null,ps,connection);
        }


        //若病情评级与原先不同，修改病人信息并尝试转区域
        int old_level = patient.getLevel();
        patient.setLevel(level);
        if (check_tools.checkIfRecovery(patient))
            System.out.println("病人已满足出院条件");
        if (old_level != level) {
            sql = "update patient set level=" + level + " where ID=" + patient.getID();
            update_tools.update(sql);
            if (check_tools.is_area_has_space_nurse(level)) {
                update_tools.change_area(patient, level);
                check_tools.change_area(patient.getArea());
                System.out.println("病人已成功转区域");
            } else {
                System.out.println("没有空闲，转区域失败");
            }
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
                   break;
                case "2":
                    System.out.println("请输入新密码：");
                    input = scanner.next();
                    sql = "update " + table_name + " set password='" + input + "' where ID=" + person_ID;
                    update(sql);
                    break;
                default:
                    is_change = true;
                    System.out.println("输入错误，请重新输入。");
                    break;
            }
        }
    }

    public static void change_area(patient patient,int newArea){
        //原先床位patient_ID置0

        String condition = "where patient_ID="+patient.getID();
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
        new_beds.get(0).setPatient_ID(patient.getID());
        update_bed(new_beds.get(0));
        //分配新的护士
        condition="where max_patient_num > actual_patient_num and area="+newArea;
        ArrayList<ward_nurse> new_nurses = select_tools.get_ward_nurse(condition);
        patient.setNurse_ID(new_nurses.get(0).getID());
        new_nurses.get(0).setActual_patient_num(new_nurses.get(0).getActual_patient_num()+1);
        String sql = "update patient set nurse_ID="+new_nurses.get(0).getID()+" where ID="+patient.getID();
        update(sql);
        update_nurse(new_nurses.get(0));
    }

    public static void update_record(int ward_nurse_id){
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        System.out.println("请输入病人ID：");
        Scanner scanner=new Scanner(System.in);
        String condition="";
        ArrayList<patient> patients=new ArrayList<>();
        patient patient = new patient();
        while (true) {
            try {
                int patient_id = Integer.parseInt(scanner.next());
                condition = "where ID=" + patient_id + " and nurse_ID=" + ward_nurse_id;
                patients = select_tools.get_patient_information(condition);
                if (patients.size() > 0) {
                    patient = patients.get(0);
                    break;
                } else {
                    System.out.println("ID不存在或不是您的病人，请重新输入");
                }
            } catch (NumberFormatException e) {
                System.out.println("输入错误，请重新输入");
            }
        }

        record new_record=new record();
        new_record.setPatient_ID(patient.getID());
        ArrayList<test> tests=select_tools.getTest(patient.getID());
        String result="无";
        if (tests.size()>0) {
            Date date1 = tests.get(0).getDate();
            result=tests.get(0).getResult();
            for (int j = 1; j < tests.size(); j++) {
                if (date1.compareTo(tests.get(j).getDate())<0){
                    date1=tests.get(j).getDate();
                    result=tests.get(j).getResult();
                }
            }
        }
        new_record.setTest_result(result);
        Date date=new Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        new_record.setDate(sqlDate);

        //温度
        System.out.println("请输入病人今天的体温：(例如：37.3)");
        String temperature = scanner.next();
        float patient_temperature = 0;
        while (true) {
            if (temperature.length() == 4) {
                if (Character.isDigit(temperature.charAt(0)) && Character.isDigit(temperature.charAt(1)) && Character.isDigit(temperature.charAt(3)) && temperature.charAt(2) == '.') {
                    patient_temperature=Float.parseFloat(temperature);
                    new_record.setTemperature(patient_temperature);
                    break;
                } else {
                    System.out.println("输入错误，请重新输入。");
                    temperature = scanner.next();
                }
            } else {
                System.out.println("输入错误，请重新输入。");
                temperature = scanner.next();
            }
        }
        int normal_temperature_num = patient.getNormal_temperature_num();
        if (temperature.compareTo("37.3") < 0) {
            normal_temperature_num += 1;
        } else {
            normal_temperature_num = 0;
        }
        patient.setNormal_temperature_num(normal_temperature_num);
        String SQL="update patient set normal_temperature_num="+normal_temperature_num+" where ID="+patient.getID();
        update_tools.update(SQL);
        //生命状态
        System.out.println("请输入病人今天的生命状态：（1康复住院；2在院治疗；3病亡）");
        String state = scanner.next();
        boolean is_input_wrong=true;
        while (is_input_wrong) {
            is_input_wrong=false;
            switch (state){
                case "1":
                    new_record.setState(1);
                    break;
                case "2":
                    new_record.setState(0);
                    break;
                case "3":
                    new_record.setState(-1);
                    break;
                default:
                    is_input_wrong=true;
                    System.out.println("输入错误，请重新输入");
                    state=scanner.next();
            }
        }
        //症状
        System.out.println("请输入病人症状：");
        String symptom=scanner.next();
        new_record.setSymptom(symptom);
        SQL="insert into record(patient_ID,state,temperature,symptom,test_result,date) values(?,?,?,?,?,?)";
        try {
            connection = JDBCTool.getMySQLConn();
            ps=connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1,patient.getID());
            ps.setInt(2,new_record.getState());
            ps.setFloat(3,new_record.getTemperature());
            ps.setString(4,new_record.getSymptom());
            ps.setString(5,new_record.getTest_result());
            ps.setDate(6,new_record.getDate());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JDBCTool.releaseDB(null,ps,connection);
        }
    }
}
