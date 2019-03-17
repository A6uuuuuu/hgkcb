package cn.edu.hnit.schedule.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.edu.hnit.schedule.ui.pages.main.ScheduleFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int mChildCount = 0;
    private List<Fragment> fragments = new ArrayList<>();
    private static final int MAX_WEEK = 23;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        for (int i = 0; i < MAX_WEEK; i++) {
            ScheduleFragment scheduleFragment = new ScheduleFragment();
            scheduleFragment.setWeek(i);
            fragments.add(i, scheduleFragment);
        }
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        if ( mChildCount > 0) {
            // 这里利用判断执行若干次不缓存，刷新
            mChildCount --;
            // 返回这个是强制ViewPager不缓存，每次滑动都刷新视图
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }

    public void reload() {
        fragments.clear();
        for (int i = 0; i < 23; i++) {
            ScheduleFragment scheduleFragment = new ScheduleFragment();
            scheduleFragment.setWeek(i);
            fragments.add(i, scheduleFragment);
        }
    }

}
