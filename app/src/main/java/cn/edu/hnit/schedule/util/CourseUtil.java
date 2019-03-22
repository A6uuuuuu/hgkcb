package cn.edu.hnit.schedule.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CourseUtil {

    private int currentWeek;
    private static final int OVER = 0;
    private static final int PROCESSING = 1;
    private static final int NOTSTART = 2;
    private static final int READY = 3;

    public CourseUtil(int currentWeek) {
        this.currentWeek = currentWeek;
    }

    //获取当前课程状态
    public int getStatus(String jc) {
        String[] time = jc.replace("节", "")
                .replace(" ", "")
                .split("-", 2);
        int[] currentTime = getCurrentTime();
        //根据节次得到开始和结束时间
        int startJc = Integer.valueOf(time[0]);
        int endJc = Integer.valueOf(time[1]);
        CourseTime courseTime = new CourseTime(startJc, endJc);
        int _currentTime = currentTime[0] * 60 + currentTime[1];
        int _start = courseTime.getStart()[0] * 60 + courseTime.getStart()[1];
        int _end = courseTime.getEnd()[0] * 60 + courseTime.getEnd()[1];
        //Log.d(TAG, "getStatus:" + "\n_start: " + _start + "\n_end: " + _end + "\ncurrentTime: " + _currentTime);
        if (_currentTime < _start) {
            if (_start - _currentTime <= 20) {
                return READY;
            }
            return NOTSTART;
        } else if (_currentTime < _end) {
            return PROCESSING;
        }
        return OVER;
    }

    //获取当前时间
    private int[] getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
        String[] timeArray = format.format(new Date()).split(":", 2);
        int[] time = new int[2];
        for (int i = 0; i < 2; i++) {
            time[i] = Integer.valueOf(timeArray[i]);
        }
        return time;
    }

    //判断是否在本周及本日
    public boolean inCurrentWeekDay(String[] time) {
        String weekday = time[0].substring(0, 1);
        boolean inCurrentWeekday = false;
        boolean inCurrentWeek = false;
        if (Integer.valueOf(weekday) == getWeekDay()) {
            inCurrentWeekday = true;
        }
        String[] weeks = time[1].split("\\)\\(", 2);
        weeks[0] = weeks[0].replace("(", "")
                .replace(")", "")
                .replace("周", "");
        Pattern r1 = Pattern.compile(",");
        Pattern r2 = Pattern.compile("-");
        Matcher m = r1.matcher(weeks[0]);
        int limit = 1;
        while (m.find()) {
            limit++;
        }
        String _weeks[] = weeks[0].split(",", limit);
        for (String week : _weeks) {
            m = r2.matcher(week);
            if (m.find()) {
                String[] range = week.split("-", 2);
                if (currentWeek >= Integer.parseInt(range[0]) & currentWeek <= Integer.parseInt(range[1])) {
                    inCurrentWeek = true;
                }
            } else if (Integer.parseInt(week) == currentWeek) {
                inCurrentWeek = true;
            }
        }
        return inCurrentWeek & inCurrentWeekday;
    }

    //获取星期
    private int getWeekDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }


    class CourseTime {
        /*
            上课时间：
            01  08:30 - 09:15
            02  09:25 - 10:05
            03  10:25 - 11:05
            04  11:05 - 12:00

            05  14:00 - 14:45
            06  14:55 - 15:40
            07  15:55 - 16:40
            08  16:50 - 17:35

            09  19:00 - 19:45
            10  19:55 - 20:40
            11  20:55 - 21:40
            12  21:50 - 22:35
        */
        private int start;
        private int end;
        private String[] timeArray = {
                "",
                "08:30-09:15", "09:25-10:05", "10:25-11:05", "11:05-12:00",
                "14:00-14:45", "14:55-15:40", "15:55-16:40", "16:50-17:35",
                "19:00-19:45", "19:55-20:40", "20:55-21:40", "21:50-22:35"};

        CourseTime(int start, int end) {
            this.start = start;
            this.end = end;
        }

        int[] getStart() {
            String[] _time = timeArray[start].split("-", 2)[0].split(":", 2);
            int[] time = new int[2];
            time[0] = Integer.valueOf(_time[0]);
            time[1] = Integer.valueOf(_time[1]);
            return time;
        }

        int[] getEnd() {
            String[] _time = timeArray[end].split("-", 2)[1].split(":", 2);
            int[] time = new int[2];
            time[0] = Integer.valueOf(_time[0]);
            time[1] = Integer.valueOf(_time[1]);
            return time;
        }

    }

}
