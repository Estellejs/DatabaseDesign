import java.sql.Connection;

public class Application {

    public static void main(String[] args){
        Connection conn = JDBCTool.getMySQLConn();
        System.out.println(conn);
        JDBCTool.releaseDB(null,null,conn);
    }
}
