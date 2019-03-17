package cn.edu.hnit.schedule.ui.pages.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.custom.MyActivity;
import cn.edu.hnit.schedule.databinding.ActivitySettingBinding;
import cn.edu.hnit.schedule.repository.SettingRepository;

public class SettingActivity extends MyActivity {

    private ActivitySettingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        mBinding.back.setOnClickListener(view -> {
            finish();
        });
        refreshUi();
    }

    @Override
    public void refreshUi() {
        initStatusBar();
        mBinding.setBackgroundColor(getBackgroundColor());
        mBinding.setTextColor(getLargeTitleTextColor());
    }

}
