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
import cn.edu.hnit.schedule.util.WeekdayUtil;
import es.dmoral.toasty.Toasty;

public class AddCourseTimeFragment extends Fragment {

    private FragmentAddCourseTimeBinding mBinding;
    private FragmentCall call;
    public int weekday = 0;
    public int startJc = 0;
    public int endJc = 0;
    public int startWeek = 0;
    public int endWeek = 0;

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

    public void saveJc(int start, int end, String weekday) {
        startJc = start;
        endJc = end;
        this.weekday = new WeekdayUtil().toInt(weekday);
        String str = weekday + "  " + start + " - " + end + "节";
        mBinding.jcTv.setText(str);
    }

    public void saveWeek(int start, int end) {
        startWeek = start;
        endWeek = end;
        String str = startWeek + " - " + endWeek + "周";
        mBinding.weekTv.setText(str);
    }

    //显示选择节次的Dialog
    private void showJcDialog() {
        JcPickerDialog dialog = new JcPickerDialog();
        if (getFragmentManager() != null) {
            dialog.setParentFragment(this);
            dialog.show(getFragmentManager(), "jcPicker");
        }
    }

    //显示选择周次的Dialog
    private void showWeekDialog() {
        WeekPickerDialog dialog = new WeekPickerDialog();
        if (getFragmentManager() != null) {
            dialog.setParentFragment(this);
            dialog.show(getFragmentManager(), "weekPicker");
        }
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
