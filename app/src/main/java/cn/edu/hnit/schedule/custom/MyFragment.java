package cn.edu.hnit.schedule.custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.repository.SettingRepository;

public abstract class MyFragment extends Fragment {

    private MyReceiver mReceiver;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            this.context = getContext();
        }
        initReceiver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(mReceiver);
    }

    //初始化广播接收器
    private void initReceiver() {
        mReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("REFRESH_UI");
        context.registerReceiver(mReceiver, intentFilter);
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null)
                if (intent.getAction().equals("REFRESH_UI"))
                    refreshUi();
        }
    }

    public int getBackgroundColor() {
        if (new SettingRepository(context).getSwitchOption("ui_dark_theme")) {
            return getResources().getColor(R.color.theme_background_dark);
        } else {
            return getResources().getColor(R.color.theme_background_light);
        }
    }

    public int getContentTextColor() {
        if (new SettingRepository(context).getSwitchOption("ui_dark_theme")) {
            return getResources().getColor(R.color.theme_text_content_dark);
        } else {
            return getResources().getColor(R.color.theme_text_content_light);
        }
    }

    public int getTitleTextColor() {
        if (new SettingRepository(context).getSwitchOption("ui_dark_theme")) {
            return getResources().getColor(R.color.theme_text_title_dark);
        } else {
            return getResources().getColor(R.color.theme_text_title_light);
        }
    }

    public int getLargeTitleTextColor() {
        if (new SettingRepository(context).getSwitchOption("ui_dark_theme")) {
            return getResources().getColor(R.color.theme_text_title_large_dark);
        } else {
            return getResources().getColor(R.color.theme_text_title_large_light);
        }
    }

    public int getGray() {
        if (new SettingRepository(context).getSwitchOption("ui_dark_theme")) {
            return getResources().getColor(R.color.gray_dark);
        } else {
            return getResources().getColor(R.color.gray);
        }
    }

    public abstract void refreshUi();


}
