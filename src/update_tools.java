import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class update_tools {

    public static void update_temperature(patient new_patient){
        String temperature="";
        for (int i=0;i<new_patient.getTemperature().size();i++){
            temperature+=new_patient.getTemperature().get(i)+",";
        }
        temperature=temperature.substring(0,temperature.length()-1);
        String SQL="update patient set temperature='"+temperature+"' , normal_temperature_num="+new_patient.getNormal_temperature_num()+" where ID="+new_patient.getID();

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

    public static void insert_patient(String name,int level){
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps=null;
        String SQL="insert into patient(name,level,area,bed_ID,normal_temperature_num,normal_test_num,nurse_ID,state) values(?,?,?,?,0,0,?,0)";

        
    }
}
