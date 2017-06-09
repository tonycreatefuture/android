package com.zhongdasoft.svwtrainnet.module.trainnormal;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ActivityKey;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class NormalApplyActivity extends BaseActivity {
    private WeakReference<? extends BaseActivity> wr;
    private ArrayList<HashMap<String, Object>> courseList;
    private ArrayList<HashMap<String, Object>> listItem;
    private BaseListViewAdapter mAdapter;
    private ListView lv;
    private boolean isCached = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);
        isCached = false;
        Waiting.show(this, getResources().getString(R.string.Loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isCached) {
                    String SuitMeCourseRefresh = TrainNetApp.getCache().getAsString(CacheKey.SuitMeCourseRefresh);
                    if (!StringUtil.isNullOrEmpty(SuitMeCourseRefresh)) {
                        courseList = TrainNetApp.getGson().fromJson(SuitMeCourseRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                        }.getType());
                    } else {
                        courseList = TrainNetWebService.getInstance().GetSuitableCourse(NormalApplyActivity.this);
                        TrainNetApp.getCache().put(CacheKey.SuitMeCourseRefresh, TrainNetApp.getGson().toJson(courseList));
                    }
                } else {
                    courseList = TrainNetWebService.getInstance().GetSuitableCourse(NormalApplyActivity.this);
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
        return R.layout.trainnet_normal_apply;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_normalapply;
    }

    private void setListView() {
        if (null == listItem) {
            listItem = new ArrayList<>();
        } else {
            listItem.clear();
        }
        for (HashMap<String, Object> course : courseList) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("info", course.get("CourseName").toString());
            map.put("id", course.get("CourseNo").toString());
            map.put("noRight", "");
            listItem.add(map);
        }

        if (listItem.size() <= 0) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("img_noData", R.drawable.trainnet_no_img);
            map.put("noLine", "");
            map.put("noRight", "");
            listItem.add(map);
        }
        mAdapter = new BaseListViewAdapter(this, listItem);
        lv = (ListView) findViewById(R.id.listview_course);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.GRAY));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (listItem.size() > 0 && listItem.get(arg2).containsKey("id")) {
                    String str = listItem.get(arg2).get("id").toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("item", str);
                    bundle.putString("className", ActivityKey.Apply);
                    readyGo(NormalCourseContentActivity.class, bundle);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
