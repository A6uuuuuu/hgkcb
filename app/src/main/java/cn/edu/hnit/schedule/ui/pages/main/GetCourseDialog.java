package cn.edu.hnit.schedule.ui.pages.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Objects;

import cn.edu.hnit.schedule.databinding.DialogGetCourseBinding;
import cn.edu.hnit.schedule.repository.CourseRepository;
import cn.edu.hnit.schedule.R;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

public class GetCourseDialog extends DialogFragment implements CourseRepository.GetVercodeCallback{

    private CourseRepository mRepository = new CourseRepository(this);
    private DialogGetCourseBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_get_course, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        getVercode();
        getPref();
        this.setCancelable(false);
        mBinding.btnOk.setOnClickListener(view -> getCourses());
        mBinding.btnCancel.setOnClickListener(view -> this.dismiss());
        mBinding.vercode.setOnClickListener(view -> getVercode());
        return mBinding.getRoot();
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

    public void getCourses() {
        hideKeyboard();
        String usr = mBinding.user.getText().toString();
        String passwd = mBinding.passwd.getText().toString();
        String vercode = mBinding.input.getText().toString();
        if (usr.isEmpty() | passwd.isEmpty() | vercode.isEmpty()) {
            error("学号、密码及验证码不能为空");
            this.dismiss();
        } else {
            change();
            SharedPreferences.Editor editor = Objects.requireNonNull(getActivity())
                    .getSharedPreferences("settings", MODE_PRIVATE).edit();
            editor.putString("usr", usr);
            editor.putString("passwd", passwd);
            editor.apply();
            mRepository.login(usr, passwd, vercode);
        }
    }

    //获取上次输入的学号和密码
    private void getPref() {
        SharedPreferences pref = Objects.requireNonNull(getActivity())
                .getSharedPreferences("settings", MODE_PRIVATE);
        String preUsr = pref.getString("usr", "");
        String prePasswd = pref.getString("passwd", "");
        mBinding.user.setText(preUsr);
        mBinding.passwd.setText(prePasswd);
    }

    @SuppressLint("WrongConstant")
    public void refreshUi() {
        Intent intent = new Intent("REFRESH_UI");
        intent.addFlags(0x01000000);
        if (getActivity() != null) {
            getActivity().sendBroadcast(intent);
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

    //切换到loading
    public void change() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                            mBinding.sync.setVisibility(View.GONE));
        }
    }

    //隐藏输入法
    private void hideKeyboard() {
        if (getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mBinding.getRoot().getWindowToken(), 0);
        }
    }
}
