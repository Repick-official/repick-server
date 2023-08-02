package repick.repickserver.global.util;

public class formatPhoneNumber {
    public static String removeHyphens(String phoneNumber) {
        return phoneNumber.replace("-", "");
    }
}
