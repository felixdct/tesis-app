package esime.authentication.utils;

/**
 * Created by DCMir on 20/11/17.
 */

public class Utils {
    public static String LOGIN_OP = "login";
    public static String ADD_OP = "add";
    public static String DELETE_OP = "delete";
    public static String CHANGE_PASSWD_OP = "changepasswd";

    public static String getOperation(String op) {
        String operation = "";
        switch (op){
            case "1":
                operation = LOGIN_OP;
                break;
            case "2":
                operation = ADD_OP;
                break;
            case "3":
                operation = DELETE_OP;
                break;
            case "4":
                operation = CHANGE_PASSWD_OP;
                break;
            default:
                operation = "";
                break;

        }
        return operation;
    }
}
