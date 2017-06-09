package com.zhongdasoft.svwtrainnet.module.trainnormal;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ActivityKey;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.DialogUtil;
import com.zhongdasoft.svwtrainnet.util.ListViewUtil;
import com.zhongdasoft.svwtrainnet.util.NetManager;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class NormalCourseContentActivity extends BaseActivity {
    private WeakReference<? extends BaseActivity> wr;
    private HashMap<String, Object> courseContentList;
    private ListView lv;
    private ArrayList<HashMap<String, Object>> listItem;
    private String className;
    private String courseNo;

    private static final int REFRESH_COMPLETE = 0X110;
    private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        courseNo = getIntent().getStringExtra("item");
        className = getIntent().getStringExtra("className");
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        Waiting.show(this, getResources().getString(R.string.Loading));
        new Thread(new NormalCourseContentThread()).start();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_normal_coursecontent;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_normalcourselist;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setListView() {
        if (null == listItem) {
            listItem = new ArrayList<>();
        } else {
            listItem.clear();
        }
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(courseContentList.get("CourseName")), ListViewUtil.PaperTitle);

        ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_content), ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(courseContentList.get("Content")), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_target), ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(courseContentList.get("Objectives")), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_overview), ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(courseContentList.get("Overview")), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_format), ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(courseContentList.get("TrainingFormat")), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_days), ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(courseContentList.get("Days")), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_position), ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(courseContentList.get("SuitablePositions")), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_director), ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(courseContentList.get("Director")), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_contact), ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(courseContentList.get("Contact")), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_remark), ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(courseContentList.get("Remark")), ListViewUtil.PaperInfo);
        TextView tvTitle = (TextView) findViewById(R.id.trainnet_title);
        if (className.equalsIgnoreCase(ActivityKey.Apply)) {
            tvTitle.setText(R.string.title_normalapply);
            ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_apply), ListViewUtil.PaperEvent);
        } else {
            tvTitle.setText(R.string.title_normalcourselist);
            String suitMeCourse = TrainNetApp.getCache().getAsString(CacheKey.SuitMeCourse);
            if (null != suitMeCourse && suitMeCourse.contains(courseContentList.get("CourseNo").toString() + ",,")) {
                ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_apply), ListViewUtil.PaperEvent);
            } else {
                listItem.get(listItem.size() - 1).remove("paperEvent");
            }
        }

        BaseListViewAdapter mAdapter = new BaseListViewAdapter(this, listItem);
        lv = (ListView) findViewById(R.id.listview_coursecontent);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (listItem.get(arg2).containsKey("paperEvent")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HashMap<String, Object> myApplyList = TrainNetWebService.getInstance().ApplyOfflineCourse(NormalCourseContentActivity.this, courseNo);
                            final Integer ReturnCode = Integer.parseInt(myApplyList.get(getResources().getString(R.string.ReturnCode)).toString());
                            final String message = myApplyList.get(getResources().getString(R.string.Message)).toString();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String text;
                                    if (ReturnCode == 0) {
                                        text = "申请课程成功！";
                                    } else {
                                        text = "申请课程失败，原因：" + message;
                                    }
                                    DialogUtil.getInstance().showDialog(wr, getResources().getString(R.string.tips), text, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AlertDialog alertDialog = (AlertDialog) v.getTag();
                                            alertDialog.dismiss();
                                        }
                                    }, null);
                                }
                            });
                        }
                    }).start();
                }
            }
        });
    }

    public class NormalCourseContentThread implements Runnable {

        public NormalCourseContentThread() {
        }

        @Override
        public void run() {
            String CourseContentRefresh = TrainNetApp.getCache().getAsString(CacheKey.CourseContentRefresh + courseNo);
            if (!NetManager.isNetworkConnected(NormalCourseContentActivity.this) && StringUtil.isNullOrEmptyOrEmptySet(CourseContentRefresh)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Waiting.dismiss();
                        ToastUtil.show(NormalCourseContentActivity.this, getResources().getString(R.string.showByNetError));
                    }
                });
                return;
            }
            if (!StringUtil.isNullOrEmptyOrEmptySet(CourseContentRefresh)) {
                courseContentList = TrainNetApp.getGson().fromJson(CourseContentRefresh, new TypeToken<HashMap<String, Object>>() {
                }.getType());
            } else {
                courseContentList = TrainNetWebService.getInstance().GetCourse(NormalCourseContentActivity.this, courseNo);
                TrainNetApp.getCache().put(CacheKey.CourseContentRefresh + courseNo, TrainNetApp.getGson().toJson(courseContentList));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setListView();
                    Waiting.dismiss();
                }
            });
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    if (NetManager.isNetworkConnected(NormalCourseContentActivity.this)) {
                        TrainNetApp.getCache().remove(CacheKey.CourseContentRefresh + courseNo);
                        new Thread(new NormalCourseContentThread()).start();
                    } else {
                        ToastUtil.show(NormalCourseContentActivity.this, getResources().getString(R.string.refreshByNetError));
                    }
                    mSwipeLayout.setRefreshing(false);
                    break;
            }
        }
    };
}
