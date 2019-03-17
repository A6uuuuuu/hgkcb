package cn.edu.hnit.schedule.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.model.Course;
import cn.edu.hnit.schedule.repository.DateRepository;
import cn.edu.hnit.schedule.repository.SettingRepository;
import cn.edu.hnit.schedule.ui.pages.main.MainActivity;

import static android.content.ContentValues.TAG;

public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ViewRemoteService(this, intent);
    }

    //实现一个ViewRemoteService再其中进行adapter的一些操作
    private class ViewRemoteService implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private int currentWeek;
        private static final int OVER = 0;
        private static final int PROCESSING = 1;
        private static final int NOTSTART = 2;
        private static final int READY = 3;
        private List<Course> courses = new ArrayList<>();

        ViewRemoteService(Context context, Intent intent) {
            this.context = context;
        }

        @Override
        public void onCreate() {
            updateList();
        }

        @Override
        public void onDataSetChanged() {
            updateList();
        }

        @Override
        public void onDestroy() {
            courses.clear();
            Log.d(TAG, "onDestroy");
        }

        @Override
        public int getCount() {
            return courses.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            String jc = getJc(courses.get(position).getTime());
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.item_course);
            views.setTextViewText(R.id.name, courses.get(position).getName());
            views.setTextViewText(R.id.info, courses.get(position).getTeacher().replace(" ", "") + "@" + handlePlace(courses.get(position).getPlace()));
            views.setTextViewText(R.id.time, jc);
            SettingRepository mRepository = new SettingRepository(context);
            if (mRepository.getSwitchOption("widget_jump")) {
                Intent intent = new Intent(context, MainActivity.class);
                views.setOnClickFillInIntent(R.id.widget_list_item, intent);
            }
            if (!mRepository.getSwitchOption("widget_course_status")) {
                views.setViewVisibility(R.id.status, View.GONE);
            } else {
                views.setViewVisibility(R.id.status, View.VISIBLE);
                switch (getStatus(jc)) {
                    case OVER:
                        views.setTextViewText(R.id.status, "已结束");
                        views.setTextColor(R.id.status, Color.parseColor("#666666"));
                        break;
                    case PROCESSING:
                        views.setTextViewText(R.id.status, "正在上课");
                        views.setTextColor(R.id.status, Color.parseColor("#3de1ad"));
                        break;
                    case NOTSTART:
                        views.setTextViewText(R.id.status, "未开始");
                        views.setTextColor(R.id.status, Color.parseColor("#e9e7ef"));
                        break;
                    case READY:
                        views.setTextViewText(R.id.status, "即将开始");
                        views.setTextColor(R.id.status, Color.parseColor("#ffa400"));
                        break;
                }
            }
            Log.d(TAG, "status: " + getStatus(jc));
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        //得到节次
        private String getJc(String time) {
            String[] _time = time.split("\\)\\(", 2);
            String jc = _time[1];
            return jc.replace(")", "");
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

        //替换一些字符
        private String handlePlace(String place) {
            return place.replace("（", "(")
                    .replace("）", ")")
                    .replace(" ", "");
        }

        //获取星期
        private int getWeekDay() {
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            return calendar.get(Calendar.DAY_OF_WEEK) - 1;
        }

        //获取当前时间
        private int[] getCurrentTime() {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
            String[] timeArray = format.format(new Date()).split(":", 2);
            int[] time = new int[2];
            for (int i = 0; i < 2; i++) {
                time[i] = Integer.valueOf(timeArray[i]);
            }
            return time;
        }

        //获取当前课程状态
        private int getStatus(String jc) {
            String[] time = jc.replace("节", "")
                    .replace(" ", "")
                    .split("-", 2);
            int[] currentTime = getCurrentTime();
            //根据节次得到开始和结束时间
            int startJc = Integer.valueOf(time[0]);
            int endJc = Integer.valueOf(time[1]);
            CourseTime courseTime = new CourseTime(startJc, endJc);
            int _currentTime = currentTime[0] * 60 + currentTime[1];
            int _start = courseTime.getStart()[0] * 60 + courseTime.getStart()[1];
            int _end = courseTime.getEnd()[0] * 60 + courseTime.getEnd()[1];
            //Log.d(TAG, "getStatus:" + "\n_start: " + _start + "\n_end: " + _end + "\ncurrentTime: " + _currentTime);
            if (_currentTime < _start) {
                if (_start - _currentTime <= 20) {
                    return READY;
                }
                return NOTSTART;
            } else if (_currentTime <= _end) {
                return PROCESSING;
            }
            return OVER;
        }

        private void updateList() {
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
        }

        class CourseTime {
            /*
                上课时间：
                01  08:30 - 09:15
                02  09:25 - 10:05
                03  10:25 - 11:05
                04  11:05 - 12:00

                05  14:00 - 14:45
                06  14:55 - 15:40
                07  15:55 - 16:40
                08  16:50 - 17:35

                09  19:00 - 19:45
                10  19:55 - 20:40
                11  20:55 - 21:40
                12  21:50 - 22:35
            */
            private int start;
            private int end;
            private String[] timeArray = {
                    "",
                    "08:30-09:15", "09:25-10:05", "10:25-11:05", "11:05-12:00",
                    "14:00-14:45", "14:55-15:40", "15:55-16:40", "16:50-17:35",
                    "19:00-19:45", "19:55-20:40", "20:55-21:40", "21:50-22:35"};

            CourseTime(int start, int end) {
                this.start = start;
                this.end = end;
            }

            int[] getStart() {
                String[] _time = timeArray[start].split("-", 2)[0].split(":", 2);
                int[] time = new int[2];
                time[0] = Integer.valueOf(_time[0]);
                time[1] = Integer.valueOf(_time[1]);
                return time;
            }

            int[] getEnd() {
                String[] _time = timeArray[end].split("-", 2)[1].split(":", 2);
                int[] time = new int[2];
                time[0] = Integer.valueOf(_time[0]);
                time[1] = Integer.valueOf(_time[1]);
                return time;
            }

        }

    }

}
