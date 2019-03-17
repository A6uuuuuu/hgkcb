package cn.edu.hnit.schedule.ui.pages.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.edu.hnit.schedule.custom.CourseView;
import cn.edu.hnit.schedule.custom.MyDialog;
import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.custom.MyFragment;
import cn.edu.hnit.schedule.databinding.FragmentScheduleBinding;
import cn.edu.hnit.schedule.model.Course;
import cn.edu.hnit.schedule.repository.DateRepository;
import cn.edu.hnit.schedule.repository.SettingRepository;
import cn.edu.hnit.schedule.ui.controller.CourseController;

import static android.view.View.inflate;

public class ScheduleFragment extends MyFragment {

    public int week;
    private CourseController courseController;
    private FragmentScheduleBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false);
        courseController = new CourseController(this);
        if (this.week == new DateRepository(getContext()).getCurrentWeek()) {
            markWeekDay();
        }
        initTextColor();
        loadCourses();
        return mBinding.getRoot();
    }

    //设置周数
    public void setWeek(int week) {
        this.week = week;
    }

    //标记星期
    private void markWeekDay() {
        Date d = new Date();
        SimpleDateFormat weekFormat = new SimpleDateFormat("E", Locale.CHINA);
        int bg = getGray();
        switch (weekFormat.format(d)) {
            case "周日":
                mBinding.cvSun.setCardBackgroundColor(bg);
                break;
            case "周一":
                mBinding.cvMon.setCardBackgroundColor(bg);
                break;
            case "周二":
                mBinding.cvTues.setCardBackgroundColor(bg);
                break;
            case "周三":
                mBinding.cvWed.setCardBackgroundColor(bg);
                break;
            case "周四":
                mBinding.cvThur.setCardBackgroundColor(bg);
                break;
            case "周五":
                mBinding.cvFri.setCardBackgroundColor(bg);
                break;
            case "周六":
                mBinding.cvSat.setCardBackgroundColor(bg);
                break;
        }
    }

    //初始化字体颜色
    private void initTextColor() {
        mBinding.setTextColor(getContentTextColor());
    }

    //加载课程
    private void loadCourses() {
        for (CourseView course: courseController.getCourses_()) {
            mBinding.schedule.addView(course);
            addOnClickCourseEvent(course);
        }
        for (CourseView course: courseController.getCourses()) {
            mBinding.schedule.addView(course);
            addOnClickCourseEvent(course);
        }
    }

    //添加点击事件
    private void addOnClickCourseEvent(CourseView view) {
        view.setOnClickListener(view1 -> {
            Course course = LitePal.find(Course.class, view.getId());
            MyDialog dialog = new MyDialog(getContext(), inflate(getContext(), R.layout.dialog_more_info, null));
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            TextView courseName = dialog.findViewById(R.id.course_name);
            TextView teacherName = dialog.findViewById(R.id.teacher_name);
            TextView classRoom = dialog.findViewById(R.id.class_room);
            TextView jc = dialog.findViewById(R.id.jc);
            TextView zc = dialog.findViewById(R.id.zc);
            courseName.setText(course.getName());
            teacherName.setText(course.getTeacher());
            classRoom.setText(course.getPlace());
            String[] time = handleCourseTime(course.getTime());
            jc.setText(time[1]);
            zc.setText(time[0]);
            dialog.findViewById(R.id.btn_delete_course).setOnClickListener(view2 -> showAlertDialog(dialog, view.getId()));
            dialog.show();
        });
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

    //删除课程
    private void deleteCourse(int id) {
        LitePal.delete(Course.class, id);
        refreshUi();
    }

    //是否删除dialog
    private void showAlertDialog(MyDialog dialog, int id) {
        if (getContext() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("删除课程");
            builder.setMessage("确认要删除这节课吗?");
            builder.setCancelable(true);
            builder.setPositiveButton("确认", (dialogInterface, i) -> {
                deleteCourse(id);
                dialog.dismiss();
            });
            builder.setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @SuppressLint("WrongConstant")
    public void refreshUi() {
        Intent intent = new Intent("REFRESH_UI");
        intent.addFlags(0x01000000);
        if (getActivity() != null) {
            getActivity().sendBroadcast(intent);
        }
    }

}
