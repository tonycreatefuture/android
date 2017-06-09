package com.zhongdasoft.svwtrainnet.module.exam;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.TestKey;
import com.zhongdasoft.svwtrainnet.greendao.DaoQuery;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserPaper;
import com.zhongdasoft.svwtrainnet.util.DialogUtil;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.WebserviceUtil;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TestFragment extends Fragment {
    private List<UserPaper> userPaperList;
    private int position;
    private String userName;
    private ArrayList<HashMap<String, Object>> listItemRunning;
    private ArrayList<HashMap<String, Object>> listItemFinished;
    private BaseListViewAdapter mAdapter;
    private BaseActivity mActivity;
    private WeakReference<? extends BaseActivity> wr;
    private ListView lv;
    private String currentTime;

    public static TestFragment newInstance(int position, WeakReference<? extends BaseActivity> wr) {
        TestFragment fragment = new TestFragment();
        fragment.position = position;
        fragment.wr = wr;
        fragment.mActivity = wr.get();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.nettest_test_center, null);
        lv = (ListView) v.findViewById(R.id.listview_testcenter);
        userName = MySharedPreferences.getInstance().getUserName();
        userPaperList = DaoQuery.getInstance().getUserPaperList(userName, 0 == position ? false : true);
        setListView();
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setListView() {
        if (position == 1) {
            if (null == listItemFinished) {
                listItemFinished = new ArrayList<>();
            } else {
                listItemFinished.clear();
            }
            for (UserPaper up : userPaperList) {
                int isFinished = getTestStatus(up);
                int status;
                if (isFinished >= TestKey.TEST_FINISHED) {
                    if (null == up.getScoreInfo()) {
                        up.setScore(DaoQuery.getInstance().getUserQuestionScore(up.getDbName()));
                        up.setScoreInfo(DaoQuery.getInstance().getUserQuestionScoreInfo(up.getDbName()));
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("info", up.getTestName());
                    // 未成功提交成绩
                    status = up.getStatus();
                    if (status <= TestKey.TEST_FINISHED) {
                        map.put("infoDesc", mActivity.getResources().getString(R.string.submitScore));
                    } else {
                        map.remove("infoDesc");
                    }
                    map.put("info1", "卷面总分：" + up.getPaperScore() + "分");
                    if (up.getShowScore()) {
                        map.put("info2", "成绩：" + up.getScore());
                    }
                    String realBeginTime;
                    String realEndTime;
                    if (null != up.getRealEndTime()) {
                        realEndTime = up.getRealEndTime().replace("T", " ").substring(0, 16);
                    } else {
                        realEndTime = up.getEndTime().replace("T", " ").substring(0, 16);
                    }
                    if (null != up.getRealBeginTime()) {
                        realBeginTime = up.getRealBeginTime().replace("T", " ").substring(0, 16);
                    } else {
                        realBeginTime = up.getBeginTime().replace("T", " ").substring(0, 16);
                    }
                    map.put("info3", "考试时间：" + realBeginTime + "-" + realEndTime);
                    map.put("noRight", "");
                    map.put("key", up.getId());
                    listItemFinished.add(map);
                }
            }
            setNoListItem(listItemFinished, R.string.no_finishedTest);
            mAdapter = new BaseListViewAdapter(mActivity, listItemFinished);
        } else {
            if (null == listItemRunning) {
                listItemRunning = new ArrayList<>();
            } else {
                listItemRunning.clear();
            }
            for (UserPaper up : userPaperList) {
                int status = getTestStatus(up);
                if (status < TestKey.TEST_FINISHED) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("info", up.getTestName());
                    if (status == TestKey.TEST_PREPARED) {
                        map.put("infoDesc", "考试即将开始");
                    } else {
                        map.put("infoDesc", "考试正在进行");
                    }
                    map.put("info1", "卷面总分：" + up.getPaperScore() + "分");
                    map.put("info2", "考试时长：" + up.getDuration() + "分钟");
                    map.put("info3", "开始时间：" + up.getBeginTime().replace("T", " ").substring(0, 16) + "-" + up.getEndTime().replace("T", " ").substring(0, 16));
                    map.put("noRight", "");
                    map.put("key", up.getId());
                    listItemRunning.add(map);
                }
            }
            setNoListItem(listItemRunning, R.string.no_runningTest);
            mAdapter = new BaseListViewAdapter(mActivity, listItemRunning);
        }
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.GRAY));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                UserPaper up;
                if (position == 0) {
                    if (listItemRunning.size() <= 0 || (listItemRunning.size() == 1 && listItemRunning.get(0).containsKey("img_noData"))) {
                        return;
                    }
                    up = DaoQuery.getInstance().findUserPaperByKey(listItemRunning.get(arg2).get("key"));
                    currentTime = MySharedPreferences.getInstance().getCurrentTime();
                    int status = getTestStatus(up);
                    switch (status) {
                        case TestKey.TEST_PREPARED:
                            int iSeconds = getMinutes(up.getBeginTime());
                            if (iSeconds <= TestKey.ENTER_TIME) {
                                showInfo(up);
                            } else {
                                if (iSeconds < 600) {
                                    ToastUtil.show(mActivity,
                                            "离考试还有" + iSeconds / 60 + "分钟" + (iSeconds - 60 * (iSeconds / 60)) + "秒，请耐心等候！");
                                } else {
                                    ToastUtil.show(mActivity,
                                            "离考试还有" + iSeconds / 60 + "分钟，请耐心等候！");
                                }
                            }
                            break;
                        case TestKey.TEST_RUNNING:
                            showInfo(up);
                            break;
                        case TestKey.TEST_FINISHED:
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isChanged", true);
                            Intent intent = new Intent(mActivity, TestCenterActivity.class);
                            mActivity.startActivity(intent, bundle);
                            mActivity.finish();
                            break;
                    }
                } else {
                    if (listItemFinished.size() <= 0 || (listItemFinished.size() == 1 && listItemFinished.get(0).containsKey("img_noData"))) {
                        return;
                    }
                    up = DaoQuery.getInstance().findUserPaperByKey(listItemFinished.get(arg2).get("key"));
                    MySharedPreferences.getInstance().setStoreString("dbName", up.getDbName());
                    TextView infoDesc = (TextView) arg1.findViewById(R.id.infoDesc);
                    if (mActivity.getResources().getString(R.string.submitScore).equals(infoDesc.getText().toString())) {
                        WebserviceUtil.getInstance().submitExam(wr, up);
                    } else {
                        if (up.getShowScore()) {
                            String name = MySharedPreferences.getInstance().getName();
                            DialogUtil.getInstance().showScoreDialog(wr, name + "(" + userName + ")", up.getDbName());
                        }
                    }
                }
            }
        });
    }

    private void setNoListItem(ArrayList<HashMap<String, Object>> listItem, int resId) {
        if (listItem.size() <= 0) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("img_noData", R.drawable.nettest_no_exam);
            map.put("title_noData", getResources().getString(resId));
            map.put("noLine", "");
            map.put("noRight", "");
            listItem.add(map);
        }
    }

    private int getTestStatus(UserPaper userPaper) {
        currentTime = MySharedPreferences.getInstance().getCurrentTime();
        int iReturn = 0;
        int status = userPaper.getStatus();
        // 考试已结束
        if (status >= TestKey.TEST_FINISHED) {
            return TestKey.TEST_FINISHED;
        }
        String BeginTime = userPaper.getBeginTime().replace("T", " ");
        String EndTime = userPaper.getEndTime().replace("T", " ");
        Date dCurrentTime = StringUtil.strToDate(currentTime.replace("T", " "));
        Date dBeginTime = StringUtil.strToDate(BeginTime);
        Date dEndTime = StringUtil.strToDate(EndTime);

        boolean isPrepared = dCurrentTime.before(dBeginTime);
        boolean isRun = dCurrentTime.after(dBeginTime)
                && dCurrentTime.before(dEndTime);
        boolean isEnd = dCurrentTime.after(dEndTime);

        // 考试进行中
        if (status == TestKey.TEST_RUNNING) {
            if (isEnd) {
                return TestKey.TEST_FINISHED;
            }
            return TestKey.TEST_RUNNING;
        }
        if (isPrepared) {
            iReturn = TestKey.TEST_PREPARED;
        } else if (isRun) {
            iReturn = TestKey.TEST_RUNNING;
        } else if (isEnd) {
            iReturn = TestKey.TEST_FINISHED;
        }
        return iReturn;
    }

    private int getMinutes(String beginTime) {
        currentTime = MySharedPreferences.getInstance().getCurrentTime();
        Date dCurrentTime = StringUtil
                .strToDate(currentTime.replace("T", " "));
        String BeginTime = beginTime.replace("T", " ");
        Date dBeginTime = StringUtil.strToDate(BeginTime);
        int iSeconds;
        try {
            iSeconds = StringUtil.secondsBetween(dCurrentTime, dBeginTime);
        } catch (ParseException e) {
            iSeconds = 0;
            e.printStackTrace();
        }
        return iSeconds;
    }

    /**
     * listview中点击按键弹出对话框
     */
    public void showInfo(UserPaper up) {
        // 题库没有数据
        if (userPaperList.size() == 0) {
            ToastUtil.show(mActivity, "题库中没有数据，请重新登录下载题库！");
            return;
        }
        DialogUtil.getInstance().showTestDialog(wr, up);
    }
}
