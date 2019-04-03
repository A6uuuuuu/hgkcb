package cn.edu.hnit.schedule.ui.pages.grade;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import java.util.Iterator;
import java.util.Map;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.custom.MyActivity;
import cn.edu.hnit.schedule.databinding.ActivityGradeBinding;

public class GradeActivity extends MyActivity implements LoginForGradeFragment.QueryCallBack{

    private ActivityGradeBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_grade);
        mBinding.back.setOnClickListener(view -> finish());
    }

    @Override
    public void refreshUi() {

    }

    @Override
    public void showGrade() {
        runOnUiThread(() -> {
            mBinding.gradeView.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void setGrade(Map data) {
        StringBuilder content = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = data.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            content.append(entry.getKey());
            content.append(": ");
            content.append(entry.getValue());
            content.append("\n");
        }
        runOnUiThread(() -> {
            mBinding.grade.setText(content.toString());
            mBinding.gradeView.setVisibility(View.VISIBLE);
        });
    }

}
