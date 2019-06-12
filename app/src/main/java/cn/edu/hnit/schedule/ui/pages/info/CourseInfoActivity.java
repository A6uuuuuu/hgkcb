package cn.edu.hnit.schedule.ui.pages.info;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import org.litepal.LitePal;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.custom.MyActivity;
import cn.edu.hnit.schedule.databinding.ActivityCourseInfoBinding;
import cn.edu.hnit.schedule.model.Course;

public class CourseInfoActivity extends MyActivity {

    private ActivityCourseInfoBinding mBinding;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_course_info);
        refreshUi();
        loadData();
        mBinding.back.setOnClickListener(view -> finish());
        mBinding.delete.setOnClickListener(view -> deleteCourse());
    }

    private void loadData() {
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
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

    @SuppressLint("WrongConstant")
    private void deleteCourse() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除课程");
        builder.setMessage("确认要删除这节课吗?");
        builder.setCancelable(true);
        builder.setPositiveButton("确认", (dialogInterface, i) -> {
            LitePal.delete(Course.class, id);
            Intent intent = new Intent("REFRESH_UI");
            intent.addFlags(0x01000000);
            sendBroadcast(intent);
            finish();
        });
        builder.setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void refreshUi() {
        mBinding.setBackgroundColor(getBackgroundColor());
        mBinding.setTitleColor(getTitleTextColor());
        mBinding.setTextColor(getContentTextColor());
        initStatusBar();
    }
}
