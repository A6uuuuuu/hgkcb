package cn.edu.hnit.schedule.util;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.hnit.schedule.model.Course;

import static android.content.ContentValues.TAG;

/*
    这个类用于解析返回的数据
    jsoup解析后主要用split跟replace两个方法获取目标数据
    我不知道有没有更好的办法=-=
 */

public class JsoupUtil {

    /*
        2019/2/26   write by liuwenkiii
        以下为解析课程的方法
     */

    /*
        解析post请求课程表的响应内容

    public boolean parseResponse(String response, String targetClassName) {
        Document courseTable = Jsoup.parse(response);
        Elements course;
        List<String[]> courses = new ArrayList<>();
        int targetIndex = 0;
        try {
            for (int mClass = 3; mClass < 1000; mClass++) {
                //如果目标索引不为零就跳出循环，即在解析完目标班级课程之后跳出循环
                if (targetIndex != 0) break;
                //先寻找目标班级的位置
                course = courseTable.select("#kbtable > tbody > tr:nth-child(" + mClass + ") > td:nth-child(1)");
                //如果是要找的班级，就遍历当前行，把所有课程取出来
                if (course.text().equals(targetClassName)) {
                    for (int mCourse = 1; mCourse < 37; mCourse++) {
                        course = courseTable.select("#kbtable > tbody > tr:nth-child(" + mClass + ") > td:nth-child(" + mCourse + ")");
                        Log.d("jsoup", "parseResponse: " + course.toString());
                        //设置目标索引，sorry可能当时写到这里我有些困了，但是它能用，那么这里就算它ok（留着以后优化用
                        if (mCourse != 1 & !course.text().equals(targetClassName)) {
                            targetIndex = mClass;
                        }
                        //好了我觉得这里if应该有点多余，但是它也没造成什么bug，对吧？
                        if (mClass == targetIndex) {
                            //如果有内容就解析并添加至List
                            if (course.hasText()) {
                                courses.add(parseCourse(course.html()));
                                //Log.d(TAG, "添加到list: " + Arrays.toString(parseCourse(course.html())));
                            }
                        }
                    }
                }
            }
            //保存课程
            saveCourse(courses);
            return true;
        } catch (Exception e) {
            Log.d("JsoupUtil", "parseResponse: ");
        }
        return false;
    }
     */

    //这个用来解析get方式获取的课表
    public boolean parseResponse(String response) {
        Document courseTable = Jsoup.parse(response);
        Elements course;
        //遍历课表行列存储数据
        for (int row = 2; row <= 6; row++) {
            for (int col = 2; col <= 8; col++) {
                course = courseTable.select("#kbtable > tbody > tr:nth-child(" + row + ") > td:nth-child(" + col + ") > div:nth-child(2)");
                Log.d(TAG, Arrays.toString(parseCourse(course.html())));
                //如果有内容就解析并保存
                if (course.hasText()) {
                    saveCourse(Objects.requireNonNull(parseCourse(course.html())), col - 1);
                }
            }
        }
        return true;
    }

    //再分一个出来方便读
    private String[] parseCourse(String course) {
        try {
            //先过滤一些不必要的字符
            course = filterCourse(course);
            return getCourseInfo(course);
        } catch (Exception e) {
            Log.d("parseCourse", "parseCourse error" + e);
        }
        return null;
    }

    //保存课程
    private void saveCourse(String[] courseInfo, int weekday) {
        if (courseInfo.length > 1) {
            save(courseInfo, weekday);
        }
    }

    //单独写一个save方便递归，因为有一个格子多节课的情况存在
    private void save(String[] courseInfo, int weekday) {
        if (courseInfo.length > 1) {
            String time = transformCourseTime(courseInfo[3], weekday);
            Course course = new Course(courseInfo[0], courseInfo[2], time, courseInfo[4]);
            if (courseInfo.length > 5) {
                String[] _courseInfo = new String[courseInfo.length - 5];
                System.arraycopy(courseInfo, 5, _courseInfo, 0, courseInfo.length - 5);
                save(_courseInfo, weekday);
                Log.d(TAG, "保存课程: " + courseInfo[0] + courseInfo[2] + time + courseInfo[4]);
            }
            try {
                course.save();
            } catch (Exception e) {
                Log.d(TAG, "save: " + e);
            }
        }
    }

    //post方法和get方法获取的数据不一样，为了统一要将数据转换一下，这里统一成post方式的数据，因为如果要改的话要动很多地方QwQ
    private String transformCourseTime(String info, int weekday) {
        Log.d(TAG, "transformCourseTime: " + info);
        String[] t = info
                .split("\\[", 2)[1]
                .replace("节]", "")
                .split("-", 2);
        String _info = info.replace("[", ")(")
                            .replace("]", ")");
        Log.d(TAG, "transformCourseTime: " + weekday + t[0] + t[1] + " (" + _info);
        return weekday + t[0] + t[1] + " (" + _info;
    }

    //过滤不必要的字符
    private String filterCourse(String course) {
        course = course.replace("&nbsp;", "")
                .replace("<hr style=\"color:red\" SIZE=\"1\"><br>", "")
                .replace("\n", "")
                .replace("<nobr>", "")
                .replace("</nobr>", "")
                .replace("北区", "北")
                .replace(" ", "");
        Log.d("parseCourse", "filter course: " + course);
        return course;
    }

    //分割课程信息
    private String[] getCourseInfo(String course) {
        int limit = 0;
        String pattern = "<br>";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(course);
        while (m.find()) {
            limit++;
        }
        return course.split("<br>", limit + 1);
    }

    /*
        解析得到班级
    public String parseForClassName(String response) {
        Document info = Jsoup.parse(response);
        Elements className = info.select("#xjkpTable > tbody > tr:nth-child(3) > td:nth-child(4)");
        Log.d("JsoupUtil", "parseClassName: " + className.text());
        return className.text().replace("班级：", "");
    }
    */

    /*
        解析得到学院
    public String parseForAcademy(String response) {
                    skyx    上课院系：
                                    00005       建筑工程与艺术设计学院
                                    00004       机械工程学院
                                    00003       计算机与信息科学学院
                                    00002       电气与信息工程学院
                                    00001       经济与管理学院
                                    00010       安全与环境工程学院
                                    00011       材料与化学工程学院
                                    00012       外国语学院
                                    100086      数理科学与能源工程学院
        Document info = Jsoup.parse(response);
        Elements e = info.select("#xjkpTable > tbody > tr:nth-child(3) > td:nth-child(1)");
        Log.d("JsoupUtil", "parseAcademy: " + e.text());
        String academy = e.text().replace("院系：", "");
        switch (academy) {
            case "计算机与信息科学学院":
                academy = "00003";
                break;
            case "经济与管理学院":
                academy = "00001";
                break;
            case "电气与信息工程学院":
                academy = "00002";
                break;
            case "机械工程学院":
                academy = "00004";
                break;
            case "建筑工程与艺术设计学院":
                academy = "00005";
                break;
            case "安全与环境工程学院":
                academy = "00010";
                break;
            case "材料与化学工程学院":
                academy = "00011";
                break;
            case "外国语学院":
                academy = "00012";
                break;
            case "数理科学与能源工程学院":
                academy = "100086";
                break;
        }
        return academy;
    }
     */

    /*
        2019/3/30   write by liuwenkiii
        以下为解析成绩的方法
     */
    public Map parseGrade(String response) {
        Document gradeTable = Jsoup.parse(response);
        Elements courses = gradeTable.getElementsByClass("smartTr");
        Map<String, String> data = new HashMap<>();
        for (Element course : courses) {
            String name = course.child(4).text();
            String grade = course.child(5).text();
            data.put(name, grade);
        }
        return data;
    }
}
