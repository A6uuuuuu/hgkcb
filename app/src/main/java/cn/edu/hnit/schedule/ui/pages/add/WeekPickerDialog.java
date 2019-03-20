package cn.edu.hnit.schedule.ui.pages.add;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.databinding.DialogWeekPickerBinding;

public class WeekPickerDialog extends DialogFragment {

    private DialogWeekPickerBinding mBinding;
    private AddCourseTimeFragment fragment;
    private List<Integer> weeks = new ArrayList<>();
    private int start = 1;
    private int end = 1;
    private int MAX_WEEK = 23;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 1; i <= MAX_WEEK; i++) {
            weeks.add(i);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_week_picker, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        setPicker();
        mBinding.ok.setOnClickListener(view -> {
            fragment.weekOK(start, end);
            this.dismiss();
        });
        return mBinding.getRoot();
    }

    private void setPicker() {
        initPicker(mBinding.weekPicker1);
        initPicker(mBinding.weekPicker2);
        //设置滚动监听
        mBinding.weekPicker1.setOnItemSelectedListener((picker, data, position) -> {
            List<Integer> _data = new ArrayList<>();
            for (int i = position + 1; i <= MAX_WEEK; i++) {
                _data.add(i);
            }
            start = (Integer) data;
            mBinding.weekPicker2.setData(_data);
            mBinding.weekPicker2.setSelectedItemPosition(0);
            end = start;
        });
        mBinding.weekPicker2.setOnItemSelectedListener((picker, data, position) -> {
            if ((Integer) data < start) {
                picker.setSelectedItemPosition(start - 1);
                end = start;
            } else {
                end = (Integer) data;
            }
        });
    }

    private void initPicker(WheelPicker picker) {
        picker.setData(weeks);
        picker.setAtmospheric(true);
        picker.setCurved(true);
        picker.setItemTextSize(72);
        picker.setItemAlign(WheelPicker.ALIGN_CENTER);
        picker.setSelectedItemTextColor(0xff000000);
        picker.setItemSpace(40);
    }

    public void setParentFragment(AddCourseTimeFragment fragment) {
        this.fragment = fragment;
    }


}
