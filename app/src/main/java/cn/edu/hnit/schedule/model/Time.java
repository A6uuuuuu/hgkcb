package cn.edu.hnit.schedule.model;

public class Time {

    private String date;
    private int week;

    public Time(String date, int week) {
        this.date = date;
        this.week = week;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }
}
