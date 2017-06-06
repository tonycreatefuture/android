package com.zhongdasoft.svwtrainnet.module.trainnormal;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;
import com.zhongdasoft.svwtrainnet.util.CollectionUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class NormalScoreActivity extends BaseActivity {
    private WeakReference<? extends BaseActivity> wr;
    private ArrayList<HashMap<String, Object>> finishedList;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        Waiting.show(this, getResources().getString(R.string.Loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                finishedList = TrainNetWebService.getInstance().GetOfflineCourseResult(NormalScoreActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setListView();
                        Waiting.dismiss();
                    }
                });
            }
        }).start();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_normal_score;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_normalscore;
    }

    private void setListView() {
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        CollectionUtil.sortString(finishedList, "StartDate", CollectionUtil.OrderDesc);
        for (HashMap<String, Object> hashMap : finishedList) {
            if (!"ApiApply".equals(hashMap.get(getResources().getString(R.string.ParentNode)))) {
                continue;
            }
            for (HashMap<String, Object> tmpMap : finishedList) {
                if (!"Course".equals(tmpMap.get(getResources().getString(R.string.ParentNode)))) {
                    continue;
                }
                if (hashMap.get("ApplyId").equals(tmpMap.get("parentId"))) {
                    hashMap.put("CourseName", tmpMap.get("CourseName"));
                    break;
                }
            }
            boolean pass = Boolean.parseBoolean(hashMap.get("Pass").toString());
            HashMap<String, Object> map = new HashMap<>();
            map.put("info", hashMap.get("CourseName"));
            map.put("info1", "培训时间：" + hashMap.get("StartDate").toString().substring(0, 16).replace("T", " ") + "至" + hashMap.get("EndDate").toString().substring(0, 16).replace("T", " "));
            map.put("info2", "是否通过：" + (pass ? "通过" : "未通过"));
            map.put("info3", "成绩：" + hashMap.get("Grade"));
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
        BaseListViewAdapter mAdapter = new BaseListViewAdapter(this, listItem);
        lv = (ListView) findViewById(R.id.listview_score);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.GRAY));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
