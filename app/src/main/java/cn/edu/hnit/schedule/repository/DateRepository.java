package cn.edu.hnit.schedule.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/*
    这个类用来计算一些即时数据
    比如日期
 */
public class DateRepository {

    private static final String TAG = "DateRepository";

    private Context context;

    public DateRepository(Context context) {
        this.context = context;
    }

    public int getWeekOfYear() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    //获取偏移周数
    private int getOffset() {
        if (context == null) {
            Log.d(TAG, "getOffset: context is null");
            return 0;
        } else {
            SharedPreferences pref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
            return pref.getInt("weekOffset", -7);
        }
    }

    public int getCurrentWeek() {
        return getOffset() + getWeekOfYear();
    }

}
