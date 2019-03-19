package cn.edu.hnit.schedule.ui.pages.add;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.databinding.DialogJcPickerBinding;

public class JcPickerDialog extends DialogFragment {

    private DialogJcPickerBinding mBinding;
    private AddCourseTimeFragment fragment;
    private int start = 1;
    private int end = 1;
    private String weekday = "星期一";
    List<Integer> data = new ArrayList<>();
    List<String> weeks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 1; i <= 12; i++) {
            data.add(i);
        }
        weeks = Arrays.asList("星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_jc_picker, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        setPicker();
        mBinding.ok.setOnClickListener(view -> {
            fragment.ok(start, end, weekday);
            this.dismiss();
        });
        return mBinding.getRoot();
    }

    private void setPicker() {
        initPicker(mBinding.jcPicker1);
        mBinding.jcPicker1.setData(data);
        initPicker(mBinding.jcPicker2);
        mBinding.jcPicker2.setData(data);
        initPicker(mBinding.weekdayPicker);
        mBinding.weekdayPicker.setData(weeks);
        mBinding.jcPicker1.setOnItemSelectedListener((picker, data, position) -> {
            List<Integer> _data = new ArrayList<>();
            for (int i = position + 1; i <= 12; i++) {
                _data.add(i);
            }
            start = (Integer) data;
            mBinding.jcPicker2.setData(_data);
            mBinding.jcPicker2.setSelectedItemPosition(0);
            end = start;
        });
        mBinding.jcPicker2.setOnItemSelectedListener((picker, data, position) -> {
            if ((Integer) data < start) {
                picker.setSelectedItemPosition(start - 1);
                end = start;
            } else {
                end = (Integer) data;
            }
        });
        mBinding.weekdayPicker.setOnItemSelectedListener((picker, data, position) -> {
            weekday = (String) data;
        });
    }

    private void initPicker(WheelPicker picker) {
        //picker.setCyclic(true);
        //picker.setData(data);
        //picker.setIndicator(true);
        //picker.setIndicatorColor(Color.parseColor("#000000"));
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
