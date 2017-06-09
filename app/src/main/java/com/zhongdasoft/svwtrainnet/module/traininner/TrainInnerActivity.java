package com.zhongdasoft.svwtrainnet.module.traininner;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.InnerListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.ChineseToEnglish;
import com.zhongdasoft.svwtrainnet.util.CollectionUtil;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.Scale;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.widget.popwindow.PlanPopWindow;
import com.zhongdasoft.svwtrainnet.widget.popwindow.PlanSpinerAdapter;
import com.zhongdasoft.svwtrainnet.widget.popwindow.SpinnerOption;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class TrainInnerActivity extends BaseActivity {
    private String beginDate;
    private String endDate;
    private ArrayList<HashMap<String, Object>> listItem;
    private InnerListViewAdapter mAdapter;

    private TextView tvTitle;
    private PlanPopWindow planPopWindow;
    private List<SpinnerOption> data_list;
    private PlanSpinerAdapter<SpinnerOption> planAdapter;
    private final String[] planStatus = {"执行中,未执行,已完成", "计划类型", "总部内训,自主内训,全部类型", "执行月份", "", "查询"};
    private final String[] status = {"执行中▼", "未执行▼", "已完成▼"};
    private int popWidth;
    private WeakReference<? extends BaseActivity> wr;
    private String currentPlanType = "All";
    private int iStatus = 0;
    //    private boolean isInternalTrainer;
    private ArrayList<HashMap<String, Object>> innerPlanList;
    private String InnerPlanQuerySelectedYM;
    private ListView lv;
    private String currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentTime = MySharedPreferences.getInstance().getCurrentTime();
        wr = new WeakReference<>(this);

//        String status = getIntent().getStringExtra("status");
//        if (StringUtil.isNullOrEmpty(status)) {
//            MySharedPreferences.getInstance().setStoreString("InnerPlanQuery", "0,2,2,", this);
//        }

        tvTitle = (TextView) findViewById(R.id.trainnet_title);
        Button rightBtn = (Button) findViewById(R.id.trainnet_button_right);
//        if (isInternalTrainer) {
        String innerPlanQuery = MySharedPreferences.getInstance().getString("InnerPlanQuery");
        if (!StringUtil.isNullOrEmpty(innerPlanQuery)) {
            iStatus = Integer.parseInt(innerPlanQuery.substring(0, 1));
            int j = Integer.parseInt(innerPlanQuery.substring(2, 3));
            if (j == 0) {
                currentPlanType = "Headquarters";
            } else if (j == 1) {
                currentPlanType = "Autonomous";
            } else {
                currentPlanType = "All";
            }
        }
        InnerPlanQuerySelectedYM = MySharedPreferences.getInstance().getString("InnerPlanQuerySelectedYM");
        if (StringUtil.isNullOrEmpty(InnerPlanQuerySelectedYM)) {
            String yearMonth = getYearMonth();
            InnerPlanQuerySelectedYM = yearMonth.split(",")[1];
        }
        tvTitle.setText(status[iStatus]);
        tvTitle.setClickable(true);
        tvTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("查询▲".equals(tvTitle.getText().toString()) && planPopWindow.isShowing()) {
                    planClick(-1);
                    planPopWindow.dismiss();
                } else {
                    popWindow();
                }
            }
        });

        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("+");
        rightBtn.setTextSize(35.0f);
        rightBtn.setTextColor(getResources().getColor(R.color.white));
        rightBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                readyGoThenKill(InnerPlanCreateUpdateActivity.class);
            }
        });

        popWidth = Scale.getScreen(wr).getPxWidth();
        setPlanQuery();
        planClick(-1);
//        } else {
//            tvTitle.setText(R.string.title_traininner);
//            setListView();
//        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_inner_plan;
    }

    @Override
    protected int getMTitle() {
        return 0;
    }

    private void setData() {
        if (null == listItem) {
            listItem = new ArrayList<>();
        } else {
            listItem.clear();
        }
        if (null == innerPlanList) {
            setNoData();
            return;
        }
        HashMap<String, Object> hashMap = null;
        for (HashMap<String, Object> map : innerPlanList) {
            if (!"ApiInternalPlan".equals(map.get(getResources().getString(R.string.ParentNode)))
                    && !"Course".equals(map.get(getResources().getString(R.string.ParentNode)))) {
                continue;
            }
            if ("ApiInternalPlan".equals(map.get(getResources().getString(R.string.ParentNode)))) {
                if ("已取消".equals(map.get("SummaryStatus").toString())) {
                    hashMap = null;
                    continue;
                }
                hashMap = new HashMap<>();
                hashMap.put("planId", map.get("Id").toString());
                hashMap.put("type", map.get("Type").toString());
                hashMap.put("subTitle", getTypeName(map.get("Type").toString()));
                hashMap.put("infoDate", map.get("StartDate").toString().substring(0, 10) + "至" + map.get("FinishDate").toString().substring(0, 10));
                hashMap.put("infoTitle", map.get("SummaryStatus").toString());
                hashMap.put("info", getInfoString(map.get("PlanNumber").toString(), map.get("ApproveNumber").toString()));
                int planNumber = Integer.parseInt(map.get("PlanNumber").toString());
                int approveNumber = Integer.parseInt(map.get("ApproveNumber").toString());
                int planStatus = getPlanStatus(map.get("Published").toString(), map.get("StartDate").toString(), map.get("FinishDate").toString(), map.get("Canceled").toString(), map.get("SummaryStatus").toString());
                String summaryStatus = map.get("SummaryStatus").toString();
                hashMap.put("summaryStatus", summaryStatus);
                hashMap.put("planNumber", planNumber);
                hashMap.put("approveNumber", approveNumber);
                hashMap.put("planStatus", planStatus);
                hashMap.put("startDate", map.get("StartDate").toString());
                hashMap.put("finishDate", map.get("FinishDate").toString());
                hashMap.put("term", null == map.get("Term") ? "1" : map.get("Term").toString());
                hashMap.put("teacher", "");//map.get("Id").toString());
                if ("未通过".equals(summaryStatus) || "请修改".equals(summaryStatus)) {
                    hashMap.put("infoTitle_event", "详情");
                    hashMap.put("infoMsg", null == map.get("SummaryApproveComment") ? "" : map.get("SummaryApproveComment").toString());
                } else {
                    hashMap.remove("infoTitle_event");
                }
                switch (planStatus) {
                    case 1:
                        //未执行
                        hashMap.put("tv_event1", "人员");
                        if ("true".equals(map.get("Published").toString().toLowerCase())) {
                            hashMap.put("tv_event2", "取消");
                        } else {
                            hashMap.put("img_op1", "编辑");
                            hashMap.put("tv_event2", "发布");
                            hashMap.put("tv_event3", "取消");
                        }
                        break;
                    case 0:
                        //执行中
                        hashMap.put("tv_event1", "人员");
                        hashMap.put("tv_event2", "成绩");
                        hashMap.put("tv_event3", "小结");
                        if (!"待审核".equals(hashMap.get("summaryStatus").toString())) {
                            hashMap.put("tv_event4", "提交");
                        }
                        break;
                    case 2:
                        //已完成
//                        hashMap.put("tv_event1", "人员");
                        hashMap.put("tv_event1", "成绩");
                        hashMap.put("tv_event2", "小结");
//                        hashMap.put("noEvent", "");
                        break;
                }
                //此处添加过滤条件
                if (iStatus == planStatus) {
                    if ("All".equals(currentPlanType) || hashMap.get("type").toString().equals(currentPlanType)) {
                        String startDate = hashMap.get("startDate").toString();
                        String finishDate = hashMap.get("finishDate").toString();
                        if (startDate.startsWith(InnerPlanQuerySelectedYM) || finishDate.startsWith(InnerPlanQuerySelectedYM)) {
                            listItem.add(hashMap);
                        }
                    }
                }
                //过滤条件添加完成
            } else {
                if (null == hashMap) {
                    continue;
                }
                hashMap.put("title", map.get("CourseName").toString());
                hashMap.put("pinYin", ChineseToEnglish.getPingYin(map.get("CourseName").toString()));
                if (map.containsKey("CourseNo")) {
                    hashMap.put("courseNo", map.get("CourseNo").toString());
                } else {
                    hashMap.put("courseNo", "0");
                }
                hashMap.put("courseName", map.get("CourseName").toString());
            }
        }
        if (listItem.size() <= 0) {
            setNoData();
        } else {
            CollectionUtil.sortString(listItem, "infoDate", CollectionUtil.OrderDesc, "pinYin", CollectionUtil.OrderAsc);
        }
    }

    private void setNoData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("img_noData", R.drawable.trainnet_no_img);
        map.put("noEvent", "");
        map.put("noLine", "");
        listItem.add(map);
    }

    private void setListView() {
        setData();
        mAdapter = new InnerListViewAdapter(wr, listItem);
        lv = (ListView) findViewById(R.id.listview_inner_plan);
        lv.setAdapter(mAdapter);
        lv.setSelected(true);
        lv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
            }
        });
        String InnerPlanPos = MySharedPreferences.getInstance().getString("InnerPlanPos");
        if (StringUtil.isNullOrEmpty(InnerPlanPos)) {
            lv.setSelection(0);
        } else {
            int currentPos = Integer.parseInt(InnerPlanPos);
            lv.setSelection(currentPos);
        }
    }

    private String getTypeName(String type) {
        String result;
        if ("Headquarters".equals(type)) {
            result = "总部内训";
        } else {
            result = "自主内训";
        }
        return result;
    }

    private String getInfoString(String planNumber, String approveNumber) {
        String result = String.format("计划：%s人  参训：%s人 ", planNumber, approveNumber);
        return result;
    }

    private int getPlanStatus(String... params) {
        if (params.length == 5) {
            //已取消
            if ("true".equals(params[3])) {
                return 2;
            }
            //未发布
            if ("false".equals(params[0])) {
                return 1;
            }
            //已完成
            if ("已取消".equals(params[4]) || "已过期".equals(params[4]) || "未通过".equals(params[4]) || "已通过".equals(params[4])) {
                return 2;
            }
            if (currentTime.substring(0, 10).compareTo(params[1].substring(0, 10)) < 0) {
                return 1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private void popWindow() {
        planPopWindow.setWidth(popWidth);
        planPopWindow.showAsDropDown(tvTitle, 0, 0);
        tvTitle.setText("查询▲");
    }

    private void setPlanQuery() {
        data_list = new ArrayList<>();
        SpinnerOption spinnerOption;
        for (int i = 0; i < planStatus.length; i++) {
            if (StringUtil.isNullOrEmpty(planStatus[i])) {
                planStatus[i] = getYearMonth();
            }
            spinnerOption = new SpinnerOption(i + "", planStatus[i]);
            data_list.add(spinnerOption);
        }
        // 设置Adapter
        planAdapter = new PlanSpinerAdapter<>(this);
        planAdapter.refreshData(data_list, 0);

        planPopWindow = new PlanPopWindow(this);
        planPopWindow.setAdatper(planAdapter);
        planPopWindow
                .setItemListener(new PlanSpinerAdapter.IOnItemSelectListener() {
                    @Override
                    public void onItemClick(int pos) {
                        planClick(pos);
                    }
                });
    }

    private String getYearMonth() {
        return getYearMonth(1, currentTime);
    }

    private String getYearMonth(int pos, String yearMonthStr) {
        String year = yearMonthStr.substring(0, 4);
        String month = yearMonthStr.substring(5, yearMonthStr.length() < 7 ? yearMonthStr.length() : 7);
        int iYear = Integer.parseInt(year);
        int iMonth = Integer.parseInt(month);
        if (pos != 1) {
            if (pos == 0) {
                if (iMonth == 12) {
                    iYear += 1;
                    iMonth = 1;
                } else {
                    iMonth += 1;
                }
            } else {
                if (iMonth == 1) {
                    iYear -= 1;
                    iMonth = 12;
                } else {
                    iMonth -= 1;
                }
            }
        }
        if (iMonth > 1 && iMonth < 12) {
            return String.format("%s-%s,%s-%s,%s-%s", iYear, getFix(iMonth - 1), iYear, getFix(iMonth), iYear, getFix(iMonth + 1));
        } else if (iMonth == 1) {
            return String.format("%s-%s,%s-%s,%s-%s", iYear - 1, 12, iYear, getFix(iMonth), iYear, getFix(iMonth + 1));
        } else {
            return String.format("%s-%s,%s-%s,%s-%s", iYear, getFix(iMonth - 1), iYear, getFix(iMonth), iYear + 1, 1);
        }
    }

    private String getFix(int num) {
        if (num >= 10) {
            return num + "";
        } else {
            return "0" + num;
        }
    }

    private String[] getDate(String yearMonthStr) {
        int iYear = Integer.parseInt(yearMonthStr.substring(0, 4));
        int iMonth = Integer.parseInt(yearMonthStr.substring(5, yearMonthStr.length()));
        String[] dateStr = new String[2];
        Calendar calendar = Calendar.getInstance();
        calendar.set(iYear, iMonth, 1);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        int endDay = calendar.get(Calendar.DATE);
        int preYear = iYear;
        int preMonth = iMonth;
        if (iMonth == 1) {
            preYear -= 1;
            preMonth = 12;
        } else {
            preMonth -= 1;
        }
        dateStr[0] = String.format("%s-%s-01", preYear, getFix(preMonth));
        int nextYear = iYear;
        int nextMonth = iMonth;
        if (iMonth == 12) {
            nextYear += 1;
            nextMonth = 1;
        } else {
            nextMonth += 1;
        }
        dateStr[1] = String.format("%s-%s-%s", nextYear, getFix(nextMonth), endDay);
        return dateStr;
    }

    private void planClick(int pos) {
        String innerPlanQuery = MySharedPreferences.getInstance().getString("InnerPlanQuery");
        if (StringUtil.isNullOrEmpty(innerPlanQuery)) {
            innerPlanQuery = getResources().getString(R.string.innerPlanQuery);
        }
        iStatus = Integer.parseInt(innerPlanQuery.substring(0, 1));
        tvTitle.setText(status[iStatus]);
        int j = Integer.parseInt(innerPlanQuery.substring(2, 3));
        if (j == 0) {
            currentPlanType = "Headquarters";
        } else if (j == 1) {
            currentPlanType = "Autonomous";
        } else {
            currentPlanType = "All";
        }
        InnerPlanQuerySelectedYM = MySharedPreferences.getInstance().getString("InnerPlanQuerySelectedYM");
        if (StringUtil.isNullOrEmpty(InnerPlanQuerySelectedYM)) {
            String yearMonth = getYearMonth();
            InnerPlanQuerySelectedYM = yearMonth.split(",")[0];
        } else {
            int k = Integer.parseInt(innerPlanQuery.substring(4, 5));
            String[] yearMonths = data_list.get(4).getText().split(",");
            if (!yearMonths[k].equals(InnerPlanQuerySelectedYM)) {
                data_list.remove(4);
                SpinnerOption spinnerOption = new SpinnerOption("4", getYearMonth(k, InnerPlanQuerySelectedYM));
                data_list.add(4, spinnerOption);
                planAdapter.refreshData(data_list, 0);
            }
        }
        runThread(InnerPlanQuerySelectedYM);
    }

    private void runThread(String yearMonthStr) {
        String[] dateStr = getDate(yearMonthStr);
        beginDate = dateStr[0];
        endDate = dateStr[1];
        Waiting.show(this, getResources().getString(R.string.Loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                innerPlanList = TrainNetWebService.getInstance().InternalQueryPlans(TrainInnerActivity.this, beginDate, endDate);
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
    protected void onResume() {
        super.onResume();
        planClick(-1);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
