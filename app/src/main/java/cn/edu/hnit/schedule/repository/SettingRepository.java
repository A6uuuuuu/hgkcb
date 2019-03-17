package cn.edu.hnit.schedule.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingRepository {

    private Context context;
    private SharedPreferences pref;

    public SettingRepository(Context context) {
        this.context = context;
        this.pref = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    public boolean getSwitchOption(String option) {
        return pref.getBoolean(option, false);
    }

    public void setSwitchOption(String option, boolean bool) {
        SharedPreferences.Editor editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putBoolean(option, bool);
        editor.apply();
    }

    /*
        设置偏移周数
     */
    public void setWeekOffset(int offset) {
        SharedPreferences.Editor editor = context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putInt("weekOffset", offset);
        editor.apply();
    }

}
