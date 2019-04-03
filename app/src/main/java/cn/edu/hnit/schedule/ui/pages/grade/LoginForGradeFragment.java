package cn.edu.hnit.schedule.ui.pages.grade;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Map;
import java.util.Objects;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.databinding.FragmentLoginForGradeBinding;
import cn.edu.hnit.schedule.repository.GradeRepository;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

public class LoginForGradeFragment extends Fragment implements GradeRepository.GradeCallback{

    private FragmentLoginForGradeBinding mBinding;
    private GradeRepository mRepository = new GradeRepository(this);
    private QueryCallBack callBack;
    private String xq = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callBack = (QueryCallBack) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_for_grade, container, false);
        mBinding.vercode.setOnClickListener(view -> getVercode());
        mBinding.query.setOnClickListener(view -> getGrade());
        mBinding.selectXq.setOnClickListener(view -> showXqDialog());
        getVercode();
        return mBinding.getRoot();
    }

    private void showXqDialog() {
        XqPickerDialog dialog = new XqPickerDialog();
        if (getFragmentManager() != null) {
            dialog.setParentFragment(this);
            dialog.show(getFragmentManager(), "xqPicker");
        }
    }

    public void saveXq(String xq) {
        this.xq = xq;
        mBinding.selectXq.setText(xq);
    }

    public interface QueryCallBack {
        void showGrade();
        void setGrade(Map data);
    }

    @Override
    public void showGrade(Map data) {
        callBack.setGrade(data);
    }

    @Override
    public void setVercode(Bitmap bmp) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mBinding.vercode.setImageBitmap(bmp);
                mBinding.tips.setVisibility(View.INVISIBLE);
            });
        }
    }

    private void getVercode() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mBinding.vercode.setImageResource(R.color.gray);
                mBinding.tips.setVisibility(View.VISIBLE);
            });
        }
        mRepository.getVercode();
    }

    public void getGrade() {
        if (xq.length() < 1) {
            error("请选择学期");
        } else {
            callBack.showGrade();
            String usr = mBinding.user.getText().toString();
            String passwd = mBinding.passwd.getText().toString();
            String vercode = mBinding.input.getText().toString();
            if (usr.isEmpty() | passwd.isEmpty() | vercode.isEmpty()) {
                error("学号、密码及验证码不能为空");
            } else {
                SharedPreferences.Editor editor = Objects.requireNonNull(getActivity())
                        .getSharedPreferences("settings", MODE_PRIVATE).edit();
                editor.putString("usr", usr);
                editor.putString("passwd", passwd);
                editor.apply();
                mRepository.setXq(xq);
                mRepository.login(usr, passwd, vercode);
            }
        }
    }

    //封装一下Toast
    public void error(String str) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                    Toasty.error(getActivity(), str, Toast.LENGTH_SHORT).show());
        }
    }

    public void success(String str) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                    Toasty.success(getActivity(), str, Toast.LENGTH_SHORT).show());
        }
    }

}
