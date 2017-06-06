package com.zhongdasoft.svwtrainnet.module.traininner;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.ChineseToEnglish;
import com.zhongdasoft.svwtrainnet.util.CollectionUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.adapter.TraineeListViewAdapter;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class InnerPlanDispatchActivity extends BaseActivity {
    private WeakReference<? extends BaseActivity> wr;

    private String planId;
    private int planNumber;
    private int approveNumber;
    private String planStatus;

    private ArrayList<HashMap<String, Object>> traineeList;
    private EditText innerPlanSearch;
    private TraineeListViewAdapter mAdapter;
    private ArrayList<HashMap<String, Object>> listItem;
    private ArrayList<HashMap<String, Object>> tmpListItem;
    private ImageView innerPlanDispatchRefresh;
    private TextView innerPlanDispatchNum;

    private ArrayList<String> staffList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        tmpListItem = new ArrayList<>();
        staffList = new ArrayList<>();
        planId = getIntent().getStringExtra("item");
        planNumber = getIntent().getIntExtra("planNumber", 0);
        approveNumber = getIntent().getIntExtra("approveNumber", 0);
        planStatus = getIntent().getStringExtra("planStatus");

        runThread();
        innerPlanSearch = (EditText) findViewById(R.id.innerPlanSearch);
        innerPlanSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = innerPlanSearch.getText().toString();
                filterList(searchText);
                mAdapter.notifyDataSetChanged();
            }
        });
        innerPlanDispatchNum = (TextView) findViewById(R.id.innerPlanDispatchNum);
        innerPlanDispatchNum.setText(String.format("%s/%s", approveNumber, planNumber));
        innerPlanDispatchRefresh = (ImageView) findViewById(R.id.innerPlanDispatchRefresh);
        innerPlanDispatchRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //刷新列表
                CollectionUtil.sortString(tmpListItem, "cb", CollectionUtil.OrderAsc, "pinYin", CollectionUtil.OrderAsc);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_innerplan_dispatch;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_innerplan_dispatch;
    }

    private void runThread() {
        Waiting.show(this, getResources().getString(R.string.Loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
//                String InnerPlanDispatchRefresh = getCache().getAsString(CacheKey.InnerPlanDispatchRefresh + planId);
//                if (!StringUtil.isNullOrEmpty(InnerPlanDispatchRefresh)) {
//                    traineeList = getGson().fromJson(InnerPlanDispatchRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
//                    }.getType());
//                } else {
                traineeList = TrainNetWebService.getInstance().InternalSuitableTrainees(InnerPlanDispatchActivity.this, planId);
//                getCache().put(CacheKey.InnerPlanDispatchRefresh + planId, getGson().toJson(traineeList));
//                }
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

    private void filterList(String text) {
        tmpListItem.clear();
        if (text.length() == 0) {
            tmpListItem.addAll(listItem);
            return;
        }
        for (HashMap<String, Object> map : listItem) {
            if (map.get("Name").toString().contains(text)) {
                tmpListItem.add(map);
            }
        }
    }

    private void setData() {
        if (null == listItem) {
            listItem = new ArrayList<>();
        } else {
            listItem.clear();
        }
        String title = "";
        HashMap<String, Object> hashMap = null;
        for (HashMap<String, Object> map : traineeList) {
            if (!"ApiInternalTrainee".equals(map.get(getResources().getString(R.string.ParentNode)))
                    && !"ApiWorktype".equals(map.get(getResources().getString(R.string.ParentNode)))) {
                continue;
            }
            if ("ApiInternalTrainee".equals(map.get(getResources().getString(R.string.ParentNode)))) {
                hashMap = new HashMap<>();
                hashMap.put("planId", planId);
                hashMap.put("planNumber", planNumber);
                hashMap.put("approveNumber", approveNumber);
                hashMap.put("cb", getStatus(map.get("Status").toString()));
                hashMap.put("StaffId", map.get("StaffId").toString());
                hashMap.put("Idcard", map.get("Idcard").toString());
                title = map.get("Name").toString();
                hashMap.put("pinYin", ChineseToEnglish.getPingYin(title));
                hashMap.put("Name", title);
                hashMap.put("title", title);
//                hashMap.put("subTitle", getStatusName(map.get("Status").toString()));
//                hashMap.put("subTitleDesc", getStatusDescName(map.get("Status").toString()));
//                hashMap.put("isChecked", "0");
//                hashMap.put("saveChecked", isChecked(map.get("Status").toString()));
                listItem.add(hashMap);
            } else {
                if (title.equals(hashMap.get("title").toString())) {
                    hashMap.put("title", getFixLength(title) + map.get("Name").toString());
                } else {
                    hashMap.put("title", hashMap.get("title").toString() + "," + map.get("Name").toString());
                }
            }
        }
        if (listItem.size() <= 0) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("img_noData", R.drawable.trainnet_no_img);
            listItem.add(map);
        } else {
            CollectionUtil.sortString(listItem, "cb", CollectionUtil.OrderAsc, "pinYin", CollectionUtil.OrderAsc);
            if ("2".equals(planStatus) || "3".equals(planStatus) || "4".equals(planStatus)) {
                listItem.get(0).put("readOnly", "1");
            }
        }
        tmpListItem.addAll(listItem);
    }

    private Object isChecked(String status) {
        if ("Registered".equals(status)) {
            return "1";
        } else if ("NotRegistered".equals(status)) {
            return "0";
        } else {
            return "0";
        }
    }

    private String getFixLength(String title) {
        StringBuilder sbStr = new StringBuilder(title);
        for (int i = 0; i < 4 - title.length(); i++) {
            sbStr.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        }
        sbStr.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        return sbStr.toString();
    }

    private void setListView() {
        setData();
        mAdapter = new TraineeListViewAdapter(InnerPlanDispatchActivity.this, tmpListItem);
        ListView lv = (ListView) findViewById(R.id.listview_innerplan_dispatch);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
            }
        });
    }

    private String getStatus(String status) {
        if ("Registered".equals(status)) {
            return "1";
        } else if ("NotRegistered".equals(status)) {
            return "2";
        } else {
            return "3";
        }
    }

    private String getStatusName(String status) {
        if ("Registered".equals(status)) {
            return "已安排";
        } else if ("NotRegistered".equals(status)) {
            return "未安排";
        } else {
            return "未安排";
        }
    }

    private String getStatusDescName(String status) {
        if ("Registered".equals(status)) {
            return "";
        } else if ("NotRegistered".equals(status)) {
            return "";
        } else {
            return "已安排到其他计划";
        }
    }
}
