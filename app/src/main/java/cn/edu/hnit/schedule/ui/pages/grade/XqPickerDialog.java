package cn.edu.hnit.schedule.ui.pages.grade;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.databinding.DialogXqPickerBinding;

public class XqPickerDialog extends DialogFragment {

    private DialogXqPickerBinding mBinding;
    private LoginForGradeFragment fragment;
    private List<String> years = new ArrayList<>();
    private List<String> nums = new ArrayList<>();
    private String year;
    private int num;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nums.add("第一学期");
        nums.add("第二学期");
        setYears();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_xq_picker, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        setPicker();
        mBinding.ok.setOnClickListener(view -> {
            fragment.saveXq(year + "-" + num);
            this.dismiss();
        });
        return mBinding.getRoot();
    }

    private void setPicker() {
        initPicker(mBinding.yearPicker);
        mBinding.yearPicker.setData(years);
        initPicker(mBinding.numPicker);
        mBinding.numPicker.setData(nums);
        year = years.get(0);
        num = 1;
        //设置滚动监听
        mBinding.yearPicker.setOnItemSelectedListener((picker, data, position) -> {
            year = (String) data;
        });
        mBinding.numPicker.setOnItemSelectedListener((picker, data, position) -> {
            num = position + 1;
        });
    }

    private void initPicker(WheelPicker picker) {
        picker.setAtmospheric(true);
        picker.setCurved(true);
        picker.setItemTextSize(72);
        picker.setItemAlign(WheelPicker.ALIGN_CENTER);
        picker.setSelectedItemTextColor(0xff000000);
        picker.setItemSpace(40);
    }

    //获取当前学期
    private void setYears() {
        Date d = new Date();
        SimpleDateFormat y_ft = new SimpleDateFormat("yyyy", Locale.CHINA);
        String y = y_ft.format(d);
        int year = Integer.parseInt(y);
        for (int i = 0; i < 5; i++ ) {
            years.add((year - i - 1) + "-" + (year - i));
        }
    }

    public void setParentFragment(LoginForGradeFragment fragment) {
        this.fragment = fragment;
    }

}
