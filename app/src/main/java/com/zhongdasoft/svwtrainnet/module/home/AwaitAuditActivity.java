package com.zhongdasoft.svwtrainnet.module.home;

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
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.util.CollectionUtil;
import com.zhongdasoft.svwtrainnet.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class AwaitAuditActivity extends BaseActivity {
    private ArrayList<HashMap<String, Object>> processList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String AuditRefresh = TrainNetApp.getCache().getAsString(CacheKey.AuditRefresh);
        if (!StringUtil.isNullOrEmptyOrEmptySet(AuditRefresh)) {
            processList = TrainNetApp.getGson().fromJson(AuditRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
        } else {
            processList = new ArrayList<>();
        }
        setListView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_await_audit;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_awaitaudit;
    }

    private void setListView() {
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        String tmpStr;
        CollectionUtil.sortString(processList, "ApplyDate", CollectionUtil.OrderDesc);
        for (HashMap<String, Object> hashMap : processList) {
            if (!"ApiPendingAudit".equals(hashMap.get(getResources().getString(R.string.ParentNode)))) {
                continue;
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("info", hashMap.get("CourseName"));
            map.put("info1", "<strong>申请日期：</strong>" + hashMap.get("ApplyDate").toString().replace("T", " "));
            tmpStr = hashMap.get("DealerApproveStatus").toString();
            if (!"待批准".equals(tmpStr)) {
                map.put("info2", "<strong>经销商审核：</strong>" + tmpStr + "," + hashMap.get("DealerApproveDate").toString().replace("T", " "));
            } else {
                map.put("info2", "<strong>经销商审核：</strong>" + tmpStr);
            }
            if ("未批准".equals(tmpStr)) {
                map.put("info3", "<strong>经销商审核备注：</strong>" + StringUtil.objectToStr(hashMap.get("DealerApproveComment")));
            } else {
                if ("已批准".equals(tmpStr)) {
                    if (hashMap.containsKey("ApproveStatus")) {
                        tmpStr = hashMap.get("ApproveStatus").toString();
                        if (!"待批准".equals(tmpStr)) {
                            map.put("info3", "<strong>管理员审核：</strong>" + tmpStr + "," + hashMap.get("ApproveDate").toString().replace("T", " "));
                            if ("未批准".equals(tmpStr)) {
                                map.put("info4", "<strong>管理员审核备注：</strong>" + hashMap.get("ApproveComment").toString());
                            }
                        } else {
                            map.put("info3", "<strong>管理员审核：</strong>" + tmpStr);
                        }
                    }
                }
            }
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
        BaseListViewAdapter mAdapter = new BaseListViewAdapter(AwaitAuditActivity.this, listItem);
        ListView lv = (ListView) findViewById(R.id.listview_awaitaudit);
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
}
