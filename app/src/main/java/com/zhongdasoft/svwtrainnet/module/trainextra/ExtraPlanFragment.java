package com.zhongdasoft.svwtrainnet.module.trainextra;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.CollectionUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ZhangJianFeng on 2016-09-14.
 */

public class ExtraPlanFragment extends Fragment {
    //private ArrayList<HashMap<String, Object>> evaluationList;
    private int position;
    private ArrayList<HashMap<String, Object>> listItemCounselingNotFinishedPlans;
    private ArrayList<HashMap<String, Object>> listItemCounselingFinishedPlans;
    private ArrayList<HashMap<String, Object>> evaluation1List;
    private ArrayList<HashMap<String, Object>> evaluation2List;
    private BaseListViewAdapter mAdapter;
    private BaseActivity mActivity;
    private WeakReference<? extends BaseActivity> wr;
    private ListView lv;

    public static ExtraPlanFragment newInstance(int position, WeakReference<? extends BaseActivity> wr) {
        ExtraPlanFragment fragment = new ExtraPlanFragment();
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
        runThread();
        return v;
    }

    private void runThread() {
        Waiting.show(getActivity(), getActivity().getResources().getString(R.string.LoadingExtraPlan));

        new Thread(new Runnable() {
            @Override
            public void run() {
//                    if (null == evaluation1List) {
                evaluation1List = null;
//                    }
//                    if (null == evaluation2List) {
                evaluation2List = null;
//                    }

                evaluation1List = TrainNetWebService.getInstance().CounselingNotFinishedPlans(mActivity);
                evaluation2List = TrainNetWebService.getInstance().CounselingFinishedPlans(mActivity);

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setListView();
                        Waiting.dismiss();
                    }
                });
            }
        }).start();

    }

    private void setListView() {
        clear();
        if (0 == position) {
            listItemCounselingNotFinishedPlans.addAll(getListItem("CounselingNotFinishedPlans"));
            setNoListItem(listItemCounselingNotFinishedPlans, R.string.no_notFinishedExtraPlan);
            mAdapter = new BaseListViewAdapter(mActivity, listItemCounselingNotFinishedPlans);
            CollectionUtil.sortString(listItemCounselingNotFinishedPlans, "info1", CollectionUtil.OrderAsc);
        } else {
            listItemCounselingFinishedPlans.addAll(getListItem("CounselingFinishedPlans"));
            setNoListItem(listItemCounselingFinishedPlans, R.string.no_finishedExtraPlan);
            mAdapter = new BaseListViewAdapter(mActivity, listItemCounselingFinishedPlans);
            CollectionUtil.sortString(listItemCounselingFinishedPlans, "info1", CollectionUtil.OrderDesc);
        }
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.GRAY));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                switch (position) {
                    case 0:
                        setEvent(listItemCounselingNotFinishedPlans, arg2);
                        break;
                    case 1:
                        setEvent(listItemCounselingFinishedPlans, arg2);
                        break;
                    /*case 2:
                        setEvent(listItemExtra, arg2);
                        break;*/
                }

            }
        });
    }

    private void setEvent(ArrayList<HashMap<String, Object>> listItem, int pos) {

        if (null != listItem.get(pos).get("PlanId")) {
            Bundle bundle = new Bundle();
            bundle.putString("PlanId", null != listItem.get(pos).get("PlanId") ? listItem.get(pos).get("PlanId").toString() : "");
            bundle.putString("CourseName", null != listItem.get(pos).get("CourseName") ? listItem.get(pos).get("CourseName").toString() : "");
            bundle.putString("StartDate", null != listItem.get(pos).get("StartDate") ? listItem.get(pos).get("StartDate").toString() : "");
            bundle.putString("EndDate", null != listItem.get(pos).get("EndDate") ? listItem.get(pos).get("EndDate").toString() : "");
            bundle.putString("PlaceName", null != listItem.get(pos).get("PlaceName") ? listItem.get(pos).get("PlaceName").toString() : "");
            bundle.putString("Teachers", null != listItem.get(pos).get("Teachers") ? listItem.get(pos).get("Teachers").toString() : "");
            bundle.putString("Content", null != listItem.get(pos).get("Content") ? listItem.get(pos).get("Content").toString() : "");
            bundle.putString("RequiredWorkTypes", null != listItem.get(pos).get("RequiredWorkTypes") ? listItem.get(pos).get("RequiredWorkTypes").toString() : "");
            bundle.putString("OptionalWorkTypes", null != listItem.get(pos).get("OptionalWorkTypes") ? listItem.get(pos).get("OptionalWorkTypes").toString() : "");
            mActivity.readyGo(ExtraPlanCounselingActivity.class, bundle);
            return;
        } else {
        }

    }

    private ArrayList<HashMap<String, Object>> getListItem(String CounselingType) {
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        ArrayList<HashMap<String, Object>> evaluationList;
        String teacher = "";
        String worktype = "";
        if (CounselingType.equals("CounselingNotFinishedPlans")) {
            evaluationList = evaluation1List;
        } else {
            evaluationList = evaluation2List;
        }
        HashMap<String, Object> mapResult = new HashMap<>();

        for (HashMap<String, Object> map : evaluationList) {
            if ((CounselingType + "Result").equals(map.get("ParentNode").toString())) {
                if ("0".equals(map.get("ReturnCode").toString().trim())) {
                    continue;
                } else {
                    break;
                }
            }

            if ("ApiCounselingPlan".equals(map.get("ParentNode").toString())) {
                if (null != mapResult) {
                    if (mapResult.size() > 0) {
                        mapResult.put("info", (null != mapResult.get("CourseName") ? mapResult.get("CourseName").toString() : ""));
                        mapResult.put("info1", "日期：" + (null != mapResult.get("StartDate") ? mapResult.get("StartDate").toString() : "").replace("T", " ").substring(0, 16) + (null != mapResult.get("EndDate") ? " 至 " + mapResult.get("EndDate").toString().replace("T", " ").substring(0, 16) : ""));
                        mapResult.put("info2", (null != mapResult.get("CounselingType") ? ("Once".equals(mapResult.get("CounselingType").toString()) ? " 单次执行 - " : "巡回带教 第" + (null != mapResult.get("Term") ? mapResult.get("Term").toString() : "") + "轮,共" + (null != mapResult.get("Rounds") ? mapResult.get("Rounds").toString() : "") + "轮") : ""));
                        mapResult.put("info3", "辅导老师：" + (null != mapResult.get("Teachers") ? mapResult.get("Teachers").toString() : ""));
                        mapResult.put("noRight", "");
                        listItem.add(mapResult);
                    }

                    mapResult = null;
                }

                teacher = "Teachers";
                mapResult = new HashMap<>();
                mapResult.put("StartDate", map.get("StartDate") == null ? "" : map.get("StartDate").toString());
                mapResult.put("EndDate", map.get("EndDate") == null ? "" : map.get("EndDate").toString());
                mapResult.put("PlanId", map.get("PlanId") == null ? "" : map.get("PlanId").toString());
                mapResult.put("Term", map.get("Term") == null ? "" : map.get("Term").toString());
                continue;
            }

            if ("ApiTeacher".equals(map.get("ParentNode").toString())) {
                switch (teacher) {
                    case "Teachers":
                        mapResult.put("Teachers", null == mapResult.get("Teachers") ? (map.get("Name") == null ? "" : map.get("Name").toString()) : mapResult.get("Teachers") + "," + (map.get("Name") == null ? "" : map.get("Name").toString()));
                        break;
                    case "Directors":
                        break;
                    case "Teachers2":
                        break;
                    default:
                        break;
                }

                continue;
            }
            if ("Directors".equals(map.get("ParentNode").toString())) {
                teacher = "Directors";
            }

            if ("Course".equals(map.get("ParentNode").toString())) {
                teacher = "Teachers2";
                //获取课程信息
                mapResult.put("CourseName", (map.get("CourseName") == null ? "" : map.get("CourseName").toString()));
                mapResult.put("Rounds", (map.get("Rounds") == null ? "" : map.get("Rounds").toString()));
                mapResult.put("Content", (map.get("Content") == null ? "" : map.get("Content").toString()));
                mapResult.put("CounselingType", (map.get("CounselingType") == null ? "" : map.get("CounselingType").toString()));
                continue;
            }

            if ("Place".equals(map.get("ParentNode").toString())) {
                mapResult.put("PlaceName", (map.get("Name") == null ? "" : map.get("Name").toString()));
                continue;
            }

            if ("RequiredWorkTypes".equals(map.get("ParentNode").toString())) {
                worktype = "RequiredWorkTypes";
                continue;
            }

            if ("OptionalWorkTypes".equals(map.get("ParentNode").toString())) {
                worktype = "OptionalWorkTypes";
                continue;
            }

            if ("ApiWorktype".equals(map.get("ParentNode").toString())) {
                switch (worktype) {
                    case "OptionalWorkTypes":
                        mapResult.put("OptionalWorkTypes", null == mapResult.get("OptionalWorkTypes") ? (map.get("Name") == null ? "" : map.get("Name").toString()) : mapResult.get("OptionalWorkTypes") + "," + (map.get("Name") == null ? "" : map.get("Name").toString()));
                        break;
                    case "RequiredWorkTypes":
                        mapResult.put("RequiredWorkTypes", null == mapResult.get("RequiredWorkTypes") ? (map.get("Name") == null ? "" : map.get("Name").toString()) : mapResult.get("RequiredWorkTypes") + "," + (map.get("Name") == null ? "" : map.get("Name").toString()));
                        break;
                    default:
                        break;
                }

                continue;
            }
        }

        if (null != mapResult) {
            if (mapResult.size() > 0) {
                mapResult.put("info", (null != mapResult.get("CourseName") ? mapResult.get("CourseName").toString() : ""));
                mapResult.put("info1", "日期：" + (null != mapResult.get("StartDate") ? mapResult.get("StartDate").toString().replace("T", " ").substring(0, 16) + " 至 " : "") + (null != mapResult.get("EndDate") ? mapResult.get("EndDate").toString().replace("T", " ").substring(0, 16) : ""));
                mapResult.put("info2", (null != mapResult.get("CounselingType") ? ("Once".equals(mapResult.get("CounselingType").toString()) ? " 单次执行 - " : "巡回带教 第" + (null != mapResult.get("Term") ? mapResult.get("Term").toString() : "") + "轮,共" + (null != mapResult.get("Rounds") ? mapResult.get("Rounds").toString() : "") + "轮") : ""));
                mapResult.put("info3", "辅导老师：" + (null != mapResult.get("Teachers") ? mapResult.get("Teachers").toString() : ""));
                mapResult.put("noRight", "");
                listItem.add(mapResult);
            }
        }
       /* String evaluationID = MySharedPreferences.getString(getResources().getString(R.string.evaluationID), mActivity);
        for (HashMap<String, Object> map : evaluationList) {
            if (null == map.get("ParentNode")) {
                continue;
            }
            if (!"ApiEvaluation".equals(map.get("ParentNode").toString())) {
                continue;
            }
            if (!evaluationType.equals(map.get("EvaluationType").toString())) {
                continue;
            }
           // HashMap<String, Object> mapResult = new HashMap<>();
            mapResult.put("info", map.get("Name").toString());
            mapResult.put("info1", "类别：" + getChsName(map.get("EvaluationType").toString()));
            if (evaluationID.contains(map.get("Name").toString() + ",")) {
                mapResult.put("infoDesc", mActivity.getResources().getString(R.string.evaluationSubmit));
            }
            mapResult.put("activityId", map.get("Name").toString());//hashMap.get("activityId"));
            mapResult.put("noRight", "");
            listItem.add(mapResult);
        }*/

        return listItem;
    }

//    private  ArrayList<HashMap<String, Object>> getListRecursion(SoapObject object){
//        ArrayList<HashMap<String, Object>> listItem=new ArrayList<>();
//        SoapObject child;
//        Object obj;
//        PropertyInfo pi = new PropertyInfo();
//        boolean hasChild;
////        for (int i = 0; i < object.getPropertyCount(); ++i) {
////            obj = object.getProperty(i);
////            if (context.getResources().getString(R.string.SoapObjectTypeName).equals(obj.getClass()
////                    .getName())) {
////                child = (SoapObject) obj;
////                hasChild = false;
////                for (int j = 0; j < child.getPropertyCount(); ++j) {
////                    child.getPropertyInfo(j, pi);
////                    if (!StringUtil.isNullOrEmpty(recurFieldName)) {
////                        if (recurFieldName.equals(pi.getName())) {
////                            parentId = listResult.get(listResult.size() - 1).get(idFieldName).toString();
////                        }
////                    }
////                    if (!context.getResources().getString(R.string.SoapObjectTypeName).equals(pi
////                            .getValue().getClass().getName())) {
////                        hasChild = true;
////                        listResult.get(listResult.size() - 1).put(pi.getName(),
////                                pi.getValue().toString());
////                    }
////                }
////                if (!StringUtil.isNullOrEmpty(recurFieldName)) {
////                    listResult.get(listResult.size() - 1).put("parentId", parentId);
////                }
////                if (hasChild) {
////                    object.getPropertyInfo(i, pi);
////                    listResult.get(listResult.size() - 1).put(context.getResources().getString(R.string.ParentNode), pi.getName());
////                    HashMap<String, Object> mapResult = new HashMap<>();
////                    listResult.add(mapResult);
////                }
////                if (child.getPropertyCount() > 0) {
////                    findProperty(context, child, listResult);
////                }
////            }
////        }
//        return listItem;
//    }


    private void clear() {
        if (null == listItemCounselingNotFinishedPlans) {
            listItemCounselingNotFinishedPlans = new ArrayList<>();
        } else {
            listItemCounselingNotFinishedPlans.clear();
        }

        if (null == listItemCounselingFinishedPlans) {
            listItemCounselingFinishedPlans = new ArrayList<>();
        } else {
            listItemCounselingFinishedPlans.clear();
        }

      /*  if (null == listItemExtra) {
            listItemExtra = new ArrayList<>();
        } else {
            listItemExtra.clear();
        }*/
    }

    private String getChsName(String evaluationType) {
        if ("CounselingNotFinishedPlans".equals(evaluationType)) {
            return "未完成";
        } else if ("CounselingFinishedPlans".equals(evaluationType)) {
            return "已完成";
        }
        /*else if ("CounselingCourse".equals(evaluationType)) {
            return "辅导课程";
        } else if ("Survey".equals(evaluationType)) {
            return "调研问卷";
        } */
        else {
            return "";
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
