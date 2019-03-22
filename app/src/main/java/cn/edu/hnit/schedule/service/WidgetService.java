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
import cn.edu.hnit.schedule.util.CourseUtil;

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
                switch (new CourseUtil(currentWeek).getStatus(jc)) {
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
            //Log.d(TAG, "status: " + getStatus(jc));
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

        //替换一些字符
        private String handlePlace(String place) {
            return place.replace("（", "(")
                    .replace("）", ")")
                    .replace(" ", "");
        }

        private void updateList() {
            courses.clear();
            List<Course> courses = LitePal.findAll(Course.class);
            currentWeek = new DateRepository(getApplicationContext()).getCurrentWeek();
            if (!courses.isEmpty()) {
                for (Course course : courses) {
                    if (new CourseUtil(currentWeek).inCurrentWeekDay(course.getTime().split(" ", 2))) {
                        this.courses.add(course);
                    }
                }
            }
        }

    }

}
