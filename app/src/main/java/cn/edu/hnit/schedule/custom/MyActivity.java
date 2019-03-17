package cn.edu.hnit.schedule.custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.repository.SettingRepository;

public abstract class MyActivity extends AppCompatActivity {

    private MyReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initReceiver();
        initStatusBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    //初始化广播接收器
    private void initReceiver() {
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("REFRESH_UI");
        registerReceiver(mReceiver, intentFilter);
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null)
                if (intent.getAction().equals("REFRESH_UI"))
                    refreshUi();
        }
    }

    //初始化状态栏样式
    public void initStatusBar() {
        SettingRepository mRepository = new SettingRepository(this);
        if (mRepository.getSwitchOption("ui_dark_theme")) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public int getBackgroundColor() {
        if (new SettingRepository(this).getSwitchOption("ui_dark_theme")) {
            return getResources().getColor(R.color.theme_background_dark);
        } else {
            return getResources().getColor(R.color.theme_background_light);
        }
    }

    public int getContentTextColor() {
        if (new SettingRepository(this).getSwitchOption("ui_dark_theme")) {
            return getResources().getColor(R.color.theme_text_content_dark);
        } else {
            return getResources().getColor(R.color.theme_text_content_light);
        }
    }

    public int getTitleTextColor() {
        if (new SettingRepository(this).getSwitchOption("ui_dark_theme")) {
            return getResources().getColor(R.color.theme_text_title_dark);
        } else {
            return getResources().getColor(R.color.theme_text_title_light);
        }
    }

    public int getLargeTitleTextColor() {
        if (new SettingRepository(this).getSwitchOption("ui_dark_theme")) {
            return getResources().getColor(R.color.theme_text_title_large_dark);
        } else {
            return getResources().getColor(R.color.theme_text_title_large_light);
        }
    }

    public abstract void refreshUi();

}
