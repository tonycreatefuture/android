package com.zhongdasoft.svwtrainnet.module.trainonline;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.adapter.MeetingListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class MeetingActivity extends BaseActivity {
    private WeakReference<? extends BaseActivity> wr;
    private ArrayList<HashMap<String, Object>> courseList;
    private ArrayList<HashMap<String, Object>> listItem;
    private MeetingListViewAdapter mAdapter;
    private ListView lv;
    private boolean isCached = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wr = new WeakReference<>(this);
        listItem = new ArrayList<>();
        isCached = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isCached) {
                    String MeetingCourseRefresh = TrainNetApp.getCache().getAsString(CacheKey.MeetingCourseRefresh);
                    if (!StringUtil.isNullOrEmpty(MeetingCourseRefresh)) {
                        courseList = TrainNetApp.getGson().fromJson(MeetingCourseRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                        }.getType());
                    } else {
                        courseList = TrainNetWebService.getInstance().MeetList(MeetingActivity.this);
                        TrainNetApp.getCache().put(CacheKey.MeetingCourseRefresh, TrainNetApp.getGson().toJson(courseList));
                    }
                } else {
                    courseList = TrainNetWebService.getInstance().MeetList(MeetingActivity.this);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Waiting.dismiss();
                        setListView();
                    }
                });
            }
        }).start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_train_meeting;
    }

    @Override
    protected int getMTitle() {
        return R.string.Meeting;
    }

    private void setListView() {
        setData();
        if (listItem.size() <= 0) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("img_noData", R.drawable.trainnet_no_img);
            map.put("noLine", "");
            listItem.add(map);
        }
        mAdapter = new MeetingListViewAdapter(wr, listItem);
        lv = (ListView) findViewById(R.id.listview_train_meeting);
        lv.setAdapter(mAdapter);
        lv.setSelector(R.color.transparent);
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
//                if (listItem.size() > 0 && listItem.get(pos).containsKey("select_event")) {
//                    boolean isOpen = false;
//                    if (listItem.get(pos).containsKey("info1")) {
//                        isOpen = true;
//                    }
//                    for (HashMap<String, Object> map : listItem) {
//                        map.remove("info1");
//                        map.remove("info2");
//                        map.remove("info3");
//                        map.remove("info4");
//                        map.remove("info5");
//                        map.remove("info6");
//                    }
//                    if (!isOpen) {
//                        listItem.get(pos).put("info1", "课程名称：" + listItem.get(pos).get("courseName"));
//                        listItem.get(pos).put("info2", "培训时间：" + listItem.get(pos).get("dateBegin") + "至" + listItem.get(pos).get("dateEnd"));
//                        listItem.get(pos).put("info3", "主讲人：" + listItem.get(pos).get("teacher"));
//                        listItem.get(pos).put("info4", "课程简介：" + listItem.get(pos).get("instruction"));
//                        listItem.get(pos).put("info5", "您要准备的设备：");
//                        listItem.get(pos).put("info6", "其他说明：" + listItem.get(pos).get("remark"));
//                    }
//                    mAdapter.notifyDataSetChanged();
//                }
            }
        });
    }

    private void setData() {
        HashMap<String, Object> map = null;
        for (HashMap<String, Object> course : courseList) {
            if ("ApiMeet".equals(course.get(getResources().getString(R.string.ParentNode)))) {
                map = new HashMap<>();
                map.put("applyId", course.get("ApplyId").toString());
                map.put("planId", course.get("PlanId").toString());
                map.put("coursewareId", course.get("CoursewareId").toString());
                map.put("courseName", course.get("CourseName").toString());
                map.put("instruction", null == course.get("Instruction") ? "" : course.get("Instruction").toString());
                map.put("title", course.get("CourseName").toString());
                map.put("dateBegin", course.get("DateBegin").toString());
                map.put("dateEnd", course.get("DateEnd").toString());
                map.put("teacher", null == course.get("Teacher") ? "" : course.get("Teacher").toString());
                map.put("remark", null == course.get("Remark") ? "" : course.get("Remark").toString());
                map.put("playType", StringUtil.objectToStr(course.get("PlayType"), "pc"));
                map.put("select_event", getResources().getString(R.string.joinMeeting));

                map.put("info1", "培训时间：" + map.get("dateBegin").toString().replace("T", " ").substring(0, 16) + "至" + map.get("dateEnd").toString().replace("T", " ").substring(0, 16));
                map.put("info2", "主讲人：" + map.get("teacher"));
                map.put("info3", "课程简介：" + map.get("instruction"));

                listItem.add(map);
            }
        }
    }
}
