package cn.edu.hnit.schedule.ui.pages.info;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.litepal.LitePal;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.custom.MyActivity;
import cn.edu.hnit.schedule.databinding.ActivityCourseInfoBinding;
import cn.edu.hnit.schedule.model.Course;

public class CourseInfoActivity extends MyActivity {

    private ActivityCourseInfoBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_course_info);
        refreshUi();
        mBinding.back.setOnClickListener(view -> finish());
    }

    private void loadData() {
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        Course course = LitePal.find(Course.class, id);
        mBinding.name.setText(course.getName());
        mBinding.teacher.setText(course.getTeacher());
        mBinding.place.setText(course.getPlace());
        String[] time = handleCourseTime(course.getTime());
        mBinding.jc.setText(time[1]);
        mBinding.week.setText(time[0]);
    }

    //处理课程时间
    private String[] handleCourseTime(String time) {
        String[] t = time.split(" ", 2);
        String[] t2 = t[1].split("\\)\\(", 2);
        String[] result = new String[2];
        result[0] = t2[0].replace("(", "");
        result[1] = t2[1].replace(")", "");
        return result;
    }

    @Override
    public void refreshUi() {
        initStatusBar();
        loadData();
    }
}
