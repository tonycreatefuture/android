package com.zhongdasoft.svwtrainnet.module.home;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.OnLineListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.WebserviceUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class AwaitStudyActivity extends BaseActivity {
    private ArrayList<HashMap<String, Object>> studyList;
    private ArrayList<HashMap<String, Object>> listItem;
    private WeakReference<? extends BaseActivity> wr;
    private OnLineListViewAdapter mAdapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wr = new WeakReference<>(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                studyList = TrainNetWebService.getInstance().OnlineCourseList(AwaitStudyActivity.this, "-1");
                listItem = WebserviceUtil.getInstance().onlineCourseList(AwaitStudyActivity.this, studyList, true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setListView();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_await_study;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_awaitstudy;
    }

    private void setListView() {
        if (listItem.size() <= 0) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("img_noData", R.drawable.trainnet_no_img);
            map.put("info3Label", "");
            map.put("noLine", "");
            listItem.add(map);
        }
        mAdapter = new OnLineListViewAdapter(wr, listItem, 1);
        lv = (ListView) findViewById(R.id.listview_awaitstudy);
        lv.setAdapter(mAdapter);
        lv.setSelector(R.color.transparent);
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
            }
        });
    }
}
