package cn.edu.hnit.schedule.ui.pages.add;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.custom.MyActivity;
import cn.edu.hnit.schedule.databinding.ActivityAddCourseBinding;
import es.dmoral.toasty.Toasty;

public class AddCourseActivity extends MyActivity implements AddCourseTimeFragment.FragmentCall{

    private ActivityAddCourseBinding mBinding;
    FragmentManager manager = getSupportFragmentManager();
    List<AddCourseTimeFragment> fragments = new ArrayList<>();

    private static final String TAG = "AddCourseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_course);
        addFragment();
        mBinding.back.setOnClickListener(view -> finish());
        mBinding.save.setOnClickListener(view -> saveCourse());
        refreshUi();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public void refreshUi() {
        initStatusBar();
        mBinding.setBackgroundColor(getBackgroundColor());
        mBinding.setTextColor(getContentTextColor());
    }

    @Override
    public void deleteFragment(AddCourseTimeFragment fragment) {
        if (fragments.size() == 1) {
            Toasty.error(this, "至少要有一个时间段").show();
        } else {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
            int index = -1;
            for (int i = 0; i < fragments.size(); i++) {
                if (fragment == fragments.get(i)) {
                    index = i;
                }
            }
            //如果删掉的是最后一个时间段则让前一个时间段的添加按钮显示出来
            if (index == fragments.size() - 1) {
                fragments.get(index - 1).setAddBtnStatus(true);
            }
            if (index >= 0) {
                fragments.remove(index);
            }
            Toasty.info(this, "已删除时间段").show();
            Log.d(TAG, "delete: " + index);
        }
    }

    @Override
    public void addFragment() {
        AddCourseTimeFragment fragment = new AddCourseTimeFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.time, fragment);
        transaction.commit();
        fragments.add(fragment);
    }

    private void saveCourse() {

    }

}
