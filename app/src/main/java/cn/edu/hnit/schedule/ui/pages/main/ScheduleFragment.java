package cn.edu.hnit.schedule.ui.pages.main;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.edu.hnit.schedule.custom.CourseView;
import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.custom.MyFragment;
import cn.edu.hnit.schedule.databinding.FragmentScheduleBinding;
import cn.edu.hnit.schedule.repository.DateRepository;
import cn.edu.hnit.schedule.repository.SettingRepository;
import cn.edu.hnit.schedule.ui.controller.CourseController;
import cn.edu.hnit.schedule.ui.pages.info.CourseInfoActivity;
import cn.edu.hnit.schedule.viewmodel.ScheduleFragmentViewModel;

public class ScheduleFragment extends MyFragment {

    public int week;
    private boolean isWeekdayBarFolded = true;
    private CourseController courseController;
    private FragmentScheduleBinding mBinding;
    private ScheduleFragmentViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false);
        mViewModel = new ScheduleFragmentViewModel();
        mViewModel = ViewModelProviders.of(this).get(ScheduleFragmentViewModel.class);
        mViewModel.setWeek(week);
        courseController = new CourseController(this);
        if (mViewModel.getWeek() == new DateRepository(getContext()).getCurrentWeek()) {
            markWeekDay();
        }
        initTextColor();
        setDateForWeekday();
        loadCourses();
        mBinding.weekdayBar.setOnClickListener(view -> changeWeekdayBar());
        return mBinding.getRoot();
    }

    //设置周数
    public void setWeek(int week) {
        this.week = week;
    }

    public int getWeek() {
        return mViewModel.getWeek();
    }

    //展开星期栏
    private void expandWeekdayBar() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(dp2px(20), dp2px(40));
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            ViewGroup.LayoutParams layoutParams = mBinding.weekdayBar.getLayoutParams();
            layoutParams.height = (int) valueAnimator1.getAnimatedValue();
            getActivity().runOnUiThread(() -> mBinding.weekdayBar.setLayoutParams(layoutParams));
        });
        valueAnimator.setDuration(300);
        valueAnimator.start();
        isWeekdayBarFolded = false;
    }

    //折叠星期栏
    private void foldWeekdayBar() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(dp2px(40), dp2px(20));
        valueAnimator.addUpdateListener(valueAnimator1 -> {
            ViewGroup.LayoutParams layoutParams = mBinding.weekdayBar.getLayoutParams();
            layoutParams.height = (int) valueAnimator1.getAnimatedValue();
            getActivity().runOnUiThread(() -> mBinding.weekdayBar.setLayoutParams(layoutParams));
        });
        valueAnimator.setDuration(300);
        valueAnimator.start();
        isWeekdayBarFolded = true;
    }

    //改变星期栏状态
    private void changeWeekdayBar() {
        Log.d("ScheduleFragment", "changeWeekdayBar: change");
        if (isWeekdayBarFolded) {
            expandWeekdayBar();
        } else {
            foldWeekdayBar();
        }
    }

    //为每个星期设置日期
    @SuppressLint("SetTextI18n")
    private void setDateForWeekday() {
        DateRepository dateRepository = new DateRepository(getContext());
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        calendar = getFirstDayOfWeek(year, week - dateRepository.getOffset() - 1);
        List<TextView> views= new ArrayList<>();
        views.add(mBinding.dateMon);
        views.add(mBinding.dateTues);
        views.add(mBinding.dateWed);
        views.add(mBinding.dateThur);
        views.add(mBinding.dateFri);
        views.add(mBinding.dateSat);
        views.add(mBinding.dateSun);
        for (TextView view : views) {
            view.setText(calendar.get(Calendar.MONTH) + 1 + "/" + calendar.get(Calendar.DATE));
            calendar.add(Calendar.DATE, 1);
        }
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
        if (getContext() != null) {
            SettingRepository mRepository = new SettingRepository(getContext());
            if (!mRepository.getSwitchOption("ui_not_current_week")) {
                for (CourseView course : courseController.getCourses_()) {
                    mBinding.schedule.addView(course);
                    addOnClickCourseEvent(course);
                }
            }
            for (CourseView course : courseController.getCourses()) {
                mBinding.schedule.addView(course);
                addOnClickCourseEvent(course);
            }
        }
    }

    //添加点击事件
    private void addOnClickCourseEvent(CourseView view) {
        view.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), CourseInfoActivity.class);
            intent.putExtra("id", view.getId());
            startActivity(intent);
        });
    }

    @SuppressLint("WrongConstant")
    public void refreshUi() {
        Intent intent = new Intent("REFRESH_UI");
        intent.addFlags(0x01000000);
        if (getActivity() != null) {
            getActivity().sendBroadcast(intent);
        }
    }

    private int dp2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public Calendar getFirstDayOfWeek(int year, int week) {
        Calendar firDay = Calendar.getInstance();
        firDay.set(Calendar.YEAR, year);
        firDay.set(Calendar.WEEK_OF_YEAR, week + 1);
        firDay.set(Calendar.DAY_OF_WEEK, 2);
        return firDay;
    }

}
