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
    private int id = 0;
    private DeleteCall call;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_add_course_time, container, false);
        call = (DeleteCall) getActivity();
        mBinding.jc.setOnClickListener(view -> showJcDialog());
        mBinding.week.setOnClickListener(view -> showWeekDialog());
        mBinding.delete.setOnClickListener(view -> deleteSelf());
        return mBinding.getRoot();
    }

    public void setId(int id) {
        this.id = id;
    }

    private void showJcDialog() {

    }

    private void showWeekDialog() {

    }

    private void deleteSelf() {
        if (id == 0 & getContext() != null)
            Toasty.error(getContext(), "至少要有一个时间段").show();
        else
            call.delete(id);
    }

    public interface DeleteCall {
        void delete(int id);
    }

}
