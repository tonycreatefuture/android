package com.zhongdasoft.svwtrainnet.module.traininner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ACache;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.DialogUtil;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.PhoneInfo;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class InnerPlanCreateUpdateActivity extends BaseActivity {
    private View include;
    private TextView innerPlanHead;
    private TextView innerPlanAuto;
    private TextView innerPlanDate1;
    private TextView innerPlanDate2;
    private EditText innerPlanCourse;
    private TextView innerPlanCourseDesc;
    private TextView innerPlanCourseTitle;
    private EditText innerPlanTerm;
    private EditText innerPlanNum;
    private TextView innerPlanOk;
    private CheckBox innerPlanPublished;
    private RadioButton innerPlanTeacher1;
    private RadioButton innerPlanTeacher2;
    private boolean isHead = true;
    private String planTeacher = null;

    private Calendar calendar;
    private DatePickerDialog dialog;

    private HashMap<String, Object> resultMap;
    private WeakReference<? extends BaseActivity> wr;

    private ACache mCache;
    private Gson gson;
    private ArrayList<HashMap<String, Object>> courseList;
    private String planId;
    private String planType;
    private String planStatus;
    private String startDate;
    private String finishDate;
    private String courseNo;
    private String courseName;
    private String term;
    private String planNumber;
    private String teacher;
    private String dealerNo;

    private String headCourseName;
    private String autoCourseName;
    private String currentTime;
    private OnClickListener innerPlanCourseClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //此处弹出窗口选择课程，选择课程后窗口关闭
            DialogUtil.getInstance().showCourseDialog(wr, "请选择培训课程", new DialogUtil.CourseDismiss() {
                @Override
                public void handleDismiss(String courseNo, String courseName) {
                    innerPlanCourse.setTag(courseNo);
                    innerPlanCourse.setText(courseName);
                    setCacheCourse(true);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        dealerNo = MySharedPreferences.getInstance().getString("DealerNo", this);

        mCache = getCache();
        gson = getGson();
        currentTime = MySharedPreferences.getInstance().getCurrentTime(this);
        int[] includes = {R.id.include_planType, R.id.include_planDate, R.id.include_planCourse, R.id.include_planTerm, R.id.include_planNum, R.id.include_planTeacher};
        int[] texts = {R.string.PlanType, R.string.PlanDate, R.string.PlanCourse, R.string.PlanTerm, R.string.PlanNum, R.string.PlanTeacher};
        for (int i = 0; i < includes.length; i++) {
            include = findViewById(includes[i]);
            TextView tv = (TextView) include.findViewById(R.id.innerPlanLineDesc);
            tv.setText(texts[i]);
            if (i == 2) {
                innerPlanCourseTitle = tv;
            }
        }

        innerPlanHead = (TextView) findViewById(R.id.innerPlanHead);
        innerPlanHead.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setType(true);
            }
        });
        innerPlanAuto = (TextView) findViewById(R.id.innerPlanAuto);
        innerPlanAuto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setType(false);
            }
        });
        calendar = Calendar.getInstance();
        innerPlanDate1 = (TextView) findViewById(R.id.innerPlanDate1);
        innerPlanDate1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出日期窗口，选择开始日期
                dialog = new DatePickerDialog(InnerPlanCreateUpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        innerPlanDate1.setText(String.format("%s-%s-%s", year, monthOfYear + 1 < 10 ? "0" + (monthOfYear + 1) : monthOfYear + 1, dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
        innerPlanDate2 = (TextView) findViewById(R.id.innerPlanDate2);
        innerPlanDate2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出日期窗口，选择结束日期
                dialog = new DatePickerDialog(InnerPlanCreateUpdateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        innerPlanDate2.setText(String.format("%s-%s-%s", year, monthOfYear + 1 < 10 ? "0" + (monthOfYear + 1) : monthOfYear + 1, dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        //默认加载一个内训课程，并把内训老师加载上
        innerPlanCourse = (EditText) findViewById(R.id.innerPlanCourse);
        innerPlanCourseDesc = (TextView) findViewById(R.id.innerPlanCourseDesc);
        innerPlanCourse.setOnClickListener(innerPlanCourseClick);

        innerPlanTerm = (EditText) findViewById(R.id.innerPlanTerm);
        innerPlanNum = (EditText) findViewById(R.id.innerPlanNum);
        innerPlanPublished = (CheckBox) findViewById(R.id.innerPlanPublished);
        innerPlanTeacher1 = (RadioButton) findViewById(R.id.innerPlanTeacher1);
        innerPlanTeacher2 = (RadioButton) findViewById(R.id.innerPlanTeacher2);
        loadTeacher();
        //更新时需要传值过来
        Intent intent = getIntent();
        planId = intent.getStringExtra("item");
        if (null == planId) {
            planId = "";
        } else {
            planType = intent.getStringExtra("type");
            planStatus = intent.getStringExtra("planStatus");
            startDate = intent.getStringExtra("startDate");
            finishDate = intent.getStringExtra("finishDate");
            courseNo = intent.getStringExtra("courseNo");
            courseName = intent.getStringExtra("courseName");
            term = intent.getStringExtra("term");
            planNumber = intent.getStringExtra("planNumber");
            teacher = intent.getStringExtra("teacher");
            if ("Headquarters".equals(planType)) {
                setType(true);
            } else {
                setType(false);
            }
            innerPlanDate1.setText(startDate.substring(0, 10));
            innerPlanDate2.setText(finishDate.substring(0, 10));
            innerPlanTerm.setText(term);
            innerPlanNum.setText(planNumber);
            if (teacher.equals(innerPlanTeacher1.getText().toString())) {
                innerPlanTeacher1.setChecked(true);
            } else if (teacher.equals(innerPlanTeacher2.getText().toString())) {
                innerPlanTeacher2.setChecked(true);
            } else {
                innerPlanTeacher1.setChecked(true);
            }
        }

        innerPlanOk = (TextView) findViewById(R.id.innerPlanOk);
        innerPlanOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //此处判断输入是否完整，然后保存数据（调接口）
                if (checkInput()) {
                    String plan = PhoneInfo.getInstance().getPlanXML(planId, dealerNo, getType(), innerPlanCourse.getTag().toString(), innerPlanCourse.getText().toString(), innerPlanDate1.getText().toString(), innerPlanDate2.getText().toString(), innerPlanTerm.getText().toString(), innerPlanNum.getText().toString(), "0", innerPlanPublished.isChecked() ? "true" : "false", planTeacher);
                    runThread(plan);
                }
            }
        });

        if (StringUtil.isNullOrEmpty(planId)) {
            setCacheCourse(false);
        } else {
            innerPlanCourse.setText(courseName);
            headCourseName = courseName;
            innerPlanCourse.setTag(courseNo);
            setCacheCourse(true);
            loadTeacher();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_innerplan_createupdate;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_innerplan_createupdate;
    }

    private void setCacheCourse(final boolean hasCourse) {
        final String courseType = "-1";
        Waiting.show(this, getResources().getString(R.string.LoadingPlanDefaultCourse));
        new Thread(new Runnable() {
            @Override
            public void run() {
                String AllInternalCourseRefresh = mCache.getAsString(CacheKey.AllInternalCourseRefresh + courseType);
                if (!StringUtil.isNullOrEmpty(AllInternalCourseRefresh)) {
                    try {
                        courseList = gson.fromJson(AllInternalCourseRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                        }.getType());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    courseList = TrainNetWebService.getInstance().GetCourseList(
                            InnerPlanCreateUpdateActivity.this, Integer.parseInt(courseType), getResources().getString(R.string.Internal));
//                    courseList = filterExpiredCourse();
                    mCache.put(CacheKey.AllInternalCourseRefresh + courseType, gson.toJson(courseList));
                }
                if (courseList.size() <= 0) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //过滤内训课程，找到第一个返回
                        if (!hasCourse) {
                            innerPlanCourse.setText(courseList.get(0).get("CourseName").toString());
                            innerPlanCourse.setTag(courseList.get(0).get("CourseNo").toString());
                            headCourseName = courseList.get(0).get("CourseName").toString();
                        }
                        if (null != innerPlanCourse.getTag()) {
                            setCourseDesc(innerPlanCourse.getTag().toString());
                        }
                        Waiting.dismiss();
                    }
                });
            }
        }).start();
    }

    private void setCourseDesc(String courseNo) {
        String InternalExpiry = null;
        for (HashMap<String, Object> map : courseList) {
            if (courseNo.equals(map.get("CourseNo").toString()) && null != map.get("InternalExpiry")) {
                InternalExpiry = map.get("InternalExpiry").toString();
                break;
            }
        }
        if (!StringUtil.isNullOrEmpty(InternalExpiry) && !"9999-12-31T23:59:59.9999999".equals(InternalExpiry)) {
            innerPlanCourseDesc.setVisibility(View.VISIBLE);
            if (InternalExpiry.substring(0, 10).compareTo(currentTime.substring(0, 10)) < 0) {
                innerPlanCourseDesc.setText(getResources().getString(R.string.innerCourseExpired1));
            } else {
                innerPlanCourseDesc.setText(getResources().getString(R.string.innerCourseExpired2, InternalExpiry.substring(0, 10)));
            }
        } else {
            innerPlanCourseDesc.setText("");
            innerPlanCourseDesc.setVisibility(View.GONE);
        }
    }

    private void loadTeacher() {
        String name = MySharedPreferences.getInstance().getName(this);
        String userName = MySharedPreferences.getInstance().getUserName(this);
        innerPlanTeacher1.setTag(userName);
        innerPlanTeacher1.setText(name);
        innerPlanTeacher1.setVisibility(View.VISIBLE);
        innerPlanTeacher1.setChecked(true);
        //这里设置选择教师事件
        planTeacher = userName + "," + name;
    }

    private boolean checkInput() {
        if (StringUtil.isNullOrEmpty(innerPlanDate1.getText().toString())) {
            ToastUtil.show(this, String.format("%s未输入，请检查", getResources().getString(R.string.hintDate1)));
            return false;
        }
        if (StringUtil.isNullOrEmpty(innerPlanDate2.getText().toString())) {
            ToastUtil.show(this, String.format("%s未输入，请检查", getResources().getString(R.string.hintDate2)));
            return false;
        }
        if (StringUtil.isNullOrEmpty(innerPlanCourse.getText().toString())) {
            ToastUtil.show(this, String.format("%s未输入，请检查", getResources().getString(R.string.PlanCourseDesc)));
            return false;
        }
        if (StringUtil.isNullOrEmpty(innerPlanTerm.getText().toString())) {
            ToastUtil.show(this, String.format("%s未输入，请检查", getResources().getString(R.string.PlanTerm)));
            return false;
        }
        if (StringUtil.isNullOrEmpty(innerPlanNum.getText().toString())) {
            ToastUtil.show(this, String.format("%s未输入，请检查", getResources().getString(R.string.PlanNum)));
            return false;
        }
        if (StringUtil.isNullOrEmpty(planTeacher)) {
            ToastUtil.show(this, String.format("%s未选择，请检查", getResources().getString(R.string.PlanTeacher)));
            return false;
        }
        return true;
    }

    private void runThread(final String plan) {
        if (StringUtil.isNullOrEmpty(planId)) {
            Waiting.show(this, getResources().getString(R.string.LoadingPlanCreate));
        } else {
            Waiting.show(this, getResources().getString(R.string.LoadingPlanUpdate));
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (StringUtil.isNullOrEmpty(planId)) {
                    resultMap = TrainNetWebService.getInstance().InternalNewPlan(InnerPlanCreateUpdateActivity.this, plan);
                } else {
                    resultMap = TrainNetWebService.getInstance().InternalUpdatePlan(InnerPlanCreateUpdateActivity.this, plan);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String message = resultMap.get(getResources().getString(R.string.Message)).toString();
                        DialogUtil.getInstance().showDialog(wr, getResources().getString(R.string.tips), message, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog alertDialog = (AlertDialog) v.getTag();
                                alertDialog.dismiss();
                                String returnCode = resultMap.get(getResources().getString(R.string.ReturnCode)).toString();
                                if ("0".equals(returnCode)) {
                                    String innerPlanQuery = MySharedPreferences.getInstance().getString("InnerPlanQuery", InnerPlanCreateUpdateActivity.this);
                                    boolean isRun = true;
                                    if (currentTime.substring(0, 10).compareTo(innerPlanDate1.getText().toString()) < 0) {
                                        isRun = false;
                                    }
                                    if (!StringUtil.isNullOrEmpty(innerPlanQuery)) {
                                        if (!innerPlanPublished.isChecked() || (innerPlanPublished.isChecked() && !isRun)) {
                                            innerPlanQuery = "1" + innerPlanQuery.substring(1, innerPlanQuery.length());
                                        } else {
                                            innerPlanQuery = "0" + innerPlanQuery.substring(1, innerPlanQuery.length());
                                        }
                                        MySharedPreferences.getInstance().setStoreString("InnerPlanQuery", innerPlanQuery, InnerPlanCreateUpdateActivity.this);
                                    }
                                    readyGoThenKill(TrainInnerActivity.class);
                                }
                            }
                        }, null);
                        Waiting.dismiss();
                    }
                });
            }
        }).start();
    }

    private void showCourse(boolean b) {
        if (b) {
            innerPlanCourse.setHint(getResources().getString(R.string.hintCourse));
            innerPlanCourse.setOnClickListener(innerPlanCourseClick);
            innerPlanCourseTitle.setText("培训课程");
            if (!StringUtil.isNullOrEmpty(innerPlanCourseDesc.getText().toString())) {
                innerPlanCourseDesc.setVisibility(View.VISIBLE);
            } else {
                innerPlanCourseDesc.setVisibility(View.GONE);
            }
            autoCourseName = innerPlanCourse.getText().toString();
            innerPlanCourse.setText(headCourseName);
        } else {
            innerPlanCourse.setHint(null);
            innerPlanCourse.setOnClickListener(null);
            innerPlanCourseTitle.setText("培训主题");
            innerPlanCourseDesc.setVisibility(View.GONE);
            headCourseName = innerPlanCourse.getText().toString();
            innerPlanCourse.setText(autoCourseName);
        }
    }

    private String getType() {
        if (isHead) {
            return "Headquarters";
        } else {
            return "Autonomous";
        }
    }

    private void setType(boolean isHeadClick) {
        if (isHeadClick) {
            if (!isHead) {
                innerPlanHead.setBackground(getResources().getDrawable(R.drawable.trainnet_inner_border_s));
                innerPlanAuto.setBackground(getResources().getDrawable(R.drawable.trainnet_inner_border_u));
                showCourse(true);
            }
            isHead = true;
        } else {
            if (isHead) {
                innerPlanHead.setBackground(getResources().getDrawable(R.drawable.trainnet_inner_border_u));
                innerPlanAuto.setBackground(getResources().getDrawable(R.drawable.trainnet_inner_border_s));
                showCourse(false);
            }
            isHead = false;
        }
    }
}
