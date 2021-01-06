import java.sql.Connection;
import java.util.Scanner;

public class Application {

    public static void main(String[] args){
        Connection conn = JDBCTool.getMySQLConn();
        System.out.println(conn);
        JDBCTool.releaseDB(null,null,conn);

        Scanner scanner=new Scanner(System.in);

        while (true){
            System.out.println("请选择登录身份：1 主治医生；2 护士长；3 急诊护士；4 病房护士\n退出：q");
            String identity=scanner.next();
            boolean is_input_wrong=true;
            while (is_input_wrong) {
                is_input_wrong=false;
                String table_name="";
                switch (identity) {
                    case "1":
                        table_name="doctor";
                        Login.login(table_name);
                        break;
                    case "2":
                        table_name="chief_nurse";
                        Login.login(table_name);
                        break;
                    case "3":
                        table_name="emergency_nurse";
                        Login.login(table_name);
                        break;
                    case "4":
                        table_name="ward_nurse";
                        Login.login(table_name);
                        break;
                    case "q":
                        System.exit(0);
                    default:
                        is_input_wrong=true;
                        System.out.println("输入错误，请重新输入：1 主治医生；2 护士长；3 急诊护士；4 病房护士\n退出：q");
                }
            }
        }

    }
}
