package cn.edu.hnit.schedule.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.edu.hnit.schedule.model.Time;
import cn.edu.hnit.schedule.repository.DateRepository;

public class HeaderViewModel extends AndroidViewModel {

    private Time time;
    private int showWeek;
    private DateRepository mRepository;

    public HeaderViewModel(@NonNull Application application) {
        super(application);
        this.mRepository = new DateRepository(application);
        setTime();
        this.showWeek = time.getWeek();
    }

    //返回当前周课表
    public void back2CurrentWeek() {
        setTime();
        showWeek = mRepository.getCurrentWeek();
    }

    //如果不在当前周的课表
    public void notInCurrentWeek(int otherWeek) {
        time.setDate("返回本周");
        showWeek = otherWeek;
    }

    public void setTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("M月d日", Locale.CHINA);
        time = new Time(dateFormat.format(date), mRepository.getCurrentWeek());
    }

    public String getDate() {
        if (time == null) {
            setTime();
        }
        return time.getDate();
    }

    //用来显示的周数
    public String getWeek() {
        return "第" + showWeek + "周";
    }

}
