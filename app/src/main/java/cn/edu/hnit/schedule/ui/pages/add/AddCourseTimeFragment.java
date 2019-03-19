package cn.edu.hnit.schedule.ui.pages.add;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.databinding.FragmentAddCourseTimeBinding;
import es.dmoral.toasty.Toasty;

public class AddCourseTimeFragment extends Fragment {

    private FragmentAddCourseTimeBinding mBinding;
    private FragmentCall call;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_add_course_time, container, false);
        call = (FragmentCall) getActivity();
        mBinding.jc.setOnClickListener(view -> showJcDialog());
        mBinding.week.setOnClickListener(view -> showWeekDialog());
        mBinding.delete.setOnClickListener(view -> delete());
        mBinding.add.setOnClickListener(view -> add());
        return mBinding.getRoot();
    }

    //显示选择节次的Dialog
    private void showJcDialog() {

    }

    //显示选择周次的Dialog
    private void showWeekDialog() {

    }

    //删除这个fragment
    private void delete() {
        call.deleteFragment(this);
    }

    //添加一个fragment
    private void add() {
        call.addFragment();
        setAddBtnStatus(false);
    }

    //设置这个fragment的添加按钮的显示状态
    public void setAddBtnStatus(boolean bool) {
        if (bool) {
            mBinding.add.setVisibility(View.VISIBLE);
        } else {
            mBinding.add.setVisibility(View.GONE);
        }
    }

    //与Activity的接口
    public interface FragmentCall {
        void deleteFragment(AddCourseTimeFragment fragment);
        void addFragment();
    }

}
