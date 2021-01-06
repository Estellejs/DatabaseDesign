import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
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
                        doctor();
                        break;
                    case "2":
                        chief_nurse();
                        break;
                    case "3":
                        emergency_nurse();
                        break;
                    case "4":
                        ward_nurse();
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

    public static String getFiltrate(int area){
        System.out.println("查看当前区域所有病人：all；");
        System.out.println("筛选条件：是否满足出院条件（是：1Y；否：1N）；是否待转入其他治疗区域（是：2Y；否：2N）；病人生命状态（康复出院：31；在院治疗：32；病亡：33）");
        boolean is_patient_operation_wrong=true;
        Scanner scanner=new Scanner(System.in);
        String condition="";
        String patient_operation=scanner.next();
        while (is_patient_operation_wrong){
            is_patient_operation_wrong=false;
            switch (patient_operation){
                case "all":
                    condition="where area="+area+"";
                    break;
                case "1Y":
                    condition="where normal_temperature_num=3 and normal_test_num=2 and area=1 and area="+area+"";
                    break;
                case "1N":
                    condition="where (normal_temperature_num !=3 or normal_test_num !=2) and area="+area+"";
                    break;
                case "2Y":
                    condition="where level!=area and area="+area+"";
                    break;
                case "2N":
                    condition="where level=area and area="+area+"";
                    break;
                case "31":
                    condition="where state=1 and area="+area;
                    break;
                case "32":
                    condition="where state=0 and area="+area+"";
                    break;
                case "33":
                    condition="where state=-1 and area="+area;
                    break;
                default:
                    is_patient_operation_wrong=true;
                    System.out.println("输入错误，请重新输入。");
                    patient_operation=scanner.next();

            }
        }
        return condition;
    }
    public static void doctor_change_patient_information(doctor doctor,patient patient){
        System.out.println("修改病情评级（轻症11；重症12；危重症13）；修改生命状态（康复出院：21；在院治疗：22；病亡：23）；核酸检测；3");
        boolean is_input_wrong=true;
        Scanner scanner=new Scanner(System.in);
        String condition="";
        String input=scanner.next();
        while (is_input_wrong){
            is_input_wrong=false;
            switch (input){
                case "11":
                    condition="set level=1 where ID=";
                    break;
            }
        }
    }
    public static void doctor(){
        Scanner scanner=new Scanner(System.in);
        String table_name="doctor";
        int doctorID= Login.login(table_name);
        doctor doctor=new doctor(doctorID);
        while (true){
            System.out.println("以查看当前治疗区域的病人信息：patient；以查看当前治疗区域的护士长及病房护士信息：nurse；重新登陆：r；退出：q；");
            boolean is_operation_wrong=true;
            String operation="";
            operation = scanner.next();
            while (is_operation_wrong) {
                is_operation_wrong = false;
                switch (operation) {
                    case "patient":
                        ArrayList<patient>patients=new ArrayList<>();
                        String condition="";
                        int area=doctor.getArea();
                        condition=getFiltrate(area);
                        patients=public_tools.get_patient_information(condition);
                        public_tools.print_patients(patients);

                        System.out.println("请输入病人ID：");
                        String patientID=scanner.next();
                        condition="where ID='"+patientID+"' and area='"+doctor.getArea()+"'";
                        patients=public_tools.get_patient_information(condition);
                        doctor_change_patient_information(doctor,patients.get(0));

                        break;
                    case "nurse":
                        System.out.println("查看护士长信息：1；查看病房护士信息：2；查看病房护士负责的病人：3");
                        String input=scanner.next();
                        boolean is_wrong=true;
                        while (is_wrong){
                            is_wrong=false;
                            switch (input){
                                case "1":
                                    String SQL="select * from chief_nurse where area="+doctor.getArea();
                                    chief_nurse chief_nurse=public_tools.getChief_nurse(SQL);
                                    System.out.println("ID:"+chief_nurse.ID+"  姓名:"+chief_nurse.name);
                                    break;
                                case "2":
                                    condition="where area="+doctor.getArea();
                                    ArrayList<ward_nurse> ward_nurses=public_tools.get_ward_nurse(condition);
                                    for (int i=0;i<ward_nurses.size();i++){
                                        ward_nurse ward_nurse=ward_nurses.get(i);
                                        System.out.println("ID:"+ward_nurse.getID()+"  姓名："+ward_nurse.getName()+"  负责的病人数量："+ward_nurse.getActual_patient_num());
                                    }
                                    break;
                                case "3":
                                    System.out.println("请输入病房护士ID：");
                                    int ward_nurse_id=Integer.parseInt(scanner.next());
                                    condition="where nurse_ID="+ward_nurse_id;
                                    patients=public_tools.get_patient_information(condition);
                                    public_tools.print_patients(patients);
                                    break;
                                default:
                                    is_wrong=true;
                                    System.out.println("输入错误，请重新输入。");
                                    input = scanner.next();
                                    break;
                            }
                        }
                        break;
                    case "q":
                        System.exit(0);
                        break;
                    case "r":
                        break;
                    default:
                        is_operation_wrong = true;
                        System.out.println("输入错误，请重新输入。");
                        operation = scanner.next();
                        break;
                }
            }
            if (operation.equals("r")){
                break;
            }
        }
    }
    public static void chief_nurse(){
        Scanner scanner=new Scanner(System.in);
        String table_name="chief_nurse";
        int chief_nurse_id=Login.login(table_name);
        String SQL="select * from chief_nurse where ID="+chief_nurse_id;
        chief_nurse chief_nurse=public_tools.getChief_nurse(SQL);
        while (true){
            System.out.println("以查看当前治疗区域的病人信息：patient；以查看当前治疗区域的病房护士信息：nurse；\n查看病床信息：bed；查看病床的病人信息：bed_patient；重新登陆：r；退出：q；");
            boolean is_operation_wrong=true;
            String operation="";
            operation = scanner.next();
            while (is_operation_wrong) {
                is_operation_wrong = false;
                switch (operation) {
                    case "patient":
                        ArrayList<patient>patients=new ArrayList<>();
                        String condition="";
                        int area=chief_nurse.getArea();
                        condition=getFiltrate(area);
                        patients=public_tools.get_patient_information(condition);
                        public_tools.print_patients(patients);

                        break;
                    case "nurse":
                        System.out.println("查看病房护士信息：1；查看病房护士负责的病人：2；增删病房护士：3");
                        String input=scanner.next();
                        boolean is_wrong=true;
                        while (is_wrong){
                            is_wrong=false;
                            switch (input){
                                case "1":
                                    condition="where area="+chief_nurse.getArea();
                                    ArrayList<ward_nurse> ward_nurses=public_tools.get_ward_nurse(condition);
                                    for (int i=0;i<ward_nurses.size();i++){
                                        ward_nurse ward_nurse=ward_nurses.get(i);
                                        System.out.println("ID:"+ward_nurse.getID()+"  姓名："+ward_nurse.getName()+"  负责的病人数量："+ward_nurse.getActual_patient_num());
                                    }
                                    break;
                                case "2":
                                    System.out.println("请输入病房护士ID：");
                                    int ward_nurse_id=Integer.parseInt(scanner.next());
                                    condition="where nurse_ID="+ward_nurse_id;
                                    patients=public_tools.get_patient_information(condition);
                                    public_tools.print_patients(patients);
                                    break;
                                case "3":

                                    break;
                                default:
                                    is_wrong=true;
                                    System.out.println("输入错误，请重新输入。");
                                    input = scanner.next();
                                    break;
                            }
                        }
                        break;
                    case "bed":
                        condition="where area="+chief_nurse.getArea();
                        ArrayList<bed> beds=public_tools.getBed(condition);
                        for (int i=0;i<beds.size();i++){
                            bed bed=beds.get(i);
                            String strArea="";
                            switch (bed.getArea()){
                                case 1:
                                    strArea="轻症区域";
                                    break;
                                case 2:
                                    strArea="重症区域";
                                    break;
                                case 3:
                                    strArea="危重症区域";
                                    break;
                            }
                            System.out.println("ID:"+bed.getID() +"  区域:"+strArea+"  病房号："+bed.getWard()+"  病床号："+bed.getBed_index()+"  病人ID："+bed.getPatient_ID());
                        }
                        break;
                    case "bed_patient":
                        System.out.println("请输入病床ID：");
                        int bed_id=Integer.parseInt(scanner.next());
                        while (true){
                            condition="where ID="+bed_id;
                            beds=public_tools.getBed(condition);
                            if (beds.size()>0){
                                condition="where ID="+beds.get(0).getPatient_ID();
                                patients=public_tools.get_patient_information(condition);
                                public_tools.print_patients(patients);
                                break;
                            }else {
                                System.out.println("此病床ID不存在，请重新输入：");
                                bed_id=Integer.parseInt(scanner.next());
                            }
                        }
                        break;
                    case "q":
                        System.exit(0);
                        break;
                    case "r":
                        break;
                    default:
                        is_operation_wrong = true;
                        System.out.println("输入错误，请重新输入。");
                        operation = scanner.next();
                        break;
                }
            }
            if (operation.equals("r")){
                break;
            }
        }
    }

    public static String getEmergencyFiltrate(){
        System.out.println("查看所有病人：all；");
        System.out.println("筛选条件：是否满足出院条件（是：1Y；否：1N）；是否待转入其他治疗区域（是：2Y；否：2N）；病人生命状态（康复出院：31；在院治疗：32；病亡：33）；治疗区域（轻症：41；重症：42；危重症：43；隔离区：44）");
        boolean is_patient_operation_wrong=true;
        Scanner scanner=new Scanner(System.in);
        String condition="";
        String patient_operation=scanner.next();
        while (is_patient_operation_wrong){
            is_patient_operation_wrong=false;
            switch (patient_operation){
                case "all":
                    condition="";
                    break;
                case "1Y":
                    condition="where normal_temperature_num=3 and normal_test_num=2 and area=1 ";
                    break;
                case "1N":
                    condition="where (normal_temperature_num !=3 or normal_test_num !=2)";
                    break;
                case "2Y":
                    condition="where level!=area ";
                    break;
                case "2N":
                    condition="where level=area ";
                    break;
                case "31":
                    condition="where state=1 ";
                    break;
                case "32":
                    condition="where state=0 ";
                    break;
                case "33":
                    condition="where state=-1";
                    break;
                case "41":
                    condition="where area=1";
                    break;
                case "42":
                    condition="where area=2";
                    break;
                case "43":
                    condition="where area=3";
                    break;
                case "44":
                    condition="where area=4";
                    break;
                default:
                    is_patient_operation_wrong=true;
                    System.out.println("输入错误，请重新输入。");
                    patient_operation=scanner.next();

            }
        }
        return condition;
    }
    public static void emergency_nurse(){
        Scanner scanner=new Scanner(System.in);
        String table_name="emergency_nurse";
        int emergency_nurse_id=Login.login(table_name);

        while (true){
            System.out.println("查看病人信息：patient；登记病人信息：register；重新登陆：r；退出：q；");
            boolean is_operation_wrong=true;
            String operation="";
            operation = scanner.next();
            while (is_operation_wrong) {
                is_operation_wrong = false;
                switch (operation) {
                    case "patient":
                        ArrayList<patient>patients=new ArrayList<>();
                        String condition="";
                        condition=getEmergencyFiltrate();
                        patients=public_tools.get_patient_information(condition);
                        public_tools.print_patients(patients);
                        break;
                    case "register":
                        break;
                    case "q":
                        System.exit(0);
                        break;
                    case "r":
                        break;
                    default:
                        is_operation_wrong = true;
                        System.out.println("输入错误，请重新输入。");
                        operation = scanner.next();
                        break;
                }
            }
            if (operation.equals("r")){
                break;
            }
        }

    }


    public static String getWarmFiltrate(int ward_nurse_id){
        String condition="";
        System.out.println("查看负责的所有病人：all；");
        System.out.println("筛选条件：是否满足出院条件（是：1Y；否：1N）；是否待转入其他治疗区域（是：2Y；否：2N）；病人生命状态（康复出院：31；在院治疗：32；病亡：33）");
        boolean is_patient_operation_wrong=true;
        Scanner scanner=new Scanner(System.in);
        String patient_operation=scanner.next();
        while (is_patient_operation_wrong){
            is_patient_operation_wrong=false;
            switch (patient_operation){
                case "all":
                    condition="where nurse_ID="+ward_nurse_id;
                    break;
                case "1Y":
                    condition="where normal_temperature_num=3 and normal_test_num=2 and area=1 and nurse_ID="+ward_nurse_id;
                    break;
                case "1N":
                    condition="where (normal_temperature_num !=3 or normal_test_num !=2) and nurse_ID="+ward_nurse_id;
                    break;
                case "2Y":
                    condition="where level!=area and nurse_ID="+ward_nurse_id;
                    break;
                case "2N":
                    condition="where level=area and nurse_ID="+ward_nurse_id;
                    break;
                case "31":
                    condition="where state=1 and nurse_ID="+ward_nurse_id;
                    break;
                case "32":
                    condition="where state=0 and nurse_ID="+ward_nurse_id;
                    break;
                case "33":
                    condition="where state=-1 and nurse_ID="+ward_nurse_id;
                    break;
                default:
                    is_patient_operation_wrong=true;
                    System.out.println("输入错误，请重新输入。");
                    patient_operation=scanner.next();

            }
        }
        return condition;
    }

    public static void ward_nurse(){
        String table_name="ward_nurse";
        int ward_nurse_id=Login.login(table_name);
        Scanner scanner=new Scanner(System.in);
        String condition="";

        while (true){
            System.out.println("查看病人信息：patient；更新病人信息：update；重新登陆：r；退出：q；");
            boolean is_operation_wrong=true;
            String operation="";
            operation = scanner.next();
            while (is_operation_wrong) {
                is_operation_wrong = false;
                switch (operation) {
                    case "patient":
                        ArrayList<patient>patients=new ArrayList<>();
                        condition=getWarmFiltrate(ward_nurse_id);
                        patients=public_tools.get_patient_information(condition);
                        public_tools.print_patients(patients);
                        break;
                    case "update":
                        break;
                    case "q":
                        System.exit(0);
                        break;
                    case "r":
                        break;
                    default:
                        is_operation_wrong = true;
                        System.out.println("输入错误，请重新输入。");
                        operation = scanner.next();
                        break;
                }
            }
            if (operation.equals("r")){
                break;
            }
        }
    }


}
