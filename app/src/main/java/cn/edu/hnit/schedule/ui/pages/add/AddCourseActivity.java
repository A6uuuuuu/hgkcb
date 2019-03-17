package cn.edu.hnit.schedule.ui.pages.add;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.aigestudio.wheelpicker.WheelPicker;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.custom.MyActivity;
import cn.edu.hnit.schedule.databinding.ActivityAddCourseBinding;

public class AddCourseActivity extends MyActivity implements AddCourseTimeFragment.DeleteCall{

    private ActivityAddCourseBinding mBinding;
    FragmentManager manager = getSupportFragmentManager();
    FragmentTransaction transaction = manager.beginTransaction();
    List<AddCourseTimeFragment> fragments = new ArrayList<>();
    private int num = 1;

    private static final String TAG = "AddCourseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_course);
        mBinding.back.setOnClickListener(view -> finish());
        mBinding.add.setOnClickListener(view -> addFragment());
        mBinding.save.setOnClickListener(view -> saveCourse());
        refreshUi();
    }

    @Override
    public void refreshUi() {
        initStatusBar();
        mBinding.setBackgroundColor(getBackgroundColor());
        mBinding.setTextColor(getContentTextColor());
    }

    @Override
    public void delete(int id) {
        Log.d(TAG, "delete: " + fragments.size() + " " + fragments.get(id - 1));
        //transaction.remove(fragments.get(id - 1));
        //transaction.replace(R.id.time, fragments.get(id - 1));
    }

    private void addFragment() {
        mBinding.add.setVisibility(View.GONE);
        AddCourseTimeFragment fragment = new AddCourseTimeFragment();
        fragment.setId(num);
        transaction.add(R.id.time, fragment, "" + num);
        transaction.commit();
        fragments.add(fragment);
        num++;
    }

    private void saveCourse() {

    }

}
