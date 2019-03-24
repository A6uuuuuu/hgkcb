package cn.edu.hnit.schedule.ui.pages.setting;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.custom.MyActivity;
import cn.edu.hnit.schedule.databinding.ActivitySettingBinding;

public class SettingActivity extends MyActivity {

    private ActivitySettingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        mBinding.back.setOnClickListener(view -> finish());
        refreshUi();
    }

    @Override
    public void refreshUi() {
        initStatusBar();
        mBinding.setBackgroundColor(getBackgroundColor());
        mBinding.setTextColor(getLargeTitleTextColor());
    }

}
