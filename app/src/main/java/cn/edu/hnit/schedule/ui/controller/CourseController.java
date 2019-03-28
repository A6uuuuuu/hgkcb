package cn.edu.hnit.schedule.ui.controller;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.hnit.schedule.custom.CourseView;
import cn.edu.hnit.schedule.model.Course;
import cn.edu.hnit.schedule.repository.SettingRepository;
import cn.edu.hnit.schedule.ui.pages.main.ScheduleFragment;

import static android.content.ContentValues.TAG;

/*
    CourseController负责控制每一个页面的课程分配
    它实现了将课程数据转化为view以及分配课程颜色、课程分组的功能
 */

public class CourseController {

    //布局参数
    private static final int TEXT_PADDING_NAME = 4;
    private static final int TEXT_PADDING_PLACE = 12;
    private static final int TEXT_PADDING_TOP = 10;
    private static final int TEXT_PADDING_BOTTOM = 16;
    private static final float TEXT_SPACING = 1;
    private static final float TEXT_SIZE_PLACE = 4f;
    private static final int MARGIN = 2;
    private static final int RADIUS = 12;
    private static final int HEIGHT = 60;

    private Context context;
    private int currentWeek;
    private int width;
    private List<CourseView> courses = new ArrayList<>();
    private List<CourseView> _courses = new ArrayList<>();
    private Map<String, Integer> courseColor = new HashMap<>();
    private Integer index = 0;
    private String[] darkColors = {"","#bdd1dd", "#d99ea2", "#9ac6c0", "#dbc57a", "#8da7c6", "#a8b4e2", "#89b1a2", "#d4b04e", "#82bbad", "#eadacb"};
    private String[] colors = {"","#d0e6f4", "#fdb7bc", "#b4e9e2", "#fde38c", "#acd9f3", "#becbff", "#a7d7c5", "#facf5a", "#99ddcc", "#ffecda"};

    public CourseController(ScheduleFragment fragment) {
        this.currentWeek = fragment.getWeek();       //此处的currentWeek指当前页面的周数
        this.context = fragment.getContext();
        this.width = getWidth();
        loadCourse();
    }

    /*
        课程由三个view组成，一个CardView作为背景，一个TextView展示课程名称，一个TextView展示课程地点
        通过设置GridLayout.Spec来确定课程在表中的位置
     */
    private void addCourse(int id, String courseName, String classRoom, int row, int week, int span, boolean inCurrentWeek) {
        //View设置
        CourseView course = new CourseView(context);
        course.setCardElevation(0);
        course.setRadius(RADIUS);
        course.setId(id);
        GridLayout.Spec rowSpec = GridLayout.spec(row, span);
        GridLayout.Spec columnSpec = GridLayout.spec(week, 1);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
        params.height = span * dp2px(HEIGHT);
        params.width = width;
        params.setMargins(dp2px(MARGIN), dp2px(MARGIN),dp2px(MARGIN),dp2px(MARGIN));
        course.setLayoutParams(params);

        //课程名称设置
        TextView name = new TextView(context);
        SettingRepository mRepository = new SettingRepository(context);
        name.setTextSize(TypedValue.COMPLEX_UNIT_SP, mRepository.getSeekBarOption("ui_text_size"));
        name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        name.setPadding(TEXT_PADDING_NAME, TEXT_PADDING_TOP, TEXT_PADDING_NAME, 0);
        CardView.LayoutParams nameParams = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameParams.gravity = Gravity.TOP;
        name.setLayoutParams(nameParams);
        name.setLineSpacing(0, TEXT_SPACING);

        //课程地点设置
        TextView place = new TextView(context);
        place.setTextSize(TypedValue.COMPLEX_UNIT_PX, ((float) width - TEXT_PADDING_PLACE * 2) / TEXT_SIZE_PLACE);
        place.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        place.setPadding(TEXT_PADDING_PLACE, 0, TEXT_PADDING_PLACE, TEXT_PADDING_BOTTOM);
        place.setText(classRoom);
        CardView.LayoutParams placeParams = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        placeParams.gravity = Gravity.BOTTOM;
        place.setLayoutParams(placeParams);

        //添加至List
        if (inCurrentWeek) {
            name.setText(courseName);
            course.addView(name);
            course.addView(place);
            if (mRepository.getSwitchOption("ui_dark_theme")) {
                course.setCardBackgroundColor(Color.parseColor(darkColors[getColor(courseName)]));
            } else {
                course.setCardBackgroundColor(Color.parseColor(colors[getColor(courseName)]));
            }
            courses.add(course);
        } else {
            String str = "[非本周]\n" + courseName;
            name.setText(str);
            course.addView(name);
            course.addView(place);
            if (mRepository.getSwitchOption("ui_dark_theme")) {
                course.setCardBackgroundColor(Color.parseColor("#b6b6b6"));
            } else {
                course.setCardBackgroundColor(Color.parseColor("#efefef"));
            }
            _courses.add(course);
        }
    }

    private void loadCourse() {
        List<Course> courses = LitePal.findAll(Course.class);
        if (!courses.isEmpty()) {
            for (Course course : courses) {
                String[] t = course.getTime().split(" ", 2);
                int time[] = handleCourseTime(t);
                String classRoom = handleClassRoom(course.getPlace());
                addCourse(course.getId(), course.getName(), classRoom, time[0], time[1], time[2], inCurrentWeek(t[1]));
                //Log.d(TAG, "loadCourse: " + course.getName() + "inCurrentWeeks: " + inCurrentWeek(t[1]));
            }
        }
    }

    private boolean inCurrentWeek(String courseTime) {
        String[] weeks = courseTime.split("\\)\\(", 2);
        weeks[0] = weeks[0].replace("(", "")
                .replace(")", "")
                .replace("周", "");
        Pattern r1 = Pattern.compile(",");
        Pattern r2 = Pattern.compile("-");
        Matcher m = r1.matcher(weeks[0]);
        int limit = 1;
        while (m.find()) {
            limit++;
        }
        String _weeks[] = weeks[0].split(",", limit);
        for (String week : _weeks) {
            m = r2.matcher(week);
            if (m.find()) {
                String[] range = week.split("-", 2);
                if (currentWeek >= Integer.parseInt(range[0]) & currentWeek <= Integer.parseInt(range[1])) {
                    return true;
                }
            } else if (Integer.parseInt(week) == currentWeek) {
                return true;
            }
        }
        return false;
    }

    //处理课程时间
    private int[] handleCourseTime(@NonNull String[] courseTime) {
        int[] time = new int[3];
        int _time = Integer.parseInt(courseTime[0]);
        int length = courseTime[0].length();
        int week = _time / pow(length - 1) % 10;
        int ord = (_time / pow(length - 2) % 10)*10 + (_time / pow(length - 3) % 10);
        int span = (_time / 10 % 10 * 10 + _time % 10) - ord + 1;
        //Log.d(TAG, "handleCourseTime: Length:" + length + "  ord:" + ord + "  span:" + span);
        time[0] = ord;
        time[1] = week;
        time[2] = span;
        return time;
    }

    //处理教室信息，过滤一些不必要的字符
    private String handleClassRoom(String classRoom) {
        classRoom = classRoom.replace("（", "")
                .replace("(", "")
                .replace("）", "")
                .replace(")", "")
                .replace(" ", "")
                .replace("高级情景语音室", "")
                .replace("多媒体", "")
                .replace("语音室", "");
        return classRoom;
    }

    //分配颜色
    private Integer getColor(String courseName) {
        if (courseColor.containsKey(courseName)) {
            return courseColor.get(courseName);
        } else {
            index++;
        }
        courseColor.put(courseName, index);
        return index;
    }

    public List<CourseView> getCourses() {
        return courses;
    }

    public List<CourseView> getCourses_() {
        return _courses;
    }

    /*
        以下为工具类方法
     */
    private int pow(int num) {
        int m = 1;
        for (int i = 0; i < num; i++) {
            m = m *10;
        }
        return m;
    }

    private int getWidth() {
        return (context.getResources().getDisplayMetrics().widthPixels - dp2px(48))/7;
    }

    private int dp2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
