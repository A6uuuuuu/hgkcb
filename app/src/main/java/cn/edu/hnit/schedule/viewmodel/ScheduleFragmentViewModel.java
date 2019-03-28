package cn.edu.hnit.schedule.viewmodel;

import android.arch.lifecycle.ViewModel;

public class ScheduleFragmentViewModel extends ViewModel {

    private int week;

    public void setWeek(int week) {
        this.week = week;
    }

    public int getWeek() {
        return week;
    }

}
