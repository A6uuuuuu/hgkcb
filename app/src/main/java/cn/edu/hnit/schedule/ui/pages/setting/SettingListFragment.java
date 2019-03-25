package cn.edu.hnit.schedule.ui.pages.setting;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.custom.MyFragment;
import cn.edu.hnit.schedule.databinding.FragmentSettingListBinding;
import cn.edu.hnit.schedule.repository.SettingRepository;
import cn.edu.hnit.schedule.util.SettingUtil;
import cn.edu.hnit.schedule.viewmodel.SettingListViewModel;
import es.dmoral.toasty.Toasty;

public class SettingListFragment extends MyFragment {

    private FragmentSettingListBinding mBinding;
    private int click = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_list, container, false);
        SettingListViewModel mViewModel = ViewModelProviders.of(this).get(SettingListViewModel.class);
        mBinding.setVm(mViewModel);
        refreshUi();
        enter2WhiteListListener();
        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int textSize = i + 10;
                String str = textSize + "sp";
                mViewModel.setTextSizeOption(textSize);
                mBinding.textSize.setText(str);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mBinding.emmm.setOnClickListener(view -> surprise());
        return mBinding.getRoot();
    }

    //设置监听引导用户至白名单
    private void enter2WhiteListListener() {
        mBinding.widgetStatus.setOnClickListener(view -> {
            new SettingUtil().enterWhiteListSetting(getContext());
        });
    }

    //一个彩蛋
    private void surprise() {
        if (getContext() != null) {
            click++;
            if (click < 20) {
                Toasty.info(getContext(), "" + click).show();
            } else {
                Toasty.info(getContext(), "你很无聊啊🙃").show();
            }
        }
    }

    @Override
    public void refreshUi() {
        mBinding.setTextColor(getContentTextColor());
        mBinding.setTitleColor(getTitleTextColor());
    }

}
