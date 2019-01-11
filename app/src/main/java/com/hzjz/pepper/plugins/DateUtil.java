package com.hzjz.pepper.plugins;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DateUtil {
    /**
     * 描述：Date类型转化为String类型.
     *
     * @param date   the dateo
     * @param format 类型如 yyyy-MM-dd HH:mm:ss
     */
    public static String getStringByFormat(Date date, String format) {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
        String strDate = null;
        try {
            strDate = mSimpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    public static String getStringByFormatString(String dd, String fromfmt, String tofmt) {
        SimpleDateFormat paramtime = new SimpleDateFormat(fromfmt);
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(tofmt);
        String strDate = null;
        try {
            strDate = mSimpleDateFormat.format(paramtime.parse(dd));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * 获取当前时间
     * pattern 类型 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentDate(String pattern) {

        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String timestamp = formatter.format(curDate);

        return timestamp;
    }

    /**
     * 获取当前年
     */
    public static int getCurrentYear() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR);
    }

    /**
     * 获取当前月
     */
    public static int getCurrentMonth() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前日
     */
    public static int getCurrentDay() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前时
     */
    public static int getCurrentHour() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前分
     */
    public static int getCurrentMinutes() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.MINUTE);
    }

    /**
     * 获取当前秒
     */
    public static int getCurrentSeconds() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.SECOND);
    }

    /**
     * 取得当月天数
     */
    public static int getCurrentMonthLastDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 得到指定月的天数
     */
    public static int getMonthLastDay(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 获取当前日期的day天后的日期
     *
     * @param pattern 格式类型
     * @param day     天数
     * @return
     */
    public static String getNewData(String pattern, int day) {
        Format format = new SimpleDateFormat(pattern);
        long time = 0;
        Date today = new Date();
        time = (today.getTime() / 1000) + 60 * 60 * 24 * day;
        Date newDate = new Date();
        newDate.setTime(time * 1000);
        String date = format.format(newDate);
        return date;
    }

    /**
     * @param DATE1 判断日期是否超过当前日期  超过当前日期为true
     * @return
     */
    public static boolean compare_date(String DATE1) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = new Date();
            if (dt1.getTime() > dt2.getTime()) {
                return true;
            } else if (dt1.getTime() <= dt2.getTime()) {
                return false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * 描述：比较两个日期的大小
     * old 表示之前的时间
     * now 现在的时间
     *
     * @return
     */
    public static String DateCompare(String old, String now) {
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Calendar c1 = java.util.Calendar.getInstance();
        java.util.Calendar c2 = java.util.Calendar.getInstance();
        try {
            c1.setTime(df.parse(old));
            c2.setTime(df.parse(now));
        } catch (Exception e) {
            System.out.println("格式不正确");
            e.printStackTrace();
        }
        int result = c1.compareTo(c2);
        if (result == 0) {
            return "相等";
        } else if (result < 0) {
            return "小于";
        } else
            return "大于";
    }

    /**
     * 描述
     * 根据出生日期获得年龄
     * data 输入的生日 pattern 转换的类型 如：yyyyMM-dd
     *
     * @return
     */
    public static String getAge(String data, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        Date curDate = new Date();//
        String timestamp = formatter.format(curDate);
        String brith = data.substring(0, 4);
        String now = timestamp.substring(0, 4);
        int age = Integer.valueOf(now) - Integer.valueOf(brith);
        return age + "岁";
    }

    /**
     * 根据String 类型的日期判断是星期几？？
     *
     * @param date   String 类型的日期
     * @param format 格式类型
     * @return
     */
    public static String getWeekOfDate(String date, String format) {
        String[] dayofweek = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四",
                "星期五", "星期六"};
        Calendar c = Calendar.getInstance();
        try {
            c.setTime((new SimpleDateFormat(format)).parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayofweek[c.get(Calendar.DAY_OF_WEEK) - 1];
    }

    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        try {
            date = formatter.parse(strTime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 项目中用的这个方法比较仿扣扣评论发送时间
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static String twoDateDistance(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            return null;
        } else {
            long timeLong = endDate.getTime() - startDate.getTime();
            if (timeLong < 60 * 1000) {
                return "刚刚";

            } else if (timeLong < 60 * 60 * 1000) {
                timeLong = timeLong / 1000 / 60;
                return timeLong + "分钟前";

            } else if (timeLong < 60 * 60 * 24 * 1000) {
                timeLong = timeLong / 60 / 60 / 1000;
                return timeLong + "小时前";

            } else if (timeLong < 60 * 60 * 24 * 1000 * 7) {
                timeLong = timeLong / 1000 / 60 / 60 / 24;
                return timeLong + "天前";
            } else if (timeLong < 60 * 60 * 24 * 1000 * 7 * 4) {
                timeLong = timeLong / 1000 / 60 / 60 / 24 / 7;
                return timeLong + "周前";

            } else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                return format.format(startDate);
            }
        }
    }

    /**
     * @param mesg 根据生份证号获取生日
     * @return
     */
    public static String SetbirthDay(String mesg) {
        String strYear = "";
        String strMonth = "";
        String strDay = "";
        if (mesg != null && mesg != "") {
            strYear = mesg.substring(6, 10);
            strMonth = mesg.substring(10, 12);
            strDay = mesg.substring(12, 14);
        }
        return strYear + "-" + strMonth + "-" + strDay;
    }

    /**
     * @param mesg 根据省份证号获取性别
     * @return
     */

    public static String setSex(String mesg) {
        String sex = "";
        if (mesg != null && mesg != "") {
            sex = mesg.substring(16, 17);
        }
        if (Integer.parseInt(sex) % 2 == 0) {
            sex = "女";
        } else {
            sex = "男";
        }
        return sex;
    }

    /**
     * 根据传入的count数，来获取count天后的日期集合
     *
     * @param count 表示要获取多少天数
     * @param flag  默认false
     * @return
     */
    public static List<String> getDateTimeStr(int count, boolean flag) {
        List<String> timestr = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            String dateStr = getDateStr(i + 1, flag);
            timestr.add(dateStr);
        }
        return timestr;
    }

    public static String getDateStr(int mon, boolean flag) {
        String time = "";
        if (flag) {
            if (mon % 2 == 1) {
                time = "01 ";
                mon = mon / 2 + 1;
            } else if (mon % 2 == 0) {
                time = "02 ";
                mon = mon / 2;
            }
        }
        Calendar cal = Calendar.getInstance();
        int _year = cal.get(Calendar.YEAR);
        int _month = cal.get(Calendar.MONTH) + 1;
        int _day = cal.get(Calendar.DAY_OF_MONTH) + mon;
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if (hour > 23) {
            _day = _day + 1;
        }
        int lastday = getLastDay();
        if (_day > lastday) {
            _month = _month + 1;
            _day = _day - lastday;
        }
        String month = "";
        String day = "";
        if (_month < 10) {
            month = "0" + _month;
        } else {
            month = _month + "";
        }
        if (_day < 10) {
            day = "0" + _day;
        } else {
            day = _day + "";
        }
        if (_month > 12) {
            month = "01";
            _year = _year + 1;
        }
        time = time + _year + "-" + month + "-" + day;
        return time;
    }

    public static int getLastDay() {
        int lastday;
        Calendar d = Calendar.getInstance();
        int month = d.get(Calendar.MONTH);
        do {
            lastday = d.get(Calendar.DAY_OF_MONTH);
            d.add(Calendar.DAY_OF_MONTH, 1);
        } while (d.get(Calendar.MONTH) == month);
        return lastday;
    }
    public static String getCurrentTime(){
        SimpleDateFormat formatter  =  new  SimpleDateFormat  ("yyyy-MM-dd  HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }
}
