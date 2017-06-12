package com.zhongdasoft.svwtrainnet.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ACache;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.SetUtil;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.widget.popwindow.AbstractSpinerAdapter;
import com.zhongdasoft.svwtrainnet.widget.popwindow.CustemSpinerAdapter;
import com.zhongdasoft.svwtrainnet.widget.popwindow.SpinerPopWindow;
import com.zhongdasoft.svwtrainnet.widget.popwindow.SpinnerOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/9/7 17:15
 * 修改人：Administrator
 * 修改时间：2016/9/7 17:15
 * 修改备注：
 */
public class InnerCourse {
    private String rootId = null;
    private String typeId = null;
    private int pPos = 0;
    private int cPos = 0;
    private ACache mCache;
    private Gson gson;
    private boolean isParentChanged = false;
    private boolean isChildChanged = false;
    private AbstractSpinerAdapter<SpinnerOption> pAdapter;
    private AbstractSpinerAdapter<SpinnerOption> cAdapter;
    private AbstractSpinerAdapter<SpinnerOption> cAdapter2;
    private SpinerPopWindow pSpinerPopWindow;
    private SpinerPopWindow cSpinerPopWindow;
    private List<SpinnerOption> parent_data_list;
    private List<SpinnerOption> child_data_list;
    TreeSet<String> coursePId;
    TreeSet<String> courseCId;
    private SpinnerOption spinnerOption;

    private TextView tv_child;
    private BaseActivity activity;
    private ArrayList<HashMap<String, Object>> courseList;
    private ArrayList<HashMap<String, Object>> listItem;
    private ListView lv;
    private BaseListViewAdapter mAdapter;
    private String CourseNo;
    private String CourseName;
    private AlertDialog alertDialog;
    private Context mContext;

    public InnerCourse(BaseActivity activity, ListView lv, TextView tv_child, AlertDialog alertDialog) {
        this.tv_child = tv_child;
        this.activity = activity;
        this.lv = lv;
        this.alertDialog = alertDialog;
        mCache = TrainNetApp.getCache();
        gson = TrainNetApp.getGson();
        rootId = "-2";
        typeId = "-1";
        pPos = 0;
        cPos = 0;
        mCache.put(getCrId(), rootId);
        mCache.put(getCtId(), typeId);
        Waiting.show(activity, activity.getResources().getString(R.string.Loading));
        isParentChanged = true;
        tv_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cPopWindow();
            }
        });
        new Thread(new InnerCourseThread()).start();
    }

    public String getCourseNo() {
        return CourseNo;
    }

    public String getCourseName() {
        return CourseName;
    }

    private void setParent() {
        parent_data_list = new ArrayList<>();
        for (String key : coursePId) {
            setSpinnerOption(key);
            parent_data_list.add(spinnerOption);
        }
        for (String key : coursePId) {
            if (key.equalsIgnoreCase(rootId)) {
                break;
            }
            pPos++;
        }
        // 设置Adapter
        cAdapter = new CustemSpinerAdapter<>(activity, false);
        cAdapter.refreshData(parent_data_list, 0);

        cSpinerPopWindow = new SpinerPopWindow(activity, false);
        cSpinerPopWindow.setAdatper(cAdapter);
        cSpinerPopWindow
                .setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
                    @Override
                    public void onItemClick(int pos) {
                        parentClick(pos);
                    }
                });
        parentClick(pPos);
    }

    private void parentClick(int pos) {
        if (pos < 0 || pos >= parent_data_list.size()) {
            return;
        }
        if (pPos != pos) {
            pPos = pos;
            typeId = null;
            cPos = 0;
        }
        rootId = parent_data_list.get(pos).getValue();
        setChild(rootId);
    }

    private void setChild(String parentId) {
        child_data_list = new ArrayList<>();
        child_data_list.clear();
        courseCId = SetUtil.HashSet2TreeSet((HashSet<String>) mCache.getAsObject(getCpId() + parentId));
        if (courseCId != null) {
            for (String key : courseCId) {
                setSpinnerOption(key);
                child_data_list.add(spinnerOption);
            }
            for (String key : courseCId) {
                if (StringUtil.isNullOrEmpty(typeId)
                        || key.equalsIgnoreCase(typeId)) {
                    break;
                }
                cPos++;
            }
        }
        // 设置Adapter
        cAdapter2 = new CustemSpinerAdapter<>(activity, false);
        cAdapter2.refreshData(child_data_list, 0);

        if (cSpinerPopWindow == null) {
            cSpinerPopWindow = new SpinerPopWindow(activity, false);
        }
        cSpinerPopWindow.setAdatper2(cAdapter2);
        cSpinerPopWindow
                .setItemListener2(new AbstractSpinerAdapter.IOnItemSelectListener() {
                    @Override
                    public void onItemClick(int pos) {
                        childClick(pos);
                    }
                });

        cSpinerPopWindow.setFocusable(false);
        cSpinerPopWindow.setOutsideTouchable(true);
        cSpinerPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tv_child.setBackground(null);
            }
        });
        childClick(cPos);
    }

    private void cPopWindow() {
        cSpinerPopWindow.setWidth(tv_child.getWidth() * 2);
        cSpinerPopWindow.showAsDropDown(tv_child, 0, 0);
        tv_child.setBackground(activity.getResources().getDrawable(R.drawable.trainnet_coursebutton));
    }

    private void childClick(int pos) {
        if (pos < 0 || pos >= child_data_list.size()) {
            return;
        }
        //Waiting.show(activity, activity.getResources().getString(R.string.Loading));
        cPos = pos;
        SpinnerOption pSpinnerOption = parent_data_list.get(pPos);
        SpinnerOption cSpinnerOption = child_data_list.get(cPos);
        tv_child.setText(pSpinnerOption.getText() + "/" + cSpinnerOption.getText() + "▼");
        tv_child.setTag(cSpinnerOption.getValue());
        if (!cSpinerPopWindow.isShowing()) {
            tv_child.setBackground(null);
        } else {
            tv_child.setBackground(activity.getResources().getDrawable(R.drawable.trainnet_coursebutton));
        }
        typeId = child_data_list.get(pos).getValue();
        mCache.put(getCrId(), rootId);
        mCache.put(getCtId(), typeId);
        new Thread(new InnerCourseThread()).start();
    }

    private void setSpinnerOption(String key) {
        spinnerOption = new SpinnerOption(key, mCache.getAsString(getCidName() + key));
    }

    private String getCpId() {
        return activity.getResources().getString(R.string.Internal) + activity.getResources().getString(R.string.CoursePId);
    }

    private String getCrId() {
        return activity.getResources().getString(R.string.Internal) + activity.getResources().getString(R.string.CourseRootId);
    }

    private String getCtId() {
        return activity.getResources().getString(R.string.Internal) + activity.getResources().getString(R.string.CourseTypeId);
    }

    private String getCidName() {
        return activity.getResources().getString(R.string.Internal) + activity.getResources().getString(R.string.CourseIdName);
    }

    private class InnerCourseThread implements Runnable {
        @Override
        public void run() {
            coursePId = SetUtil.HashSet2TreeSet((HashSet<String>) mCache.getAsObject(getCpId()));
            rootId = mCache.getAsString(getCrId());
            typeId = mCache.getAsString(getCtId());
            setCourseType();
        }

        private void setCourseType() {
            if (isParentChanged) {
                isParentChanged = false;
                isChildChanged = true;
                ArrayList<HashMap<String, Object>> myCourseTypeList = TrainNetWebService.getInstance().GetCourseTypeList(activity);
                if (myCourseTypeList.size() <= 0
                        || myCourseTypeList.get(0).size() <= 0) {
                    return;
                }
                HashSet<String> parentKetSet = new HashSet<>();
                int id;
                int parentId;
                String idStr;
                String parentIdStr;
                Map<String, HashSet<String>> pcIdMap = new HashMap<>();
                Map<String, String> pcIdNameMap = new HashMap<>();
                for (Map<String, Object> map : myCourseTypeList) {
                    if (map.containsKey("Id") && map.containsKey("Name")) {
                        pcIdNameMap.put(map.get("Id").toString(),
                                map.get("Name").toString());
                    }
                    if (map.containsKey("Id") && map.containsKey("parentId")) {
                        idStr = map.get("Id").toString();
                        parentIdStr = map.get("parentId").toString();
                        id = Integer.parseInt(idStr);
                        parentId = Integer.parseInt(parentIdStr);
                        if (parentId != 0 && id != parentId) {
                            parentKetSet.add(parentIdStr);
                            if (pcIdMap.containsKey(parentIdStr)) {
                                pcIdMap.get(parentIdStr).add(idStr);
                            } else {
                                HashSet<String> cidSet = new HashSet<>();
                                cidSet.add(idStr);
                                pcIdMap.put(parentIdStr, cidSet);
                            }
                        }
                    }
                }
                parentKetSet.add("-2");
                pcIdNameMap.put("-2", "全部");
                pcIdNameMap.put("-1", "全部");
                HashSet<String> cidSet = new HashSet<>();
                cidSet.add("-1");
                pcIdMap.put("-2", cidSet);
                rootId = "-2";
                for (String key : pcIdMap.keySet()) {
                    HashSet<String> hs = pcIdMap.get(key);
                    mCache.put(getCpId() + key, hs);
                    mCache.put(getCidName() + key, pcIdNameMap.get(key));
                    for (String key0 : pcIdMap.get(key)) {
                        mCache.put(getCidName() + key0, pcIdNameMap.get(key0));
                    }
                }
                mCache.put(getCpId(), parentKetSet);
                coursePId = SetUtil.HashSet2TreeSet((HashSet<String>) mCache.getAsObject(getCpId()));
                courseCId = SetUtil.HashSet2TreeSet((HashSet<String>) mCache.getAsObject(getCpId() + rootId));
                mHandler.sendEmptyMessage(1);
            } else {
                courseCId = SetUtil.HashSet2TreeSet((HashSet<String>) mCache.getAsObject(getCpId() + rootId));
                if (parent_data_list == null) {
                    mHandler.sendEmptyMessage(1);
                    return;
                }
                setDataList();
                mHandler.sendEmptyMessage(2);
            }
        }
    }

    private void setDataList() {
        int courseType = Integer.parseInt(typeId);
        String AllInternalCourseRefresh = mCache.getAsString(CacheKey.AllInternalCourseRefresh + courseType);
        if (!StringUtil.isNullOrEmpty(AllInternalCourseRefresh)) {
            courseList = gson.fromJson(AllInternalCourseRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
        } else {
            courseList = TrainNetWebService.getInstance().GetCourseList(
                    activity, courseType, activity.getResources().getString(R.string.Internal));
//            courseList = filterExpiredCourse();
            mCache.put(CacheKey.AllInternalCourseRefresh + courseType, gson.toJson(courseList));
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    setParent();
                    break;
                case 2:
                    listItem = new ArrayList<>();
                    for (HashMap<String, Object> course : courseList) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("info", course.get("CourseName").toString());
                        map.put("id", course.get("CourseNo").toString());
                        map.put("noRight", "");
                        listItem.add(map);
                    }
                    setListView();
                    Waiting.dismiss();
                    break;
            }
        }
    };

    private void setListView() {
        if (listItem.size() <= 0) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("img_noData", R.drawable.trainnet_no_img);
            map.put("noLine", "");
            map.put("noRight", "");
            listItem.add(map);
        }
        mAdapter = new BaseListViewAdapter(activity, listItem);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.GRAY));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (listItem.size() > 0 && listItem.get(arg2).containsKey("id")) {
                    CourseNo = listItem.get(arg2).get("id").toString();
                    CourseName = listItem.get(arg2).get("info").toString();
                    alertDialog.dismiss();
                }
            }
        });
    }
}
