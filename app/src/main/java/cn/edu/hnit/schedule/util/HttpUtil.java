package cn.edu.hnit.schedule.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.annotations.EverythingIsNonNull;

/*
    这个类用来封装教务系统的登录、获取课表数据的请求

    2019/04/02 更新：
        获取课表数据有两种方式，一种是在已登录的情况下可以直接发送get请求到对应url，这种不需要获取班级和学院
        另一种则是发送post请求，这种需要班级和学院，不然会返回一张总课表，解析十分耗费时间
        此前一直使用post方式=-=，直到某个学妹找我说和超级课程表的课表不一样我才发现get的方式
        打算先留着，因为post方式可以查询全校的课表
 */

public class HttpUtil {

    private OkHttpClient client;
    private HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private static final String vercode_url = "http://jwgl.hnit.edu.cn/verifycode.servlet";
    private static final String login_url = "http://jwgl.hnit.edu.cn/Logon.do?method=logon";
    private static final String login2_url = "http://jwgl.hnit.edu.cn/Logon.do?method=logonBySSO";
    private static final String course_url_post = "http://jwgl.hnit.edu.cn/zcbqueryAction.do?method=goQueryZKbByXzbj";
    private static final String course_url_get = "http://jwgl.hnit.edu.cn/tkglAction.do?method=goListKbByXs&istsxx=no&xnxqh=";
    private static final String studentInfo_url = "http://jwgl.hnit.edu.cn/xszhxxAction.do?method=addStudentPic&tktime=";
    private static final String grade_url = "http://jwgl.hnit.edu.cn/xszqcjglAction.do?method=queryxscj";

    public HttpUtil() {
        this.client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override@EverythingIsNonNull
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override@EverythingIsNonNull
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();
    }

    public void getVercode(okhttp3.Callback callback) {
        Request request_vercode = new Request.Builder().get().url(vercode_url).build();
        client.newCall(request_vercode).enqueue(callback);
    }

    public void login(String usr, String password, String vercode, okhttp3.Callback callback) {
        FormBody loginFormBody = new FormBody.Builder()
                .add("USERNAME", usr)
                .add("PASSWORD", password)
                .add("useDogCode", "")
                .add("useDogCode", "")
                .add("RANDOMCODE", vercode)
                .add("x", "36")
                .add("y", "12")
                .build();
        Request request_login = new Request.Builder()
                .post(loginFormBody)
                .url(login_url)
                .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
                .header("Referer", "http://jwgl.hnit.edu.cn/")
                .header("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Host", "jwgl.hnit.edu.cn")
                .header("Connection", "Keep-Alive")
                .header("Pragma", "no-cache")
                .build();
        Call call_login = client.newCall(request_login);
        call_login.enqueue(callback);
    }

    //targetAcademy -> 目标学院   targetTime -> 目标学期
    public void getCourses(String targetAcademy, String targetTime, okhttp3.Callback callback) {
        FormBody courseFormBody = new FormBody.Builder()
                .add("lb", "queryzkb.jsp")
                .add("xq", "00002")
                .add("xnxqh", targetTime)
                .add("skyx", targetAcademy)
                .add("kkyx", "")
                .add("kc", "")
                .add("sknj", "")
                .add("skzy", "")
                .add("zc1", "")
                .add("zc2", "")
                .add("jc1", "")
                .add("jc2", "")
                .build();
        Request request_courses = new Request.Builder()
                .post(courseFormBody)
                .url(course_url_post)
                .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
                .header("Referer", "http://jwgl.hnit.edu.cn/jiaowu/pkgl/zkb/queryzkb.jsp?tktime=" + System.currentTimeMillis())
                .header("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept-Encoding", "deflate")
                .header("Host", "jwgl.hnit.edu.cn")
                .header("Connection", "Keep-Alive")
                .header("Pragma", "no-cache")
                .build();
        client.newCall(request_courses).enqueue(callback);
    }

    //time -> 要查询的学期
    public void getCourses_(String time, okhttp3.Callback callback) {
        Request request = new Request.Builder()
                .get().url(course_url_get + time)
                .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
                .header("Accept-Encoding", "deflate")
                .header("Accept-Language", "zh-Hans-CN, zh-Hans; q=0.8, en-US; q=0.5, en; q=0.3")
                .header("Host", "jwgl.hnit.edu.cn")
                .header("Pragma", "no-cache")
                .header("Proxy-Connection", "Keep-Alive")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void login2(okhttp3.Callback callback) {
        FormBody login2FormBody = new FormBody.Builder().build();
        Request request_login2 = new Request.Builder().post(login2FormBody).url(login2_url).build();
        client.newCall(request_login2).enqueue(callback);
    }

    public void getTarget(okhttp3.Callback callback) {
        Request request = new Request.Builder()
                .get()
                .url(studentInfo_url + System.currentTimeMillis())
                .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
                .header("Accept-Encoding", "deflate")
                .header("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Host", "jwgl.hnit.edu.cn")
                .header("Proxy-Connection", "Keep-Alive")
                .header("Referer", "http://jwgl.hnit.edu.cn/framework/new_window.jsp?lianjie=&winid=win2")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void getGrade(String time, okhttp3.Callback callback) {
        FormBody gradeFormBody = new FormBody.Builder()
            .add("kcmc", "")
            .add("kcxz", "")
            .add("kksj", time)              //time -> exp:2018-2019-1
            .add("ok", "")
            .add("xsfs", "qbcj")    //qbcj -> 全部成绩
            .build();
        Request request = new Request.Builder()
                .post(gradeFormBody)
                .url(grade_url)
                .header("Accept", "text/html, application/xhtml+xml, image/jxr, */*")
                .header("Accept-Encoding", "deflate")
                .header("Accept-Language", "zh-Hans-CN, zh-Hans; q=0.8, en-US; q=0.5, en; q=0.3")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Host", "jwgl.hnit.edu.cn")
                .header("Pragma", "no-cache")
                .header("Proxy-Connection", "Keep-Alive")
                .header("Referer", "http://jwgl.hnit.edu.cn/jiaowu/cjgl/xszq/query_xscj.jsp?tktime=" + System.currentTimeMillis())
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko")
                .build();
        client.newCall(request).enqueue(callback);
    }

}