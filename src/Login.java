import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

class Login {

    static int login(String table_name){
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps=null;

        try {
            String name ="";
            String right_password = "";
            int id=-1;

            System.out.println("请输入用户名：");
            Scanner scanner = new Scanner(System.in);
            while (true) {
                name = scanner.next();
                right_password = "";
                String SQL = "select ID,password from " + table_name + " where name = '" + name+"' ";
                connection = JDBCTool.getMySQLConn();
                ps = connection.prepareStatement(SQL);
                rs = ps.executeQuery();
                while (rs.next()) {
                    id=rs.getInt(1);
                    right_password = rs.getString(2);
                }
                if (right_password.length()!=0){
                    break;
                }else {
                    System.out.println("用户名不存在，请重新输入：");
                }
            }
            System.out.println("请输入密码：");
            while (true){
                String password=scanner.next();
                if(password.equals(right_password)){
                    System.out.println("登陆成功");
                    return id;
                }else {
                    System.out.println("密码错误，请重新输入：");
                }
            }

        }catch (Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.toString());
        }

        return -1;
    }
}
