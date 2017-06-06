package com.zhongdasoft.svwtrainnet.module.exam;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.ExamFragmentPagerAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.CRUD;
import com.zhongdasoft.svwtrainnet.greendao.DaoQuery;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserPaper;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.FileUtil;
import com.zhongdasoft.svwtrainnet.util.MyProperty;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.NetManager;
import com.zhongdasoft.svwtrainnet.util.Waiting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/6/30 12:05
 * 修改人：Administrator
 * 修改时间：2016/6/30 12:05
 * 修改备注：
 */
public class TestCenterActivity extends BaseActivity {
    private WeakReference<? extends BaseActivity> wr;
    private ExamFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ExamFragmentPagerAdapter(getSupportFragmentManager(), wr);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        userName = MySharedPreferences.getInstance().getUserName(this);
        runThread();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.nettest_test_tabs;
    }

    @Override
    protected int getMTitle() {
        return R.string.testcenter;
    }

    private void runThread() {
        Waiting.show(this, getResources().getString(R.string.LoadingTest));
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetManager.isNetworkConnected(TestCenterActivity.this)) {
                    ArrayList<HashMap<String, Object>> examList = TrainNetWebService.getInstance()
                            .GetExamList(TestCenterActivity.this);
                    if (examList.size() > 0) {
                        List<UserPaper> userPaperList = new ArrayList<>();
                        UserPaper up;
                        String DeviceDBPath = MyProperty.loadConfig().getProperty("DeviceDBPath");
                        for (HashMap<String, Object> examMap : examList) {
                            up = new UserPaper();
                            UUID uuid = UUID.randomUUID();
                            up.setUserName(userName);
                            up.setDbName(uuid.toString());
                            up.setPlanId(null != examMap.get("PlanId") ? examMap.get("PlanId").toString() : "");
                            up.setPaperId(examMap.get("PaperId").toString());
                            up.setPaperScore(Integer.parseInt(examMap.get("PaperScore").toString()));
                            up.setBeginTime(examMap.get("BeginTime").toString());
                            up.setEndTime(examMap.get("EndTime").toString());
                            up.setDuration(Integer.parseInt(examMap.get("Duration").toString()));
                            up.setExamineeId(examMap.get("ExamineeId").toString());
                            up.setTestName(examMap.get("Name").toString());
                            up.setLeftTime(up.getDuration() * 60L);
                            up.setShowScore(Boolean.parseBoolean(examMap.get("ShowScore").toString()));
                            up.setStatus(0);
                            if (DaoQuery.getInstance().existNotDownloadUserPaper(up)) {
                                String ServerDBPath = MyProperty.getCurrentValue(getResources().getString(R.string.DBPath, up.getPaperId()));
                                if (FileUtil.getInstance().saveFileFromHttp(ServerDBPath, up.getDbName(), DeviceDBPath)) {
                                    userPaperList.add(up);
                                }
                            }
                        }
                        CRUD.getInstance().InsertUserPaper(userPaperList);
//                        for (UserPaper userPaper : userPaperList) {
//                            FileUtil.getInstance().deleteDBFile(userPaper.getDbName());
//                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Waiting.dismiss();
                        refresh();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isChanged = getIntent().getBooleanExtra("isChanged", false);
        if (isChanged) {
            refresh();
        }
    }

    private void refresh() {
        if (null != pagerAdapter) {
            pagerAdapter.setUpdateFlag();
            pagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
