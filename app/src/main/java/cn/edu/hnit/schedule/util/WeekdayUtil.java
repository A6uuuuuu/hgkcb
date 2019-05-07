package cn.edu.hnit.schedule.util;
/*
    这个工具类用来将星期转为对应的数字
    也许有可以直接转换的方法吧，我不知道，不管了
 */
public class WeekdayUtil {

    public int toInt(String weekday) {
        int num = 7;
        switch (weekday) {
            case "星期一":
                num = 1;
                break;
            case "星期二":
                num = 2;
                break;
            case "星期三":
                num = 3;
                break;
            case "星期四":
                num = 4;
                break;
            case "星期五":
                num = 5;
                break;
            case "星期六":
                num = 6;
                break;
        }
        return num;
    }

    public int zhou2Int(String weekday) {
        int num = 7;
        switch (weekday) {
            case "周一":
                num = 1;
                break;
            case "周二":
                num = 2;
                break;
            case "周三":
                num = 3;
                break;
            case "周四":
                num = 4;
                break;
            case "周五":
                num = 5;
                break;
            case "周六":
                num = 6;
                break;
        }
        return num;
    }

}
