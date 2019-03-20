package cn.edu.hnit.schedule.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.repository.DateRepository;
import cn.edu.hnit.schedule.repository.SettingRepository;
import cn.edu.hnit.schedule.service.WidgetService;
import cn.edu.hnit.schedule.ui.pages.main.MainActivity;

public class CourseListWidget extends AppWidgetProvider {

    private static final String UPDATE_ACTION = "WIDGET_UPDATE";
    private static final String UPDATE_ACTION_NO_COURSES = "WIDGET_UPDATE_NO_COURSES";
    private boolean noCourse = false;

    private static final String TAG = "Widget";

    private void updateAppWidget(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager, int appWidgetId) {
        SettingRepository mRepository = new SettingRepository(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_course_list);
        SimpleDateFormat weekFormat = new SimpleDateFormat("E", Locale.CHINA);
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.course_list);
        views.setTextViewText(R.id.week_day, "星期" + weekFormat.format(new Date()).replace("周", ""));
        views.setTextViewText(R.id.week, "第" + new DateRepository(context).getCurrentWeek() + "周");
        views.setRemoteAdapter(R.id.course_list, new Intent(context, WidgetService.class));

        //判断是否开启点击跳转主界面功能
        if (mRepository.getSwitchOption("widget_jump")) {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.header, pendingIntent);
            views.setPendingIntentTemplate(R.id.course_list, pendingIntent);
            views.setPendingIntentTemplate(R.id.status, pendingIntent);
        }

        //判断是否隐藏日期和星期
        if (mRepository.getSwitchOption("widget_date")) {
            views.setViewVisibility(R.id.header, View.GONE);
        } else {
            views.setViewVisibility(R.id.header, View.VISIBLE);
        }

        //判断今天有没有课
        if (noCourse) {
            views.setViewVisibility(R.id.course_list, View.GONE);
        } else {
            views.setViewVisibility(R.id.course_list, View.VISIBLE);
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        Log.d(TAG, "onUpdate : 小部件已更新 ");
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive intent: " + intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context.getPackageName(), getClass().getName());
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case UPDATE_ACTION:
                    noCourse = false;
                    onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(thisWidget));
                    break;
                case UPDATE_ACTION_NO_COURSES:
                    noCourse = true;
                    onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(thisWidget));
                    break;
            }
        }
        super.onReceive(context, intent);
    }

}

