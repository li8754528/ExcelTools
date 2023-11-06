package com.lyl.exceltools.utils;

/**
 * @author liyongliang
 * @date 2023/10/30 17:57
 */
public class StrUtil {
    public static final char UNDERLINE = '_';

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!isBlankChar(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c)
                || Character.isSpaceChar(c)
                || c == '\ufeff'
                || c == '\u202a'
                || c == '\u0000';
    }

    public static boolean isNumeric(String tempStr) {
        if (isBlank(tempStr)) {
            return false;
        }
        int length = tempStr.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(tempStr.charAt(i))) {
                return false;
            }
        }
        return false;
    }

    public static String upperFirst(String str) {
        if (null == str) {
            return null;
        }
        if (str.length() > 0) {
            char firstChar = str.charAt(0);
            if (Character.isLowerCase(firstChar)) {
                return Character.toUpperCase(firstChar) + str.substring(1);
            }
        }
        return str;
    }
}
