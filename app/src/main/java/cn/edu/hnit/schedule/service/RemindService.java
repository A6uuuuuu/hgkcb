package cn.edu.hnit.schedule.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.ui.pages.main.MainActivity;

public class RemindService extends Service {

    private static final String TAG = "RemindService";
    private static final String CHANNEL_ID = "湖工课程表";
    private static final String CHANNEL_NAME = "课程提醒服务";

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification;
        NotificationChannel channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道
            channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
            channel.setShowBadge(false);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("课程提醒已开启")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setContentIntent(getPendingIntent())
                    .setShowWhen(false)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle("课程提醒已开启")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setContentIntent(getPendingIntent())
                    .setShowWhen(false)
                    .setPriority(Notification.PRIORITY_MIN)
                    .build();
        }
        startForeground(2333, notification);
        Log.d(TAG, "onStartCommand: 提醒服务已启动");
        return Service.START_STICKY;
    }

    public void setRemind() {

    }

    public void remind(String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("下一节课即将开始")
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setContentIntent(getPendingIntent())
                    .setShowWhen(false)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build();
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(false);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            manager.notify(0, notification);
        } else {
            Notification notification = new Notification.Builder(this)
                    .setContentTitle("下一节课即将开始")
                    .setContentText(content)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_alarm)
                    .setContentIntent(getPendingIntent())
                    .setShowWhen(false)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .build();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, notification);
        }
    }

    private PendingIntent getPendingIntent() {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        return PendingIntent.getActivity(this, 0, notificationIntent, 0);
    }

    public void onDestroy() {
        super.onDestroy();
    }

}
