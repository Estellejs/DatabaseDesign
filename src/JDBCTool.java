import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JDBCTool {

    public JDBCTool() {
    }

    //连接MySQL
    public static Connection getMySQLConn(){
        Connection conn = null;
        try {
            InputStream in = new BufferedInputStream(new FileInputStream("sql.properties"));
            Properties p = new Properties();
            p.load(in);
            String url = p.getProperty("url");
            String user = p.getProperty("user");
            String password = p.getProperty("password");
            Class.forName(p.getProperty("class"));   //加载数据库驱动
            conn = DriverManager.getConnection(url,user,password);
            System.out.println("连接成功");
        }
        //捕获异常信息
        catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    //释放数据库资源
    public static void releaseDB(ResultSet resultSet, PreparedStatement statement, Connection connection){
        if(resultSet != null){
            try{
                resultSet.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("释放成功");
    }

}
