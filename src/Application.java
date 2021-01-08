import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
//        Connection conn = JDBCTool.getMySQLConn();
//        System.out.println(conn);
//        JDBCTool.releaseDB(null,null,conn);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("请选择登录身份：1 主治医生；2 护士长；3 急诊护士；4 病房护士\n退出：q");
            boolean is_input_wrong = true;
            while (is_input_wrong) {
                String identity = scanner.next();
                is_input_wrong = false;
                String table_name = "";
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
                        is_input_wrong = true;
                        System.out.println("输入错误，请重新输入：1 主治医生；2 护士长；3 急诊护士；4 病房护士\n退出：q");
                }
            }
        }

    }

    public static String getFiltrate(int area) {
        System.out.println("查看当前区域所有病人：all；");
        System.out.println("筛选条件：是否满足出院条件（是：1Y；否：1N）；是否待转入其他治疗区域（是：2Y；否：2N）；病人生命状态（康复出院：31；在院治疗：32；病亡：33）");
        boolean is_patient_operation_wrong = true;
        Scanner scanner = new Scanner(System.in);
        String condition = "";
        String patient_operation = scanner.next();
        while (is_patient_operation_wrong) {
            is_patient_operation_wrong = false;
            switch (patient_operation) {
                case "all":
                    condition = "where area=" + area + "";
                    break;
                case "1Y":
                    condition = "where normal_temperature_num=3 and normal_test_num=2 and level=1 and area=" + area + "";
                    break;
                case "1N":
                    condition = "where (normal_temperature_num !=3 or normal_test_num !=2 or level!=1) and area=" + area + "";
                    break;
                case "2Y":
                    condition = "where level!=area and area=" + area + "";
                    break;
                case "2N":
                    condition = "where level=area and area=" + area + "";
                    break;
                case "31":
                    condition = "where state=1 and area=" + area;
                    break;
                case "32":
                    condition = "where state=0 and area=" + area + "";
                    break;
                case "33":
                    condition = "where state=-1 and area=" + area;
                    break;
                default:
                    is_patient_operation_wrong = true;
                    System.out.println("输入错误，请重新输入。");
                    patient_operation = scanner.next();

            }
        }
        return condition;
    }

    public static void doctor_change_patient_information(doctor doctor, patient patient) {
        System.out.println("修改病情评级（轻症11；重症12；危重症13）；修改生命状态（在院治疗：22；病亡：23）；进行核酸检测；3");
        boolean is_input_wrong = true;
        Scanner scanner = new Scanner(System.in);
        String sql = "";
        String condition = "";
        String input = scanner.next();
        while (is_input_wrong) {
            is_input_wrong = false;
            switch (input) {
                case "11":
                    sql = "update patient set level=1 where ID=" + patient.getID();
                    update_tools.update(sql);
                    patient.setLevel(1);
                    if (check_tools.checkIfRecovery(patient)) {
                        System.out.println("病人已满足出院条件");
                    }
                    if (check_tools.is_area_has_space_nurse(1)) {
                        update_tools.change_area(patient, 1);
                        check_tools.change_area(patient.getArea());
                        System.out.println("病人已成功转区域");
                    } else {
                        System.out.println("没有空闲，转区域失败");
                    }
                    break;
                case "12":
                    sql = "update patient set level=2 where ID=" + patient.getID();
                    update_tools.update(sql);
                    if (check_tools.is_area_has_space_nurse(2)) {
                        update_tools.change_area(patient, 2);
                        check_tools.change_area(patient.getArea());
                        System.out.println("病人已成功转区域");
                    } else {
                        System.out.println("没有空闲，转区域失败");
                    }
                    break;
                case "13":
                    sql = "update patient set level=3 where ID=" + patient.getID();
                    update_tools.update(sql);
                    if (check_tools.is_area_has_space_nurse(3)) {
                        update_tools.change_area(patient, 3);
                        System.out.println("病人已成功转区域");
                        check_tools.change_area(patient.getArea());
                    } else {
                        System.out.println("没有空闲，转区域失败");
                    }
                    break;
//                case "21":
//                    sql="update patient set state=1 where ID="+patient.getID();
//                    update_tools.update(sql);
//                    break;
                case "22":
                    sql = "update patient set state=0 where ID=" + patient.getID();
                    update_tools.update(sql);
                    break;
                case "23":
                    sql = "update patient set state=-1, bed_ID=0,nurse_ID=0 where ID=" + patient.getID();
                    update_tools.update(sql);
                    //原先床位patient_ID置0
                    int old_bed_id = patient.getBed_ID();
                    condition = "where ID=" + old_bed_id;
                    ArrayList<bed> old_bed = select_tools.getBed(condition);
                    old_bed.get(0).setPatient_ID(0);
                    update_tools.update_bed(old_bed.get(0));
                    //原先的护士actual_patient_num减1
                    int old_nurse_id = patient.getNurse_ID();
                    condition = "where ID=" + old_nurse_id;
                    ArrayList<ward_nurse> old_nurse = select_tools.get_ward_nurse(condition);
                    old_nurse.get(0).setActual_patient_num(old_nurse.get(0).getActual_patient_num() - 1);
                    update_tools.update_nurse(old_nurse.get(0));
                    check_tools.change_area(patient.getArea());
                    break;
                case "3":
                    update_tools.insert_test(patient);
                    condition = "where ID=" + patient.getID();
                    patient = select_tools.get_patient_information(condition).get(0);
                    break;
                default:
                    is_input_wrong = true;
                    System.out.println("输入错误，请重新输入。");
                    input = scanner.next();
                    break;
            }
        }
    }

    public static void doctor() {
        Scanner scanner = new Scanner(System.in);
        String table_name = "doctor";
        int doctorID = Login.login(table_name);
        doctor doctor = new doctor(doctorID);
        while (true) {
            System.out.println("查看当前治疗区域的病人信息：patient；查看当前治疗区域的护士长及病房护士信息：nurse；决定病人是否出院：leave；修改个人信息：change；重新登陆：r；退出：q；");
            boolean is_operation_wrong = true;
            String operation = "";
            operation = scanner.next();
            int area = doctor.getArea();
            while (is_operation_wrong) {
                is_operation_wrong = false;
                switch (operation) {
                    case "patient":
                        ArrayList<patient> patients = new ArrayList<>();
                        String condition = "";
                        condition = getFiltrate(area);
                        patients = select_tools.get_patient_information(condition);
                        select_tools.print_patients(patients);

                        boolean is_change = true;
                        while (is_change) {
                            System.out.println("是否修改病人信息或进行核酸检测（是：Y，否：N）");
                            is_change = false;
                            operation = scanner.next();
                            switch (operation) {
                                case "Y":
                                    System.out.println("请输入病人ID：");
                                    while (true) {
                                        String patientID = scanner.next();
                                        condition = "where ID='" + patientID + "' and area='" + doctor.getArea() + "'";
                                        patients = select_tools.get_patient_information(condition);
                                        if (patients.size() > 0 && patients.get(0).getState() == 0) {
                                            doctor_change_patient_information(doctor, patients.get(0));
                                            break;
                                        } else if (patients.size() > 0 && patients.get(0).getState() == -1) {
                                            System.out.println("病人已去世，不能修改信息。请重新输入。");
                                        } else if (patients.size() > 0 && patients.get(0).getState() == 1) {
                                            System.out.println("病人已康复出院，不能修改信息。请重新输入。");
                                        } else {
                                            System.out.println("输入错误，请重新输入。");
                                        }
                                    }
                                case "N":
                                    break;
                                default:
                                    is_change = true;
                                    System.out.println("输入错误，请重新输入。");
                            }
                        }
                        break;
                    case "nurse":
                        System.out.println("查看护士长信息：1；查看病房护士信息：2；查看病房护士负责的病人：3");
                        String input = scanner.next();
                        boolean is_wrong = true;
                        while (is_wrong) {
                            is_wrong = false;
                            switch (input) {
                                case "1":
                                    String SQL = "select * from chief_nurse where area=" + doctor.getArea();
                                    chief_nurse chief_nurse = select_tools.getChief_nurse(SQL);
                                    System.out.println("ID:" + chief_nurse.ID + "  姓名:" + chief_nurse.name);
                                    break;
                                case "2":
                                    condition = "where area=" + doctor.getArea();
                                    ArrayList<ward_nurse> ward_nurses = select_tools.get_ward_nurse(condition);
                                    for (int i = 0; i < ward_nurses.size(); i++) {
                                        ward_nurse ward_nurse = ward_nurses.get(i);
                                        System.out.println("ID:" + ward_nurse.getID() + "  姓名：" + ward_nurse.getName() + "  负责的病人数量：" + ward_nurse.getActual_patient_num());
                                    }
                                    break;
                                case "3":
                                    boolean if_input_true = true;
                                    int ward_nurse_id = 0;
                                    while (if_input_true) {
                                        while (true) {
                                            try {
                                                System.out.println("请输入病房护士ID：");
                                                ward_nurse_id = Integer.parseInt(scanner.next());
                                                if_input_true = false;
                                            } catch (NumberFormatException e) {
                                                System.out.println("输入错误，请重新输入");
                                            }
                                            condition = "where nurse_ID=" + ward_nurse_id + " and area=" + area;
                                            patients = select_tools.get_patient_information(condition);
                                            if (patients.size() > 0) {
                                                select_tools.print_patients(patients);
                                                break;
                                            } else {
                                                System.out.println("ID不存在或不属于当前区域，请重新输入。");
                                            }
                                        }
                                    }
                                    break;
                                default:
                                    is_wrong = true;
                                    System.out.println("输入错误，请重新输入。");
                                    input = scanner.next();
                                    break;
                            }
                        }
                        break;
                    case "leave":
                        patients = check_tools.getPatientRecovery(doctor.getArea());
                        if (patients.size() == 0) {
                            System.out.println("没有可以出院的病人。");
                            break;
                        }
                        select_tools.print_patients(patients);
                        patient patient = new patient();
                        boolean is_patient_id_wrong = true;

                        while (is_patient_id_wrong) {
                            try {
                                System.out.println("请输入病人ID：");
                                int patient_id = Integer.parseInt(scanner.next());
                                for (int i = 0; i < patients.size(); i++) {
                                    if (patient_id == patients.get(i).getID()) {
                                        is_patient_id_wrong = false;
                                        patient = patients.get(i);
                                        break;
                                    }
                                }
                                if (is_patient_id_wrong) {
                                    System.out.println("输入错误，请重新输入。");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("输入错误，请重新输入。");
                            }
                        }
                        String SQL = "update patient set state=1,bed_ID=0,nurse_ID=0 where ID=" + patient.getID();

                        condition = "where ID=" + patient.getNurse_ID();
                        ArrayList<ward_nurse> ward_nurses = select_tools.get_ward_nurse(condition);
                        if (ward_nurses.size() > 0) {
                            String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nurses.get(0).getActual_patient_num() - 1) + " where ID=" + ward_nurses.get(0).getID();
                            update_tools.update(SQL_nurse);
                        }
                        condition = "where ID=" + patient.getBed_ID();
                        ArrayList<bed> beds = select_tools.getBed(condition);
                        if (beds.size() > 0) {
                            String SQL_bed = "update bed set patient_ID = 0 where ID=" + beds.get(0).getID();
                            update_tools.update(SQL_bed);
                        }
                        update_tools.update(SQL);
                        check_tools.change_area(patient.getArea());

                        break;
                    case "change":
                        update_tools.update_personal_information(table_name, doctorID);
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
            if (operation.equals("r")) {
                break;
            }
        }
    }

    public static void chief_nurse() {
        Scanner scanner = new Scanner(System.in);
        String table_name = "chief_nurse";
        int chief_nurse_id = Login.login(table_name);
        String SQL = "select * from chief_nurse where ID=" + chief_nurse_id;
        chief_nurse chief_nurse = select_tools.getChief_nurse(SQL);
        while (true) {
            System.out.println("以查看当前治疗区域的病人信息：patient；以查看当前治疗区域的病房护士信息：nurse；\n查看病床信息：bed；查看病床的病人信息：bed_patient；\n修改个人信息：change；重新登陆：r；退出：q；");
            boolean is_operation_wrong = true;
            String operation = "";
            operation = scanner.next();
            while (is_operation_wrong) {
                is_operation_wrong = false;
                switch (operation) {
                    case "patient":
                        ArrayList<patient> patients = new ArrayList<>();
                        String condition = "";
                        int area = chief_nurse.getArea();
                        condition = getFiltrate(area);
                        patients = select_tools.get_patient_information(condition);
                        select_tools.print_patients(patients);

                        break;
                    case "nurse":
                        System.out.println("查看病房护士信息：1；查看病房护士负责的病人：2；增加病房护士：3；删除病房护士：4");
                        String input = scanner.next();
                        boolean is_wrong = true;
                        while (is_wrong) {
                            is_wrong = false;
                            switch (input) {
                                case "1":
                                    condition = "where area=" + chief_nurse.getArea();
                                    ArrayList<ward_nurse> ward_nurses = select_tools.get_ward_nurse(condition);
                                    for (int i = 0; i < ward_nurses.size(); i++) {
                                        ward_nurse ward_nurse = ward_nurses.get(i);
                                        System.out.println("ID:" + ward_nurse.getID() + "  姓名：" + ward_nurse.getName() + "  负责的病人数量：" + ward_nurse.getActual_patient_num());
                                    }
                                    break;
                                case "2":
                                    boolean is_input_true = true;
                                    int ward_nurse_id = 0;
                                    while (is_input_true) {
                                        try {
                                            System.out.println("请输入病房护士ID：");
                                            ward_nurse_id = Integer.parseInt(scanner.next());
                                            is_input_true = false;
                                        } catch (NumberFormatException e) {
                                            System.out.println("输入错误，请重新输入。");
                                        }
                                    }
                                    condition = "where nurse_ID=" + ward_nurse_id;
                                    patients = select_tools.get_patient_information(condition);
                                    select_tools.print_patients(patients);
                                    break;
                                case "3":
                                    condition = "where area=0";
                                    ward_nurses = select_tools.get_ward_nurse(condition);
                                    ward_nurse ward_nurse = new ward_nurse();
                                    if (ward_nurses.size() > 0) {
                                        ward_nurse = chief_get_change_ward_nurse(condition, ward_nurses);
                                        ward_nurse.setArea(chief_nurse.getArea());
                                        switch (chief_nurse.getArea()) {
                                            case 1:
                                                ward_nurse.setMax_patient_num(3);
                                                break;
                                            case 2:
                                                ward_nurse.setMax_patient_num(2);
                                                break;
                                            case 3:
                                                ward_nurse.setMax_patient_num(1);
                                                break;
                                        }
                                        update_tools.update_nurse(ward_nurse);
                                        check_tools.change_area(chief_nurse.getArea());
                                    } else {
                                        System.out.println("没有符合条件的病房护士，无法增加");
                                    }
                                    break;
                                case "4":
                                    condition = "where area=" + chief_nurse.getArea();
                                    ward_nurses = select_tools.get_ward_nurse(condition);
                                    ward_nurse = new ward_nurse();
                                    if (ward_nurses.size() > 0) {
                                        ward_nurse = chief_get_change_ward_nurse(condition, ward_nurses);
                                        ward_nurse.setArea(0);
                                        ward_nurse.setActual_patient_num(0);
                                        ward_nurse.setMax_patient_num(0);
                                        ArrayList<bed> beds = new ArrayList<>();
                                        condition = "where nurse_ID=" + ward_nurse.getID();
                                        patients = select_tools.get_patient_information(condition);
                                        update_tools.update_nurse(ward_nurse);

                                        for (int i = 0; i < patients.size(); i++) {

                                            String nurse_condition = "where max_patient_num > actual_patient_num and area=" + chief_nurse.getArea();
                                            ArrayList<ward_nurse> ward_nursesl = select_tools.get_ward_nurse(nurse_condition);
                                            if (ward_nursesl.size() > 0) {
                                                String SQL_patients = "update patient set nurse_ID=" + ward_nursesl.get(0).getID() + " where ID=" + patients.get(i).getID();
                                                update_tools.update(SQL_patients);
                                                String SQL_nurse = "update ward_nurse set actual_patient_num=" + (ward_nursesl.get(0).getActual_patient_num() + 1) + " where ID=" + ward_nursesl.get(0).getID();
                                                update_tools.update(SQL_nurse);
                                            } else {
                                                String SQL_patients = "update patient set area=4,bed_ID=0,nurse_ID=0 where ID=" + patients.get(i).getID();
                                                condition = "where patient_ID=" + patients.get(i).getID();
                                                ArrayList<bed> beds1 = select_tools.getBed(condition);
                                                if (beds1.size() > 0) {
                                                    String SQL_beds = "update bed set patient_ID=0 where ID=" + beds1.get(0).getID();
                                                    update_tools.update(SQL_beds);
                                                }
                                                update_tools.update(SQL_patients);
                                                check_tools.change_area(chief_nurse.getArea());
                                            }
                                        }


                                    } else {
                                        System.out.println("没有符合条件的病房护士，无法删除");
                                    }
                                    break;
                                default:
                                    is_wrong = true;
                                    System.out.println("输入错误，请重新输入。");
                                    input = scanner.next();
                                    break;
                            }
                        }
                        break;
                    case "bed":
                        condition = "where area=" + chief_nurse.getArea();
                        ArrayList<bed> beds = select_tools.getBed(condition);
                        for (int i = 0; i < beds.size(); i++) {
                            bed bed = beds.get(i);
                            String strArea = "";
                            switch (bed.getArea()) {
                                case 1:
                                    strArea = "轻症区域";
                                    break;
                                case 2:
                                    strArea = "重症区域";
                                    break;
                                case 3:
                                    strArea = "危重症区域";
                                    break;
                            }
                            System.out.println("ID:" + bed.getID() + "  区域:" + strArea + "  病房号：" + bed.getWard() + "  病床号：" + bed.getBed_index() + "  病人ID：" + bed.getPatient_ID());
                        }
                        break;
                    case "bed_patient":
                        System.out.println("请输入病床ID：");

                        while (true) {
                            try {
                                int bed_id = Integer.parseInt(scanner.next());
                                condition = "where ID=" + bed_id + " and area='" + chief_nurse.getArea() + "'";
                                beds = select_tools.getBed(condition);
                                if (beds.size() > 0) {
                                    condition = "where ID=" + beds.get(0).getPatient_ID();
                                    patients = select_tools.get_patient_information(condition);
                                    select_tools.print_patients(patients);
                                    break;
                                } else {
                                    System.out.println("此病床ID不存在或不在该区域，请重新输入：");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("输入错误，请重新输入");
                            }
                        }
                        break;
                    case "change":
                        update_tools.update_personal_information(table_name, chief_nurse_id);
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
            if (operation.equals("r")) {
                break;
            }
        }
    }

    public static String getEmergencyFiltrate() {
        System.out.println("查看所有病人：all；");
        System.out.println("筛选条件：是否满足出院条件（是：1Y；否：1N）；是否待转入其他治疗区域（是：2Y；否：2N）；病人生命状态（康复出院：31；在院治疗：32；病亡：33）；治疗区域（轻症：41；重症：42；危重症：43；隔离区：44）");
        boolean is_patient_operation_wrong = true;
        Scanner scanner = new Scanner(System.in);
        String condition = "";
        String patient_operation = scanner.next();
        while (is_patient_operation_wrong) {
            is_patient_operation_wrong = false;
            switch (patient_operation) {
                case "all":
                    condition = "";
                    break;
                case "1Y":
                    condition = "where normal_temperature_num=3 and normal_test_num=2 and level=1 ";
                    break;
                case "1N":
                    condition = "where (normal_temperature_num !=3 or normal_test_num !=2 or level!=1)";
                    break;
                case "2Y":
                    condition = "where level!=area ";
                    break;
                case "2N":
                    condition = "where level=area ";
                    break;
                case "31":
                    condition = "where state=1 ";
                    break;
                case "32":
                    condition = "where state=0 ";
                    break;
                case "33":
                    condition = "where state=-1";
                    break;
                case "41":
                    condition = "where area=1";
                    break;
                case "42":
                    condition = "where area=2";
                    break;
                case "43":
                    condition = "where area=3";
                    break;
                case "44":
                    condition = "where area=4";
                    break;
                default:
                    is_patient_operation_wrong = true;
                    System.out.println("输入错误，请重新输入。");
                    patient_operation = scanner.next();

            }
        }
        return condition;
    }

    public static void emergency_nurse() {
        Scanner scanner = new Scanner(System.in);
        String table_name = "emergency_nurse";
        int emergency_nurse_id = Login.login(table_name);

        while (true) {
            System.out.println("查看病人信息：patient；登记病人信息：register；\n修改个人信息：change；重新登陆：r；退出：q；");
            boolean is_operation_wrong = true;
            String operation = "";
            operation = scanner.next();
            while (is_operation_wrong) {
                is_operation_wrong = false;
                switch (operation) {
                    case "patient":
                        ArrayList<patient> patients = new ArrayList<>();
                        String condition = "";
                        condition = getEmergencyFiltrate();
                        patients = select_tools.get_patient_information(condition);
                        select_tools.print_patients(patients);
                        break;
                    case "register":
                        System.out.println("请输入病人姓名：");
                        String name = scanner.next();
                        System.out.println("请输入病情等级：（轻症：1；重症：2；危重症：3）");
                        String in = scanner.next();
                        int level = -1;
                        boolean isWrong = true;
                        while (isWrong) {
                            isWrong = false;
                            switch (in) {
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
                                    isWrong = true;
                                    System.out.println("输入错误，请重新输入。");
                                    in = scanner.next();
                            }
                        }
                        update_tools.insert_patient(name, level);
                        break;
                    case "change":
                        update_tools.update_personal_information(table_name, emergency_nurse_id);
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
            if (operation.equals("r")) {
                break;
            }
        }

    }

    public static String getWarmFiltrate(int ward_nurse_id) {
        String condition = "";
        System.out.println("查看负责的所有病人：all；");
        System.out.println("筛选条件：是否满足出院条件（是：1Y；否：1N）；是否待转入其他治疗区域（是：2Y；否：2N）；病人生命状态（康复出院：31；在院治疗：32；病亡：33）");
        boolean is_patient_operation_wrong = true;
        Scanner scanner = new Scanner(System.in);
        String patient_operation = scanner.next();
        while (is_patient_operation_wrong) {
            is_patient_operation_wrong = false;
            switch (patient_operation) {
                case "all":
                    condition = "where nurse_ID=" + ward_nurse_id;
                    break;
                case "1Y":
                    condition = "where normal_temperature_num=3 and normal_test_num=2 and area=1 and nurse_ID=" + ward_nurse_id;
                    break;
                case "1N":
                    condition = "where (normal_temperature_num !=3 or normal_test_num !=2) and nurse_ID=" + ward_nurse_id;
                    break;
                case "2Y":
                    condition = "where level!=area and nurse_ID=" + ward_nurse_id;
                    break;
                case "2N":
                    condition = "where level=area and nurse_ID=" + ward_nurse_id;
                    break;
                case "31":
                    condition = "where state=1 and nurse_ID=" + ward_nurse_id;
                    break;
                case "32":
                    condition = "where state=0 and nurse_ID=" + ward_nurse_id;
                    break;
                case "33":
                    condition = "where state=-1 and nurse_ID=" + ward_nurse_id;
                    break;
                default:
                    is_patient_operation_wrong = true;
                    System.out.println("输入错误，请重新输入。");
                    patient_operation = scanner.next();

            }
        }
        return condition;
    }

    public static void ward_nurse() {
        String table_name = "ward_nurse";
        int ward_nurse_id = Login.login(table_name);
        Scanner scanner = new Scanner(System.in);
        String condition = "";

        while (true) {
            System.out.println("查看病人信息：patient；更新病人信息：update；\n修改个人信息：change；重新登陆：r；退出：q；");
            boolean is_operation_wrong = true;
            String operation = "";
            operation = scanner.next();
            while (is_operation_wrong) {
                is_operation_wrong = false;
                switch (operation) {
                    case "patient":
                        ArrayList<patient> patients = new ArrayList<>();
                        condition = getWarmFiltrate(ward_nurse_id);
                        patients = select_tools.get_patient_information(condition);
                        select_tools.print_patients(patients);
                        break;
                    case "update":
                        update_tools.update_record(ward_nurse_id);
                        break;
                    case "change":
                        update_tools.update_personal_information(table_name, ward_nurse_id);
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
            if (operation.equals("r")) {
                break;
            }
        }
    }

    public static ward_nurse chief_get_change_ward_nurse(String condition, ArrayList<ward_nurse> ward_nurses) {
        Scanner scanner = new Scanner(System.in);
        ward_nurse ward_nurse = new ward_nurse();
        for (int i = 0; i < ward_nurses.size(); i++) {
            ward_nurse = ward_nurses.get(i);
            System.out.println("ID:" + ward_nurse.getID() + "  姓名：" + ward_nurse.getName());
        }

        boolean is_id_wrong = true;
        while (is_id_wrong) {
            try {
                System.out.println("请输入病房护士ID：");
                int nurse_id = Integer.parseInt(scanner.next());
                for (int i = 0; i < ward_nurses.size(); i++) {
                    if (nurse_id == ward_nurses.get(i).getID()) {
                        ward_nurse = ward_nurses.get(i);
                        is_id_wrong = false;
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("输入错误，请重新输入。");
            }
            if (is_id_wrong) {
                System.out.println("输入错误，请重新输入。");
            }

        }
        return ward_nurse;
    }


}
