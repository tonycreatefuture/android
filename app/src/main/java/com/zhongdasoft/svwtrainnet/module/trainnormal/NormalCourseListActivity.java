package com.zhongdasoft.svwtrainnet.module.trainnormal;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.CRUD;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ActivityKey;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.greendao.DaoQuery;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserSearchHistory;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.SetUtil;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.widget.popwindow.AbstractSpinerAdapter;
import com.zhongdasoft.svwtrainnet.widget.popwindow.CustemSpinerAdapter;
import com.zhongdasoft.svwtrainnet.widget.popwindow.SpinerPopWindow;
import com.zhongdasoft.svwtrainnet.widget.popwindow.SpinnerOption;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class NormalCourseListActivity extends BaseActivity {
    TreeSet<String> coursePId;
    TreeSet<String> courseCId;
    private WeakReference<? extends BaseActivity> wr;
    private Button btnSearch;
    private EditText tvSearch;
    private TextView tv_parent;
    private TextView tv_child;
    private ListView lv;
    private List<SpinnerOption> s_data_list;
    private List<SpinnerOption> position_data_list;
    private List<SpinnerOption> parent_data_list;
    private List<SpinnerOption> child_data_list;
    private ArrayList<HashMap<String, Object>> courseList;
    private ArrayList<HashMap<String, Object>> listItem;
    private ArrayList<HashMap<String, Object>> tmpListItem;
    private BaseListViewAdapter mAdapter;
    private String rootId = null;
    private String typeId = null;
    private int pPos = 0;
    private int cPos = 0;
    private SpinnerOption spinnerOption;

    private AbstractSpinerAdapter<SpinnerOption> sAdapter;
    private AbstractSpinerAdapter<SpinnerOption> pAdapter;
    private AbstractSpinerAdapter<SpinnerOption> cAdapter;
    private AbstractSpinerAdapter<SpinnerOption> cAdapter2;
    private SpinerPopWindow sSpinerPopWindow;
    private SpinerPopWindow pSpinerPopWindow;
    private SpinerPopWindow cSpinerPopWindow;

    private boolean isParentChanged = false;
    private String userName;
    private boolean isFitMe = true;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    setParent();
                    break;
                case 2:
                    tmpListItem.clear();
                    listItem.clear();
                    for (HashMap<String, Object> course : courseList) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("info", StringUtil.objectToStr(course.get("CourseName")));
                        map.put("id", course.get("CourseNo").toString());
                        map.put("noRight", "");
                        listItem.add(map);
                    }
                    tmpListItem.addAll(listItem);
                    setListView();
                    Waiting.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        userName = MySharedPreferences.getInstance().getUserName();
        action();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_normal_courselist;
    }

    @Override
    protected int getMTitle() {
        return 0;
    }

    @Override
    public void onDestroy() {
        if (null != sSpinerPopWindow) {
            sSpinerPopWindow.dismiss();
        }
        if (null != pSpinerPopWindow) {
            pSpinerPopWindow.dismiss();
        }
        if (null != cSpinerPopWindow) {
            cSpinerPopWindow.dismiss();
        }
        super.onDestroy();
    }

    private void action() {
        listItem = new ArrayList<>();
        tmpListItem = new ArrayList<>();
        tv_parent = (TextView) findViewById(R.id.tv_course_parent);
        tv_child = (TextView) findViewById(R.id.tv_course_child);
        tvSearch = (EditText) findViewById(R.id.trainnet_search);
        tvSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (null != sSpinerPopWindow && sSpinerPopWindow.isShowing()) {
                        sSpinerPopWindow.dismiss();
                    }
                    String searchText = tvSearch.getText().toString();
                    if (!StringUtil.isNullOrEmpty(searchText)
                            && !DaoQuery.getInstance().existUserSearchHistory(userName, ActivityKey.CourseList, searchText)) {
                        UserSearchHistory ush = new UserSearchHistory();
                        ush.setUserName(userName);
                        ush.setUseWhere(ActivityKey.CourseList);
                        ush.setText(searchText);
                        CRUD.getInstance().InsertUserSearchHistory(ush);
                    }
                    filterList(searchText);
                    mAdapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });
        tvSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    sPopWindow();
                }
            }
        });
        tvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = tvSearch.getText().toString();
                filterList(searchText);
                mAdapter.notifyDataSetChanged();
            }
        });
        tv_parent.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                pPopWindow();
            }
        });
        tv_child.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                cPopWindow();
            }
        });
        setSearchHistory();
        setPosition();
    }

    private void filterList(String text) {
        tmpListItem.clear();
        if (text.length() == 0) {
            tmpListItem.addAll(listItem);
            return;
        }
        for (HashMap<String, Object> map : listItem) {
            if (map.get("info").toString().contains(text)) {
                tmpListItem.add(map);
            }
        }
    }

    private String getCpId() {
        return getResources().getString(R.string.CoursePId);
    }

    private String getCrId() {
        return getResources().getString(R.string.CourseRootId);
    }

    private String getCtId() {
        return getResources().getString(R.string.CourseTypeId);
    }

    private String getCidName() {
        return getResources().getString(R.string.CourseIdName);
    }

    private void setSearchHistory() {
        s_data_list = new ArrayList<>();
        List<UserSearchHistory> userSearchHistoryList = DaoQuery.getInstance().getUserSearchHistoryList(userName, ActivityKey.CourseList);
        SpinnerOption spinnerOption;
        for (int i = 0; i < userSearchHistoryList.size(); i++) {
            spinnerOption = new SpinnerOption(i + "", userSearchHistoryList.get(i).getText());
            s_data_list.add(spinnerOption);
        }
        if (s_data_list.size() > 0) {
            spinnerOption = new SpinnerOption(userSearchHistoryList.size() + "", "清除历史记录");
            s_data_list.add(0, spinnerOption);
        }
        // 设置Adapter
        sAdapter = new CustemSpinerAdapter<>(this, true);
        sAdapter.refreshData(s_data_list, 0);

        sSpinerPopWindow = new SpinerPopWindow(this, true);
        sSpinerPopWindow.setDismiss(false);
        sSpinerPopWindow.setOutsideTouchable(false);
        sSpinerPopWindow.setAdatper(sAdapter);
        sSpinerPopWindow
                .setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
                    @Override
                    public void onItemClick(int pos) {
                        sClick(pos);
                    }
                });
    }

    private void sClick(int pos) {
        sSpinerPopWindow.dismiss();
        if (pos == 0) {
            List<UserSearchHistory> userSearchHistoryList = DaoQuery.getInstance().getUserSearchHistoryList(userName, ActivityKey.CourseList);
            CRUD.getInstance().DeleteUserSearchHistory(userSearchHistoryList);
            s_data_list.clear();
            sAdapter.notifyDataSetChanged();
        } else {
            String searchText = s_data_list.get(pos).getText().toString();
            tvSearch.setText(searchText);
            filterList(searchText);
        }
    }

    private void sPopWindow() {
        sSpinerPopWindow.setWidth(tvSearch.getWidth());
        sSpinerPopWindow.showAsDropDown(tvSearch, 0, 0);
    }

    private void pPopWindow() {
        pSpinerPopWindow.setWidth(tv_parent.getWidth() * 2);
        pSpinerPopWindow.showAsDropDown(tv_parent, 0, 0);
        tv_parent.setBackground(getResources().getDrawable(R.drawable.trainnet_coursebutton));
        tv_child.setBackground(null);
    }

    private void setPosition() {
        position_data_list = new ArrayList<>();
        SpinnerOption spinnerOption;
        spinnerOption = new SpinnerOption("0", "适合我");
        position_data_list.add(spinnerOption);
        spinnerOption = new SpinnerOption("1", "全部岗位");
        position_data_list.add(spinnerOption);
        // 设置Adapter
        pAdapter = new CustemSpinerAdapter<>(this, true);
        pAdapter.refreshData(position_data_list, 0);

        pSpinerPopWindow = new SpinerPopWindow(this, true);
        pSpinerPopWindow.setAdatper(pAdapter);
        pSpinerPopWindow
                .setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
                    @Override
                    public void onItemClick(int pos) {
                        posClick(pos);
                    }
                });
        pSpinerPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tv_parent.setBackground(null);
            }
        });
        posClick(pPos);
    }

    private void posClick(int pos) {
        if (pos < 0 || pos >= position_data_list.size()) {
            return;
        }
        SpinnerOption spinnerOption = position_data_list.get(pos);
        tv_parent.setText(spinnerOption.getText() + "▼");
        tv_parent.setTag(spinnerOption.getValue());
        tv_parent.setBackground(null);
        rootId = "-2";
        typeId = "-1";
        pPos = 0;
        cPos = 0;
        TrainNetApp.getCache().put(getCrId(), rootId);
        TrainNetApp.getCache().put(getCtId(), typeId);
        Waiting.show(this, getResources().getString(R.string.Loading));
        isParentChanged = true;
        if (0 == pos) {
            isFitMe = true;
        } else {
            isFitMe = false;
        }
        new Thread(new NormalCourseListThread()).start();
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
        cAdapter = new CustemSpinerAdapter<>(this, false);
        cAdapter.refreshData(parent_data_list, 0);

        cSpinerPopWindow = new SpinerPopWindow(this, false);
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
        courseCId = SetUtil.HashSet2TreeSet((HashSet<String>) TrainNetApp.getCache().getAsObject(getCpId() + parentId));
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
        cAdapter2 = new CustemSpinerAdapter<>(this, false);
        cAdapter2.refreshData(child_data_list, 0);

        if (cSpinerPopWindow == null) {
            cSpinerPopWindow = new SpinerPopWindow(this, false);
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
        tv_child.setBackground(getResources().getDrawable(R.drawable.trainnet_coursebutton));
        tv_parent.setBackground(null);
    }

    private void childClick(int pos) {
        if (pos < 0 || pos >= child_data_list.size()) {
            return;
        }
        cPos = pos;
        SpinnerOption pSpinnerOption = parent_data_list.get(pPos);
        SpinnerOption cSpinnerOption = child_data_list.get(cPos);
        tv_child.setText(pSpinnerOption.getText() + "/" + cSpinnerOption.getText() + "▼");
        tv_child.setTag(spinnerOption.getValue());
        if (!cSpinerPopWindow.isShowing()) {
            tv_child.setBackground(null);
        } else {
            tv_child.setBackground(getResources().getDrawable(R.drawable.trainnet_coursebutton));
        }
        typeId = child_data_list.get(pos).getValue();
        TrainNetApp.getCache().put(getCrId(), rootId);
        TrainNetApp.getCache().put(getCtId(), typeId);
        new Thread(new NormalCourseListThread()).start();
    }

    private void setSpinnerOption(String key) {
        spinnerOption = new SpinnerOption(key, TrainNetApp.getCache().getAsString(getCidName() + key));
    }

    private void setListView() {
        if (tmpListItem.size() <= 0) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("img_noData", R.drawable.trainnet_no_img);
            map.put("noLine", "");
            map.put("noRight", "");
            tmpListItem.add(map);
        }
        mAdapter = new BaseListViewAdapter(this, tmpListItem);
        lv = (ListView) findViewById(R.id.listview_course);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.GRAY));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (tmpListItem.size() > 0 && tmpListItem.get(arg2).containsKey("id")) {
                    String str = tmpListItem.get(arg2).get("id").toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("item", str);
                    bundle.putString("className", ActivityKey.CourseList);
                    readyGo(NormalCourseContentActivity.class, bundle);
                }
            }
        });
    }

    private void setDataList(boolean isCached) {
        if (!isFitMe) {
            int courseType = Integer.parseInt(typeId);
            if (isCached) {
                String AllCourseRefresh = TrainNetApp.getCache().getAsString(CacheKey.AllCourseRefresh + courseType);
                if (!StringUtil.isNullOrEmpty(AllCourseRefresh)) {
                    courseList = TrainNetApp.getGson().fromJson(AllCourseRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                    }.getType());
                } else {
                    courseList = TrainNetWebService.getInstance().GetCourseList(this, courseType, getResources().getString(R.string.Normal));
                    TrainNetApp.getCache().put(CacheKey.AllCourseRefresh + courseType, TrainNetApp.getGson().toJson(courseList));
                }
            } else {
                courseList = TrainNetWebService.getInstance().GetCourseList(this, courseType, getResources().getString(R.string.Normal));
            }
        } else {
            if (isCached) {
                String SuitMeCourseRefresh = TrainNetApp.getCache().getAsString(CacheKey.SuitMeCourseRefresh);
                if (!StringUtil.isNullOrEmpty(SuitMeCourseRefresh)) {
                    courseList = TrainNetApp.getGson().fromJson(SuitMeCourseRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                    }.getType());
                } else {
                    courseList = TrainNetWebService.getInstance().GetSuitableCourse(this);
                    TrainNetApp.getCache().put(CacheKey.SuitMeCourseRefresh, TrainNetApp.getGson().toJson(courseList));
                }
            } else {
                courseList = TrainNetWebService.getInstance().GetSuitableCourse(this);
            }
        }
    }

    public class NormalCourseListThread implements Runnable {
        @Override
        public void run() {
            coursePId = SetUtil.HashSet2TreeSet((HashSet<String>) TrainNetApp.getCache().getAsObject(getCpId()));
            rootId = TrainNetApp.getCache().getAsString(getCrId());
            typeId = TrainNetApp.getCache().getAsString(getCtId());
            setCourseType();
        }

        private void setCourseType() {
            if (isParentChanged) {
                isParentChanged = false;
                if (isFitMe) {
                    HashSet<String> parent = new HashSet<>();
                    parent.add("-2");
                    HashSet<String> child = new HashSet<>();
                    child.add("-1");
                    TrainNetApp.getCache().put(getCpId(), parent);
                    TrainNetApp.getCache().put(getCpId() + "-2", child);
                    TrainNetApp.getCache().put(getCidName() + "-1", "全部");
                    TrainNetApp.getCache().put(getCidName() + "-2", "全部");
                    coursePId = SetUtil.HashSet2TreeSet(parent);
                    courseCId = SetUtil.HashSet2TreeSet(child);
                } else {
                    ArrayList<HashMap<String, Object>> myCourseTypeList = TrainNetWebService.getInstance().GetCourseTypeList(NormalCourseListActivity.this);
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
                        TrainNetApp.getCache().put(getCpId() + key, hs);
                        TrainNetApp.getCache().put(getCidName() + key, pcIdNameMap.get(key));
                        for (String key0 : pcIdMap.get(key)) {
                            TrainNetApp.getCache().put(getCidName() + key0, pcIdNameMap.get(key0));
                        }
                    }
                    TrainNetApp.getCache().put(getCpId(), parentKetSet);
                    coursePId = SetUtil.HashSet2TreeSet((HashSet<String>) TrainNetApp.getCache().getAsObject(getCpId()));
                    courseCId = SetUtil.HashSet2TreeSet((HashSet<String>) TrainNetApp.getCache().getAsObject(getCpId() + rootId));
                }
                mHandler.sendEmptyMessage(1);
            } else {
                courseCId = SetUtil.HashSet2TreeSet((HashSet<String>) TrainNetApp.getCache().getAsObject(getCpId() + rootId));
                if (parent_data_list == null) {
                    mHandler.sendEmptyMessage(1);
                    return;
                }
                setDataList(false);
                mHandler.sendEmptyMessage(2);
            }
        }
    }
}
