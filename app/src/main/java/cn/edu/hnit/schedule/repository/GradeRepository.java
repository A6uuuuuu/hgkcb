package cn.edu.hnit.schedule.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.hnit.schedule.ui.pages.grade.LoginForGradeFragment;
import cn.edu.hnit.schedule.util.HttpUtil;
import cn.edu.hnit.schedule.util.JsoupUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;

public class GradeRepository {

    private HttpUtil httpUtil = new HttpUtil();
    private LoginForGradeFragment fragment;
    private GradeCallback callback;
    private String xq;

    public GradeRepository(LoginForGradeFragment fragment) {
        this.fragment = fragment;
        callback = fragment;
    }

    public interface GradeCallback {
        void setVercode(Bitmap bmp);
        void showGrade(Map data);
    }

    public void setXq(String xq) {
        this.xq = xq;
    }

    //获取验证码
    public void getVercode() {
        httpUtil.getVercode(new Callback() {
            @Override@EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                error("验证码加载失败，请检查网络连接");
            }

            @Override@EverythingIsNonNull
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() & response.body() != null) {
                    InputStream is = response.body().byteStream();
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    if (fragment != null) {
                        callback.setVercode(bmp);
                    }
                    is.close();
                } else {
                    error("验证码加载失败，教务系统繁忙");
                }
            }
        });
    }

    //登录教务系统
    public void login(String usr, String passwd, String vercode) {
        httpUtil.login(usr, passwd, vercode, new Callback() {
            @Override@EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                error("登录失败，请检查网络连接");
            }

            @Override@EverythingIsNonNull
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    httpUtil.login2(new Callback() {
                        @Override@EverythingIsNonNull
                        public void onFailure(Call call, IOException e) {
                            error("登陆失败，请检查网络连接");
                        }

                        @Override@EverythingIsNonNull
                        public void onResponse(Call call, Response response) throws IOException {
                            String regex = "xml";
                            Pattern p = Pattern.compile(regex);
                            if (response.body() != null) {
                                Matcher m = p.matcher(response.body().string().substring(0, 200));
                                if (response.isSuccessful() & m.find()) {
                                    getGrade();
                                } else {
                                    //登录失败
                                    error("登陆失败，教务系统繁忙");
                                }
                            }
                        }
                    });
                } else {
                    error("登录失败，教务系统繁忙");
                }
            }
        });
    }

    private void getGrade() {
        httpUtil.getGrade(xq, new Callback() {
            @Override@EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                error("成绩查询失败，请检查网络连接");
            }

            @Override@EverythingIsNonNull
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() & response.body() != null) {
                    callback.showGrade(new JsoupUtil().parseGrade(response.body().string()));
                    success();
                } else {
                    error("成绩查询失败，教务系统繁忙");
                }
            }
        });
    }

    private void error(String str) {
        if (fragment != null) {
            fragment.error(str);
        }
    }
    private void success() {
        if (fragment != null) {
            fragment.success("成绩查询成功");
        }
    }

}
