package cn.edu.hnit.schedule.ui.pages.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.custom.MyDialog;
import cn.edu.hnit.schedule.custom.MyFragment;
import cn.edu.hnit.schedule.databinding.FragmentHeaderBinding;
import cn.edu.hnit.schedule.repository.DateRepository;
import cn.edu.hnit.schedule.repository.SettingRepository;
import cn.edu.hnit.schedule.ui.pages.add.AddCourseActivity;
import cn.edu.hnit.schedule.ui.pages.setting.SettingActivity;
import cn.edu.hnit.schedule.viewmodel.HeaderViewModel;
import es.dmoral.toasty.Toasty;

public class HeaderFragment extends MyFragment {

    private BackToCurrentWeekListener callback;
    private HeaderViewModel mViewModel;
    private FragmentHeaderBinding mBinding;
    private GetCourseDialog dialog = new GetCourseDialog();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_header, container, false);
        callback = (BackToCurrentWeekListener) getActivity();
        if (getActivity() != null) {
            mViewModel = new HeaderViewModel(getActivity().getApplication());
            mViewModel = ViewModelProviders.of(this).get(HeaderViewModel.class);
        }
        mBinding.setVm(mViewModel);
        initOnClickListener();
        refreshUi();
        return mBinding.getRoot();
    }

    //设置点击监听
    private void initOnClickListener() {
        mBinding.add.setOnClickListener(view -> jump2Add());
        mBinding.refresh.setOnClickListener(view -> showGetCourseDialog());
        mBinding.settings.setOnClickListener(view -> jump2Settings());
        mBinding.date.setOnClickListener(view -> callback.go2CurrentWeek());
        mBinding.week.setOnClickListener(view -> setCurrentWeek());
    }

    //跳转到当前周的接口
    public interface BackToCurrentWeekListener {
        void go2CurrentWeek();
        void refreshUi();
    }

    public void onOtherWeek(int otherWeek) {
        mViewModel.notInCurrentWeek(otherWeek);
        mBinding.setVm(mViewModel);
    }

    public void back2CurrentWeek() {
        mViewModel.back2CurrentWeek();
        mBinding.setVm(mViewModel);
    }

    //显示获取课程的dialog
    private void showGetCourseDialog() {
        if (getFragmentManager() != null & !dialog.isAdded()) {
            dialog.show(getFragmentManager(), "getCoursesFragment");
            mBinding.refresh.animate().rotation(mBinding.refresh.getRotation() + 360).setDuration(600).start();
        }
    }

    //设置当前周
    private void setCurrentWeek() {
        MyDialog dialog = new MyDialog(getContext(), View.inflate(getContext(), R.layout.dialog_set_current_week, null));
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();
        EditText input = dialog.findViewById(R.id.input);
        dialog.findViewById(R.id.btn_ok).setOnClickListener(view -> saveOffset(dialog, input.getText().toString()));
    }

    //保存当前周数设置
    private void saveOffset(MyDialog dialog, String week) {
        if (getContext() != null) {
            if (week != null) {
                if (!week.equals("")) {
                    if (Integer.valueOf(week) < 0){
                        Toasty.error(getContext(), "周数不能为负").show();
                    } else if (Integer.valueOf(week) > 22) {
                        Toasty.error(getContext(), "周数应小于22").show();
                    } else {
                        SettingRepository mRepository = new SettingRepository(getContext());
                        mRepository.setWeekOffset(Integer.valueOf(week) - new DateRepository(getContext()).getWeekOfYear());
                        mViewModel.setTime();
                        mBinding.setVm(mViewModel);
                        callback.refreshUi();
                        callback.go2CurrentWeek();
                    }
                } else {
                    Toasty.error(getContext(), "周数不能为空").show();
                }
            }
        }
        dialog.dismiss();
    }

    //跳转到设置
    private void jump2Settings() {
        Intent intent = new Intent(getActivity(), SettingActivity.class);
        startActivity(intent);
    }

    //跳转到添加课程
    private void jump2Add() {
        Intent intent = new Intent(getActivity(), AddCourseActivity.class);
        startActivity(intent);
    }

    @Override
    public void refreshUi() {
        int dateColor = getLargeTitleTextColor();
        int weekColor = getContentTextColor();
        mBinding.setDateColor(dateColor);
        mBinding.setWeekColor(weekColor);
    }

}
