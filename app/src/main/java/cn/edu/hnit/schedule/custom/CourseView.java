package cn.edu.hnit.schedule.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;

public class CourseView extends CardView {

    private int id;

    public CourseView(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}
