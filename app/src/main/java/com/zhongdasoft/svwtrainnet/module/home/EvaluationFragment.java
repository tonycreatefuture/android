package com.zhongdasoft.svwtrainnet.module.home;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.WebserviceUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/8/18 17:54
 * 修改人：Administrator
 * 修改时间：2016/8/18 17:54
 * 修改备注：
 */
public class EvaluationFragment extends Fragment {
    private int position;
    //    private ArrayList<HashMap<String, Object>> listItemOnLine;
    private ArrayList<HashMap<String, Object>> listItemOffLine;
    private ArrayList<HashMap<String, Object>> listItemExtra;
    private ArrayList<HashMap<String, Object>> evaluationList;
    private BaseListViewAdapter mAdapter;
    private BaseActivity mActivity;
    private WeakReference<? extends BaseActivity> wr;
    private ListView lv;

    public static EvaluationFragment newInstance(int position, WeakReference<? extends BaseActivity> wr) {
        EvaluationFragment fragment = new EvaluationFragment();
        fragment.position = position;
        fragment.wr = wr;
        fragment.mActivity = wr.get();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.trainnet_await_evaluate, null);
        lv = (ListView) v.findViewById(R.id.listview_awaitevaluate);
        if (null == mActivity) {
            return v;
        }
        String EvaluateRefresh = TrainNetApp.getCache().getAsString(CacheKey.EvaluateRefresh);
        if (!StringUtil.isNullOrEmpty(EvaluateRefresh)) {
            evaluationList = TrainNetApp.getGson().fromJson(EvaluateRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
        } else {
            evaluationList = new ArrayList<>();
        }
        setListView();
        return v;
    }

    private void setListView() {
        clear();
        switch (position) {
//            case 0:
//                listItemOnLine.addAll(getListItem("OnlineCourse"));
//                setNoListItem(listItemOnLine, R.string.no_onLineEvaluating);
//                mAdapter = new BaseListViewAdapter(mActivity, listItemOnLine);
//                break;
            case 0:
                listItemOffLine.addAll(getListItem("OfflineCourse"));
                setNoListItem(listItemOffLine, R.string.no_offLineEvaluating);
                mAdapter = new BaseListViewAdapter(mActivity, listItemOffLine);
                break;
            case 1:
                listItemExtra.addAll(getListItem("CounselingCourse"));
                setNoListItem(listItemExtra, R.string.no_extraEvaluating);
                mAdapter = new BaseListViewAdapter(mActivity, listItemExtra);
                break;
        }
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.GRAY));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                switch (position) {
//                    case 0:
//                        setEvent(listItemOnLine, arg2);
//                        break;
                    case 0:
                        setEvent(listItemOffLine, arg2);
                        break;
                    case 1:
                        setEvent(listItemExtra, arg2);
                        break;
                }

            }
        });
    }

    private void setEvent(ArrayList<HashMap<String, Object>> listItem, int pos) {
        if (null != listItem.get(pos).get("infoDesc")) {
            String activityId = listItem.get(pos).get("activityId").toString();
            String jsonData = MySharedPreferences.getInstance().getString(activityId);
            WebserviceUtil.getInstance().submitEvaluation(wr, activityId, jsonData);
            ToastUtil.show(mActivity, mActivity.getResources().getString(R.string.submitEvaluation));
            listItem.remove(pos);
            mAdapter.notifyDataSetChanged();
            return;
        }
        if (null != listItem.get(pos).get("activityId")) {
            Bundle bundle = new Bundle();
            bundle.putString("item", listItem.get(pos).get("activityId").toString());
            bundle.putString("type", listItem.get(pos).get("type").toString());
            bundle.putString("info1", listItem.get(pos).get("info1").toString());
            bundle.putString("info2", listItem.get(pos).get("info2").toString());
            bundle.putString("info3", listItem.get(pos).get("info3").toString());
            bundle.putString("info4", listItem.get(pos).get("info4").toString());
            mActivity.readyGo(EvaluationPaperActivity.class, bundle);
            return;
        }
    }

    private ArrayList<HashMap<String, Object>> getListItem(String evaluationType) {
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        String evaluationID = MySharedPreferences.getInstance().getString(getResources().getString(R.string.evaluationID));
        HashMap<String, Object> mapResult = null;
        for (HashMap<String, Object> map : evaluationList) {
            if ("EvaluationListResult".equals(map.get("ParentNode").toString())) {
                continue;
            }
            if ("ApiEvaluation".equals(map.get("ParentNode").toString())) {
                if (!evaluationType.equals(map.get("EvaluationType").toString())) {
                    mapResult = null;
                    continue;
                }
                mapResult = new HashMap<>();
                mapResult.put("type", evaluationType);
                mapResult.put("activityId", map.get("ActivityId").toString());
                mapResult.put("noRight", "");
                if (null != mapResult && evaluationID.contains(map.get("ActivityId").toString() + ",")) {
                    mapResult.put("infoDesc", mActivity.getResources().getString(R.string.evaluationSubmit));
                }
                listItem.add(mapResult);
            }
            if (null != mapResult && "TrainPlan".equals(map.get("ParentNode").toString())) {
                mapResult.put("info2", "培训日期：" + map.get("StartDate").toString().replace("T", " ").substring(0, 16) + "至" + map.get("EndDate").toString().replace("T", " ").substring(0, 16));
                mapResult.put("info4", "培训老师：" + map.get("Teacher").toString());
            }
            if (null != mapResult && "Course".equals(map.get("ParentNode").toString())) {
                mapResult.put("info1", "培训课程：" + map.get("CourseName").toString());
            }
            if (null != mapResult && "Place".equals(map.get("ParentNode").toString())) {
                mapResult.put("info3", "培训地点：" + map.get("Name").toString());
            }
        }
        return listItem;
    }

    private void clear() {
//        if (null == listItemOnLine) {
//            listItemOnLine = new ArrayList<>();
//        } else {
//            listItemOnLine.clear();
//        }

        if (null == listItemOffLine) {
            listItemOffLine = new ArrayList<>();
        } else {
            listItemOffLine.clear();
        }

        if (null == listItemExtra) {
            listItemExtra = new ArrayList<>();
        } else {
            listItemExtra.clear();
        }
    }

    private void setNoListItem(ArrayList<HashMap<String, Object>> listItem, int resId) {
        if (listItem.size() <= 0) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("img_noData", R.drawable.nettest_no_exam);
            map.put("title_noData", mActivity.getResources().getString(resId));
            map.put("noLine", "");
            map.put("noRight", "");
            listItem.add(map);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
