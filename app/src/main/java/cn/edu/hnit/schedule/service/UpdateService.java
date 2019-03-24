package cn.edu.hnit.schedule.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
    用于app自动检查更新并下载
 */
public class UpdateService extends Service {
    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
