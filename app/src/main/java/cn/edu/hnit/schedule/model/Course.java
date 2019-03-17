package cn.edu.hnit.schedule.model;

import org.litepal.crud.LitePalSupport;

public class Course extends LitePalSupport {

    private int id;
    private String name;
    private String place;
    private String teacher;
    private String time;

    public Course(String name, String teacher,String time, String place ) {
        this.name = name;
        this.place = place;
        this.teacher = teacher;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
