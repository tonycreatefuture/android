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

import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.module.amap.MapActivity;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.DialogUtil;
import com.zhongdasoft.svwtrainnet.util.ListViewUtil;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.NetManager;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.util.WebserviceUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class NormalPlanActivity extends BaseActivity {
    private WeakReference<? extends BaseActivity> wr;
private boolean confirmed;
    private String planId;
    private String applyId;
    private ArrayList<HashMap<String, Object>> myPlanList;
    private HashMap<String, Object> planList;
    private ListView lv;
    private ArrayList<HashMap<String, Object>> listItem;
    private BaseListViewAdapter mAdapter;

    private static final int REFRESH_COMPLETE = 0X110;
    private SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        String planId = getIntent().getStringExtra("item");
        String strConfirmed = getIntent().getStringExtra("confirmed");
        confirmed = Boolean.parseBoolean(strConfirmed);
        if (planId == null) {
            planId = MySharedPreferences.getInstance().getString("currentPlan", this);
        } else {
            MySharedPreferences.getInstance().setStoreString("currentPlan", planId, this);
        }
        String[] id = planId.split(",,");
        this.planId = id[0];
        this.applyId = id[1];

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
            }
        });
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        Waiting.show(this, getResources().getString(R.string.Loading));
        new Thread(new NormalPlanThread()).start();

//        new NormalPlanHandler(wr, planId, confirmed);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_normal_plan;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_normalplan;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void setData() {
        if (null == listItem) {
            listItem = new ArrayList<>();
        } else {
            listItem.clear();
        }
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(planList.get("CourseName")), ListViewUtil.PaperTitle);

        ListViewUtil.setListItem(listItem, getResources().getString(R.string.CourseStartAndEndTime), ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(planList.get("StartDate"), planList.get("EndDate")), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, getResources().getString(R.string.CoursePlace), ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(planList.get("Address")), ListViewUtil.PaperAMap);

        ListViewUtil.setListItem(listItem, getResources().getString(R.string.Director), ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(planList.get("Teacher")), ListViewUtil.PaperInfo);

        if (!confirmed) {
            ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_callback), ListViewUtil.PaperEvent);
        } else {
            ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_callbacked), ListViewUtil.PaperEventDone);
        }

        String courseNo = planList.get("CourseNo").toString();
        String startDate = planList.get("StartDate").toString();
        String currentTime = MySharedPreferences.getInstance().getCurrentTime(this);
        if (WebserviceUtil.getInstance().prepareRegisterRoute(courseNo, startDate, currentTime)) {
            ListViewUtil.setListItem(listItem, "", ListViewUtil.PaperInfo);
            ListViewUtil.setListItem(listItem, getResources().getString(R.string.course_register), ListViewUtil.PaperEventTwo);
        }
    }

    private void setListView() {
        setData();
        mAdapter = new BaseListViewAdapter(this, listItem);
        lv = (ListView) findViewById(R.id.listview_plan);
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
                            HashMap<String, Object> myApplyList = TrainNetWebService.getInstance().ConfirmApply(NormalPlanActivity.this, applyId);
                            final String returnCode = myApplyList.get(getResources().getString(R.string.ReturnCode)).toString();
                            final String message = myApplyList.get(getResources().getString(R.string.Message)).toString();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogUtil.getInstance().showDialog(wr, getResources().getString(R.string.tips), message, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AlertDialog alertDialog = (AlertDialog) v.getTag();
                                            alertDialog.dismiss();
                                            if ("0".equals(returnCode)) {
                                                confirmed = true;
                                                setData();
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }, null);
                                }
                            });
                        }
                    }).start();
                }
                if (listItem.get(arg2).containsKey("paperEventTwo")) {
                    WebserviceUtil.getInstance().registerRoute(wr, applyId);
                }
                if (listItem.get(arg2).containsKey("paperAMap")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("item", listItem.get(arg2).get("paperInfo").toString());
                    readyGo(MapActivity.class, bundle);
                }
            }
        });
    }


    class NormalPlanThread implements Runnable {
        public NormalPlanThread() {
        }

        @Override
        public void run() {
            String PlanRefresh = getCache().getAsString(CacheKey.PlanRefresh + planId);
            if (!NetManager.isNetworkConnected(NormalPlanActivity.this) && StringUtil.isNullOrEmptyOrEmptySet(PlanRefresh)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Waiting.dismiss();
                        ToastUtil.show(NormalPlanActivity.this, getResources().getString(R.string.showByNetError));
                    }
                });
                return;
            }
            if (!StringUtil.isNullOrEmptyOrEmptySet(PlanRefresh)) {
                planList = getGson().fromJson(PlanRefresh, new TypeToken<HashMap<String, Object>>() {
                }.getType());
            } else {
                myPlanList = TrainNetWebService.getInstance().GetTrainingPlan(NormalPlanActivity.this, planId);
                planList = new HashMap<>();
                for (HashMap<String, Object> map : myPlanList) {
                    if ("Result".equals(map.get(getResources().getString(R.string.ParentNode)))
                            || "Course".equals(map.get(getResources().getString(R.string.ParentNode)))
                            || "Place".equals(map.get(getResources().getString(R.string.ParentNode)))) {
                        for (String key : map.keySet()) {
                            planList.put(key, map.get(key).toString());
                        }
                    }
                }
                getCache().put(CacheKey.PlanRefresh + planId, getGson().toJson(planList));
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
                    if (NetManager.isNetworkConnected(NormalPlanActivity.this)) {
                        getCache().remove(CacheKey.PlanRefresh);
                        new Thread(new NormalPlanThread()).start();
                    } else {
                        ToastUtil.show(NormalPlanActivity.this, getResources().getString(R.string.refreshByNetError));
                    }
                    mSwipeLayout.setRefreshing(false);
                    break;
            }
        }
    };
}
