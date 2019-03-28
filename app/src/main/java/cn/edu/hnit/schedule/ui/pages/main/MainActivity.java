package cn.edu.hnit.schedule.ui.pages.main;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import cn.edu.hnit.schedule.R;
import cn.edu.hnit.schedule.custom.MyActivity;
import cn.edu.hnit.schedule.databinding.ActivityMainBinding;
import cn.edu.hnit.schedule.repository.DateRepository;
import cn.edu.hnit.schedule.service.KeepAliveService;
import cn.edu.hnit.schedule.ui.adapter.PagerAdapter;

public class MainActivity extends MyActivity implements HeaderFragment.BackToCurrentWeekListener {

    private PagerAdapter adapter;
    private ViewPager viewPager;
    private ActivityMainBinding mBinding;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setBackgroundColor(getBackgroundColor());
        initSchedule();
        initService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadAdapter();
        Log.d(TAG, "onResume");
    }

    @Override
    public void refreshUi() {
        reloadAdapter();
        initStatusBar();
        mBinding.setBackgroundColor(getBackgroundColor());
    }

    @Override
    public void go2CurrentWeek() {
        viewPager.setCurrentItem(new DateRepository(getApplicationContext()).getCurrentWeek());
    }

    //服务
    public void initService() {
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(getJobInfo());
    }

    //初始化
    private void initSchedule() {
        viewPager = mBinding.schedule;
        FragmentManager fragmentManager = getSupportFragmentManager();
        HeaderFragment headerFragment = (HeaderFragment) fragmentManager.findFragmentById(R.id.fragment_header);

        //ViewPager设置adapter
        adapter = new PagerAdapter(fragmentManager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i != new DateRepository(getApplicationContext()).getCurrentWeek() & headerFragment != null) {
                    headerFragment.onOtherWeek(i);
                } else if (headerFragment != null) {
                    headerFragment.back2CurrentWeek();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        //判断周数设置初始页面
        int currentWeek = new DateRepository(getApplicationContext()).getCurrentWeek();
        if (currentWeek <= 22 & currentWeek > 0) {
            viewPager.setCurrentItem(currentWeek);
        } else {
            viewPager.setCurrentItem(0);
        }
    }

    //刷新课表数据
    private void reloadAdapter() {
        runOnUiThread(() -> {
            adapter.reload();
            adapter.notifyDataSetChanged();
        });
    }

    private JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(this, KeepAliveService.class));
        builder.setMinimumLatency(10 * 1000);
        builder.setOverrideDeadline(30 * 1000);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        return builder.build();
    }

}
