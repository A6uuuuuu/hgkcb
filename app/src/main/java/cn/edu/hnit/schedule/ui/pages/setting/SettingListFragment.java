package cn.edu.hnit.schedule.ui.pages.setting;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.custom.MyFragment;
import cn.edu.hnit.schedule.databinding.FragmentSettingListBinding;
import cn.edu.hnit.schedule.repository.SettingRepository;
import cn.edu.hnit.schedule.util.SettingUtil;
import cn.edu.hnit.schedule.viewmodel.SettingListViewModel;
import es.dmoral.toasty.Toasty;

public class SettingListFragment extends MyFragment {

    private FragmentSettingListBinding mBinding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_list, container, false);
        SettingListViewModel mViewModel = ViewModelProviders.of(this).get(SettingListViewModel.class);
        mBinding.setVm(mViewModel);
        refreshUi();
        enter2WhiteListListener();
        return mBinding.getRoot();
    }

    //设置监听引导用户至白名单
    private void enter2WhiteListListener() {
        mBinding.widgetStatus.setOnClickListener(view -> {
            new SettingUtil().enterWhiteListSetting(getContext());
        });
    }

    @Override
    public void refreshUi() {
        mBinding.setTextColor(getContentTextColor());
        mBinding.setTitleColor(getTitleTextColor());
    }

}
