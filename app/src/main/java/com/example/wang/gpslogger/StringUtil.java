package com.example.wang.gpslogger;

import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtil {

    /**
     * 提取出城市或者县
     *
     * @param city
     * @param district
     * @return
     */
    public static String extractLocation(final String city, final String district) {
        return district.contains("县") ? district.substring(0, district.length() - 1) : city.substring(0, city.length() - 1);
    }

    public static String getPhoneNumberWithHide(String phone) {
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static String geIdCardNumberWithHide(String IdCardNumber) {
        return IdCardNumber.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1*****$2");
    }

    public static String TimeStamp2YearMonth(long timestamp) {
        return new SimpleDateFormat("yyyy-MM").format(new Date(
                timestamp * 1000));
    }

    public static String TimeStamp2Day(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(
                timestamp * 1000));
    }

    public static String TimeStamp2DayDot(long timestamp) {
        return new SimpleDateFormat("yyyy.MM.dd").format(new Date(
                timestamp * 1000));
    }

    public static String TimeStamp2Date(long timestamp) {
        return new SimpleDateFormat("MM-dd HH:mm").format(new Date(
                timestamp * 1000));
    }

    public static String TimeStamp2Year(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(
                timestamp * 1000));
    }

    public static String TimeStamp2HourMin(long timestamp) {
        return new SimpleDateFormat("HH:mm").format(new Date(
                timestamp * 1000));
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.replace(" ", "").isEmpty();
    }

    public static boolean isPhoneNumber(String string) {
        if (string.length() != 11) {
            return false;
        }

        if (string.charAt(0) != '1') {
            return false;
        }

        for (int i = 0; i < string.length(); i++) {
            if (!Character.isDigit(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLoginPasswordOk(String password) {
        String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";
        return password.matches(regex);
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean isToday(String day) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = simpleDateFormat.parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为昨天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean isYesterday(String day) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = simpleDateFormat.parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 把日期换成时间戳
     */
    public static long string2time(String arg) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(arg);
        return date.getTime();
    }


    /**
     * 保留两位小数
     */
    public static String decimalFormat2(double money) {
        return new DecimalFormat("#0.00").format(money);
    }

    /**
     * 保留1位小数
     */
    public static String decimalFormat1(double money) {
        return new DecimalFormat("#0.0").format(money);
    }

    /**
     * 不保留小数
     *
     * @param floor
     * @return
     */
    public static String decimalFormat0Left0(double floor) {
        return new DecimalFormat("#0").format(floor);
    }

    /**
     * 不保留小数，小数点最后最多留一位
     * @param floor  1.00-->1  1.02-->1.0
     * @return
     */
    public static String decimalFormat0Lefgt1(double floor) {
        return new DecimalFormat("#0.#").format(floor);
    }

    /**
     * 保留两位小数点后面的数字
     *
     * @param money 1-->1.00
     * @return
     */
    public static String decimalFormat0LeftZero(Double money) {
        if (money == null) {
            money = 0.0;
        }
        return new DecimalFormat("#0.00").format(money);
    }

    /**
     * 非空
     */
    public static String notNull(String string) {
        if (string == null) {
            string = "";
        }
        return string;
    }

    /**
     * 没有小数点后面的数字，1.10--->1.0
     *
     * @param money
     * @return
     */
    public static String decimalFormat0Left22(Double money) {
        if (money == null) {
            money = 0.0;
        }
//        return new DecimalFormat("###################.##").format(money);
        return new DecimalFormat("#0.##").format(money);
    }


    public static String getFormedDistance(int m) {
        String distance = "";
        if (m >= 1000) {
            int distance1 = m / 100;
            int km = distance1 / 10;
            int littlekm = distance1 % 10;
            distance = km + "." + littlekm + "km";
        } else {
            distance = m + "m";
        }
        return distance;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isNullAndEmpty(String string) {
        if (string == null) return false;
        if (TextUtils.isEmpty(string)) return false;
        return true;
    }


}
