package com.zhongdasoft.svwtrainnet.module.trainonline;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.OnLineListViewAdapter;
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
import com.zhongdasoft.svwtrainnet.util.WebserviceUtil;
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

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/12/1 16:34
 * 修改人：Administrator
 * 修改时间：2016/12/1 16:34
 * 修改备注：
 */

public class TrainOnLineActivity extends BaseActivity {
    TreeSet<String> coursePId;
    TreeSet<String> courseCId;
    private WeakReference<? extends BaseActivity> wr;
    private Button btnSearch;
    private EditText tvSearch;
    private TextView tv_grandParent;
    private TextView tv_parent;
    private TextView tv_child;
    private ListView lv;
    private List<SpinnerOption> s_data_list;
    private List<SpinnerOption> grand_data_list;
    private List<SpinnerOption> position_data_list;
    private List<SpinnerOption> parent_data_list;
    private List<SpinnerOption> child_data_list;
    private ArrayList<HashMap<String, Object>> courseList;
    private ArrayList<HashMap<String, Object>> listItem;
    private ArrayList<HashMap<String, Object>> tmpListItem;
    private OnLineListViewAdapter mAdapter;
    private String rootId = null;
    private String typeId = null;
    private int pPos = 0;
    private int cPos = 0;
    private SpinnerOption spinnerOption;
    private int lastGrand = 0;
    private int lastPos = 0;

    private AbstractSpinerAdapter<SpinnerOption> sAdapter;
    private AbstractSpinerAdapter<SpinnerOption> gAdapter;
    private AbstractSpinerAdapter<SpinnerOption> pAdapter;
    private AbstractSpinerAdapter<SpinnerOption> cAdapter;
    private AbstractSpinerAdapter<SpinnerOption> cAdapter2;
    private SpinerPopWindow sSpinerPopWindow;
    private SpinerPopWindow gSpinerPopWindow;
    private SpinerPopWindow pSpinerPopWindow;
    private SpinerPopWindow cSpinerPopWindow;

    private boolean isParentChanged = false;
    private String userName;

    private String[] status = {"全部", "待选课", "未完成", "已完成"};
    private String[] type = {"必修", "选修", "入职培训包"};
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    setParent();
                    break;
                case 2:
                    tmpListItem.clear();
                    listItem = WebserviceUtil.getInstance().onlineCourseList(TrainOnLineActivity.this, courseList, false);
                    int pos1 = Integer.parseInt(tv_grandParent.getTag().toString());
                    int pos2 = Integer.parseInt(tv_parent.getTag().toString());
                    boolean required;
                    boolean isPackageCourse;
                    int finishNum = 0;
                    int chapterNum = 0;
                    for (HashMap<String, Object> mapItem : listItem) {
                        required = Boolean.parseBoolean(mapItem.get("required").toString());
                        isPackageCourse = Boolean.parseBoolean(mapItem.get("isPackageCourse").toString());
                        finishNum = Integer.parseInt(mapItem.get("finishNum").toString());
                        chapterNum = Integer.parseInt(mapItem.get("chapterNum").toString());
                        if (pos1 > 0) {
                            switch (pos1) {
                                case 1:
                                    if (mapItem.containsKey("select_event")) {
                                        tmpListItem.add(mapItem);
                                    }
                                    break;
                                case 2:
                                    if (finishNum < chapterNum) {
                                        tmpListItem.add(mapItem);
                                    }
                                    break;
                                case 3:
                                    if (finishNum >= chapterNum) {
                                        tmpListItem.add(mapItem);
                                    }
                                    break;
                            }
                        } else {
                            tmpListItem.add(mapItem);
                        }
                        if (tmpListItem.contains(mapItem)) {
                            switch (pos2) {
                                case 0:
                                    //必修
                                    if (!required) {
                                        tmpListItem.remove(mapItem);
                                    }
                                    break;
                                case 1:
                                    //选修
                                    if (required) {
                                        tmpListItem.remove(mapItem);
                                    }
                                    break;
                                case 2:
                                    //入职培训包
                                    if (!isPackageCourse) {
                                        tmpListItem.remove(mapItem);
                                    }
                                    break;
                            }
                        }
                        if (null != typeId && !"-1".equals(typeId) && !typeId.equals(mapItem.get("courseTypeId").toString())) {
                            tmpListItem.remove(mapItem);
                        }
                    }
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

        tmpListItem = new ArrayList<>();
        listItem = new ArrayList<>();
        userName = MySharedPreferences.getInstance().getUserName(this);
        action();
    }

    @Override
    public void onDestroy() {
        if (null != sSpinerPopWindow) {
            sSpinerPopWindow.dismiss();
            sSpinerPopWindow = null;
        }
        if (null != gSpinerPopWindow) {
            gSpinerPopWindow.dismiss();
            gSpinerPopWindow = null;
        }
        if (null != pSpinerPopWindow) {
            pSpinerPopWindow.dismiss();
            pSpinerPopWindow = null;
        }
        if (null != cSpinerPopWindow) {
            cSpinerPopWindow.dismiss();
            cSpinerPopWindow = null;
        }
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_train_online;
    }

    @Override
    protected int getMTitle() {
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
        if (tvSearch != null) {
            tvSearch.clearFocus();
        }
    }

    private void action() {
        tv_grandParent = (TextView) findViewById(R.id.tv_course_grandParent);
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
                            && !DaoQuery.getInstance().existUserSearchHistory(userName, ActivityKey.OnLine, searchText)) {
                        UserSearchHistory ush = new UserSearchHistory();
                        ush.setUserName(userName);
                        ush.setUseWhere(ActivityKey.OnLine);
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
                if (null != mAdapter) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        tv_grandParent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gPopWindow();
            }
        });
        tv_parent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pPopWindow();
            }
        });
        tv_child.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cPopWindow();
            }
        });
        setSearchHistory();
        setPosition();
        setGrand();

        setCourseType();
        Waiting.show(this, getResources().getString(R.string.Loading));
        isParentChanged = true;
        new Thread(new NormalCourseListThread()).start();
    }

    private void setCourseType() {
        pPos = 0;
        cPos = 0;
        rootId = "-2";
        typeId = "-1";
        getCache().put(getCrId(), rootId);
        getCache().put(getCtId(), typeId);
    }

    private void filterList(String text) {
        tmpListItem.clear();
        if (text.length() == 0) {
            tmpListItem.addAll(listItem);
            return;
        }
        String textLower = text.toLowerCase();
        for (HashMap<String, Object> map : listItem) {
            if (map.get("title").toString().toLowerCase().contains(textLower)) {
                tmpListItem.add(map);
            }
        }
    }

    private String getCpId() {
        return getResources().getString(R.string.MCoursePId);
    }

    private String getCrId() {
        return getResources().getString(R.string.MCourseRootId);
    }

    private String getCtId() {
        return getResources().getString(R.string.MCourseTypeId);
    }

    private String getCidName() {
        return getResources().getString(R.string.MCourseIdName);
    }

    private void setSearchHistory() {
        s_data_list = new ArrayList<>();
        List<UserSearchHistory> userSearchHistoryList = DaoQuery.getInstance().getUserSearchHistoryList(userName, ActivityKey.OnLine);
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
        if (null != sSpinerPopWindow) {
            sSpinerPopWindow.dismiss();
        }
        if (pos == 0) {
            List<UserSearchHistory> userSearchHistoryList = DaoQuery.getInstance().getUserSearchHistoryList(userName, ActivityKey.OnLine);
            CRUD.getInstance().DeleteUserSearchHistory(userSearchHistoryList);
            s_data_list.clear();
            if (null != sAdapter) {
                sAdapter.notifyDataSetChanged();
            }
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

    private void gPopWindow() {
        gSpinerPopWindow.setWidth(tv_grandParent.getWidth() * 3);
        gSpinerPopWindow.showAsDropDown(tv_grandParent, 0, 0);
        tv_grandParent.setBackground(getResources().getDrawable(R.drawable.trainnet_coursebutton));
        tv_parent.setBackground(null);
        tv_child.setBackground(null);
    }

    private void setGrand() {
        grand_data_list = new ArrayList<>();
        SpinnerOption spinnerOption;
        for (int i = 0; i < status.length; i++) {
            spinnerOption = new SpinnerOption(i + "", status[i]);
            grand_data_list.add(spinnerOption);
        }
        // 设置Adapter
        gAdapter = new CustemSpinerAdapter<>(this, true);
        gAdapter.refreshData(grand_data_list, 0);

        gSpinerPopWindow = new SpinerPopWindow(this, true);
        gSpinerPopWindow.setAdatper(gAdapter);
        gSpinerPopWindow
                .setItemListener(new AbstractSpinerAdapter.IOnItemSelectListener() {
                    @Override
                    public void onItemClick(int pos) {
                        grandClick(pos);
                    }
                });
        gSpinerPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                tv_grandParent.setBackground(null);
            }
        });
        lastGrand = 2;
        grandClick(lastGrand);
    }

    private void grandClick(int pos) {
        if (pos < 0 || pos >= grand_data_list.size()) {
            return;
        }
        SpinnerOption spinnerOption = grand_data_list.get(pos);
        tv_grandParent.setText(spinnerOption.getText() + "▼");
        tv_grandParent.setTag(spinnerOption.getValue());
        tv_grandParent.setBackground(null);
        if (pos != lastGrand) {
            lastGrand = pos;
            setCourseType();
            setParent();
        }
    }

    private void pPopWindow() {
        pSpinerPopWindow.setWidth(tv_parent.getWidth() * 3);
        pSpinerPopWindow.showAsDropDown(tv_parent, 0, 0);
        tv_parent.setBackground(getResources().getDrawable(R.drawable.trainnet_coursebutton));
        tv_child.setBackground(null);
    }

    private void setPosition() {
        position_data_list = new ArrayList<>();
        SpinnerOption spinnerOption;
        for (int i = 0; i < type.length; i++) {
            spinnerOption = new SpinnerOption(i + "", type[i]);
            position_data_list.add(spinnerOption);
        }
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
        lastPos = 0;
        posClick(lastPos);
    }

    private void posClick(int pos) {
        if (pos < 0 || pos >= position_data_list.size()) {
            return;
        }
        SpinnerOption spinnerOption = position_data_list.get(pos);
        tv_parent.setText(spinnerOption.getText() + "▼");
        tv_parent.setTag(spinnerOption.getValue());
        tv_parent.setBackground(null);
        if (pos != lastPos) {
            lastPos = pos;
            setCourseType();
            setParent();
        }
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
        if (pos < 0 || pos > parent_data_list.size()) {
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
        courseCId = SetUtil.HashSet2TreeSet((HashSet<String>) getCache().getAsObject(getCpId() + parentId));
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
        cSpinerPopWindow.setWidth(tv_child.getWidth() * 3);
        cSpinerPopWindow.showAsDropDown(tv_child, 0, 0);
        tv_child.setBackground(getResources().getDrawable(R.drawable.trainnet_coursebutton));
        tv_parent.setBackground(null);
    }

    private void childClick(int pos) {
        if (pos < 0 || pos > child_data_list.size()) {
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
        getCache().put(getCrId(), rootId);
        getCache().put(getCtId(), typeId);
        new Thread(new NormalCourseListThread()).start();
    }

    private void setSpinnerOption(String key) {
        spinnerOption = new SpinnerOption(key, getCache().getAsString(getCidName() + key));
    }

    private void setListView() {
        if (tmpListItem.size() <= 0) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("img_noData", R.drawable.trainnet_no_img);
            map.put("info3Label", "");
            map.put("noLine", "");
            tmpListItem.add(map);
        }
        mAdapter = new OnLineListViewAdapter(wr, tmpListItem, 0);
        lv = (ListView) findViewById(R.id.listview_train_online);
        lv.setAdapter(mAdapter);
        lv.setSelector(R.color.transparent);
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
//                if (tmpListItem.size() > 0 && tmpListItem.get(pos).containsKey("progress_event")) {
//                    boolean isOpen = false;
//                    if (tmpListItem.get(pos).containsKey("info1")) {
//                        isOpen = true;
//                    }
//                    for (HashMap<String, Object> map : tmpListItem) {
//                        map.remove("info1");
//                        map.remove("info2");
//                        map.remove("info3");
//                    }
//                    if (!isOpen) {
//                        tmpListItem.get(pos).put("info1", tmpListItem.get(pos).get("firstTime"));
//                        tmpListItem.get(pos).put("info2", tmpListItem.get(pos).get("lastTime"));
//                        int finishNum = Integer.parseInt(tmpListItem.get(pos).get("finishNum").toString());
//                        int chapterNum = Integer.parseInt(tmpListItem.get(pos).get("chapterNum").toString());
//                        if (0 == chapterNum) {
//                            tmpListItem.get(pos).put("info3", 0);
//                        } else {
//                            tmpListItem.get(pos).put("info3", finishNum * 100 / chapterNum);
//                        }
//                    }
//                    mAdapter.notifyDataSetChanged();
//                }
            }
        });
    }

    private void setDataList(boolean isCached) {
        if (isCached) {
            String courseType = null == typeId ? "" : typeId;
            String AllOnLineCourseRefresh = getCache().getAsString(CacheKey.AllOnLineCourseRefresh + courseType);
            if (!StringUtil.isNullOrEmpty(AllOnLineCourseRefresh)) {
                courseList = getGson().fromJson(AllOnLineCourseRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
            } else {
                courseList = TrainNetWebService.getInstance().OnlineCourseList(this, typeId);
                getCache().put(CacheKey.AllOnLineCourseRefresh + courseType, getGson().toJson(courseList));
            }
        } else {
            courseList = TrainNetWebService.getInstance().OnlineCourseList(this, typeId);
        }
    }

    class NormalCourseListThread implements Runnable {
        @Override
        public void run() {
            coursePId = SetUtil.HashSet2TreeSet((HashSet<String>) getCache().getAsObject(getCpId()));
            rootId = getCache().getAsString(getCrId());
            typeId = getCache().getAsString(getCtId());
            setCourseType();
        }

        private void setCourseType() {
            if (isParentChanged) {
                isParentChanged = false;
                ArrayList<HashMap<String, Object>> courseTypeList = TrainNetWebService.getInstance().OnlineCourseTypes(TrainOnLineActivity.this);
                if (courseTypeList.size() <= 0
                        || courseTypeList.get(0).size() <= 0) {
                    return;
                }
                HashSet<String> parentKetSet = new HashSet<>();
                int id;
                int parentId;
                String idStr;
                String parentIdStr;
                Map<String, HashSet<String>> pcIdMap = new HashMap<>();
                Map<String, String> pcIdNameMap = new HashMap<>();
                for (Map<String, Object> map : courseTypeList) {
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
                    getCache().put(getCpId() + key, hs);
                    getCache().put(getCidName() + key, pcIdNameMap.get(key));
                    for (String key0 : pcIdMap.get(key)) {
                        getCache().put(getCidName() + key0, pcIdNameMap.get(key0));
                    }
                }
                getCache().put(getCpId(), parentKetSet);
                coursePId = SetUtil.HashSet2TreeSet((HashSet<String>) getCache().getAsObject(getCpId()));
                courseCId = SetUtil.HashSet2TreeSet((HashSet<String>) getCache().getAsObject(getCpId() + rootId));
                mHandler.sendEmptyMessage(1);
            } else {
                courseCId = SetUtil.HashSet2TreeSet((HashSet<String>) getCache().getAsObject(getCpId() + rootId));
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
