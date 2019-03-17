package cn.edu.hnit.schedule.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.hnit.schedule.model.Course;
import cn.edu.hnit.schedule.repository.DateRepository;

public class KeepAliveService extends JobService {

    private static final String TAG = "KeepAliveService";
    private int currentWeek;
    private List<Course> courses = new ArrayList<>();
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: 服务已启动");
        return Service.START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob: 已执行");
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(getJobInfo());
        updateWidget();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "onStopJob: 已停止");
        return false;
    }

    private JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(this, KeepAliveService.class));
        builder.setMinimumLatency(30 * 1000);
        builder.setOverrideDeadline(120 * 1000);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        return builder.build();
    }

    @SuppressLint("WrongConstant")
    private void updateWidget() {
        courses.clear();
        List<Course> courses = LitePal.findAll(Course.class);
        currentWeek = new DateRepository(getApplicationContext()).getCurrentWeek();
        if (!courses.isEmpty()) {
            for (Course course : courses) {
                if (inCurrentWeekDay(course.getTime().split(" ", 2))) {
                    this.courses.add(course);
                }
            }
        }
        Intent intent;
        if (this.courses.isEmpty()) {
            intent = new Intent("WIDGET_UPDATE_NO_COURSES");
        } else {
            intent = new Intent("WIDGET_UPDATE");
        }
        intent.addFlags(0x01000000);
        sendBroadcast(intent);
        Log.d(TAG, "updateWidget: 小部件已更新");
    }

    //判断是否在本周及本日
    private boolean inCurrentWeekDay(String[] time) {
        String weekday = time[0].substring(0, 1);
        boolean inCurrentWeekday = false;
        boolean inCurrentWeek = false;
        if (Integer.valueOf(weekday) == getWeekDay()) {
            inCurrentWeekday = true;
        }
        String[] weeks = time[1].split("\\)\\(", 2);
        weeks[0] = weeks[0].replace("(", "")
                .replace(")", "")
                .replace("周", "");
        Pattern r1 = Pattern.compile(",");
        Pattern r2 = Pattern.compile("-");
        Matcher m = r1.matcher(weeks[0]);
        int limit = 1;
        while (m.find()) {
            limit++;
        }
        String _weeks[] = weeks[0].split(",", limit);
        for (String week : _weeks) {
            m = r2.matcher(week);
            if (m.find()) {
                String[] range = week.split("-", 2);
                if (currentWeek >= Integer.parseInt(range[0]) & currentWeek <= Integer.parseInt(range[1])) {
                    inCurrentWeek = true;
                }
            } else if (Integer.parseInt(week) == currentWeek) {
                inCurrentWeek = true;
            }
        }
        return inCurrentWeek & inCurrentWeekday;
    }

    //获取星期
    private int getWeekDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

}
