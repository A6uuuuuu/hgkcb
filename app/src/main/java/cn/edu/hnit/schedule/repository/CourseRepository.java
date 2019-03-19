package cn.edu.hnit.schedule.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.litepal.LitePal;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.hnit.schedule.model.Course;
import cn.edu.hnit.schedule.ui.pages.main.GetCourseDialog;
import cn.edu.hnit.schedule.util.HttpUtil;
import cn.edu.hnit.schedule.util.JsoupUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;

public class CourseRepository {

    private HttpUtil httpUtil = new HttpUtil();
    private GetCourseDialog fragment;
    private GetVercodeCallback callback;

    public CourseRepository(GetCourseDialog fragment) {
        this.fragment = fragment;
        callback = fragment;
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
                dismiss();
            }

            @Override@EverythingIsNonNull
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    httpUtil.login2(new Callback() {
                        @Override@EverythingIsNonNull
                        public void onFailure(Call call, IOException e) {
                            error("登陆失败，请检查网络连接");
                            dismiss();
                        }

                        @Override@EverythingIsNonNull
                        public void onResponse(Call call, Response response) throws IOException {
                            String regex = "xml";
                            Pattern p = Pattern.compile(regex);
                            if (response.body() != null) {
                                Matcher m = p.matcher(response.body().string().substring(0, 200));
                                if (response.isSuccessful() & m.find()) {
                                    getInfo();
                                } else {
                                    //登录失败
                                    error("登陆失败，教务系统繁忙");
                                    dismiss();
                                }
                            }
                        }
                    });
                } else {
                    error("登录失败，教务系统繁忙");
                    dismiss();
                }
            }
        });
    }

    //获取用户学院和班级,如果不按学院班级检索会大大减慢解析速度
    private void getInfo() {
        httpUtil.getTarget(new Callback() {
            @Override@EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                error("获取课表失败，请检查网络连接");
                dismiss();
            }

            @Override@EverythingIsNonNull
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() & response.body() != null) {
                    String r = response.body().string();
                    String academy = new JsoupUtil().parseForAcademy(r);
                    String className = new JsoupUtil().parseForClassName(r);
                    Log.d("parseForTarget", "onResponse: " + academy + " " + className);
                    getCourses(academy, getCurrentXq(), className);
                } else {
                    error("获取课表失败，教务系统繁忙");
                    dismiss();
                }
            }
        });
    }

    //获取课程表
    private void getCourses(String targetAcademy, String targetXq, String targetClassName) {
        httpUtil.getCourses( targetAcademy, targetXq, new Callback() {
            @Override@EverythingIsNonNull
            public void onFailure(Call call, IOException e) {
                error("获取课表失败，请检查网络连接");
                dismiss();
            }

            @Override@EverythingIsNonNull
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    JsoupUtil jsoupUtil = new JsoupUtil();
                    LitePal.deleteAll(Course.class, "id > ?", "0");
                    if (response.body() != null) {
                        if (jsoupUtil.parseResponse(response.body().string(), targetClassName)) {
                            success();
                            refreshUi();
                            dismiss();
                        }
                    } else {
                        error("获取课表失败，教务系统繁忙");
                        dismiss();
                    }
                }
            }
        });
    }

    //获取当前学期
    private String getCurrentXq() {
        int xq = 1;
        Date d = new Date();
        SimpleDateFormat m_ft = new SimpleDateFormat("M", Locale.CHINA);
        String m = m_ft.format(d);
        SimpleDateFormat y_ft = new SimpleDateFormat("yyyy", Locale.CHINA);
        String y = y_ft.format(d);
        int month = Integer.parseInt(m);
        int year = Integer.parseInt(y);
        if (month < 7) {
            xq = 2;
        }
        return (year - 1) + "-" + year + "-" + xq;
    }

    public interface GetVercodeCallback {
        void setVercode(Bitmap bmp);
    }

    /*
        这里插入一点UI操作
     */
    private void error(String str) {
        if (fragment != null) {
            fragment.error(str);
        }
    }
    private void success() {
        if (fragment != null) {
            fragment.success("同步课表成功");
        }
    }
    private void dismiss() {
        if (fragment != null) {
            fragment.dismiss();
        }
    }
    private void refreshUi() {
        if (fragment != null) {
            fragment.refreshUi();
        }
    }

}
