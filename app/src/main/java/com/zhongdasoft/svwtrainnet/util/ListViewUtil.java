package com.zhongdasoft.svwtrainnet.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.soundcloud.android.crop.Crop;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;
import com.zhongdasoft.svwtrainnet.adapter.ProfileListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.module.home.LoginActivity;
import com.zhongdasoft.svwtrainnet.module.more.TvContentActivity;
import com.zhongdasoft.svwtrainnet.module.trainnormal.NormalPlanActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/7/5 15:02
 * 修改人：Administrator
 * 修改时间：2016/7/5 15:02
 * 修改备注：
 */
public class ListViewUtil {
    public final static int PaperTitle = 1;
    public final static int PaperSubTitle = 2;
    public final static int PaperInfo = 3;
    public final static int PaperAMap = 4;
    public final static int PaperEvent = 5;
    public final static int PaperEventDone = 6;
    public final static int PaperEventTwo = 7;
    public final static int PaperEventNoBack = 8;
    public final static int PaperImageEvent = 9;

    public final static int PaperRating = 100;
    public final static int PaperInput = 101;

    public static void setListItem(ArrayList<HashMap<String, Object>> listItem, String str, int type) {
        HashMap<String, Object> map = new HashMap<>();
        switch (type) {
            case PaperTitle:
                map.put("paperTitle", str);
                break;
            case PaperSubTitle:
                map.put("paperSubTitle", str);
                break;
            case PaperInfo:
                map.put("paperInfo", str);
                break;
            case PaperAMap:
                map.put("paperInfo", str);
                map.put("paperImg", R.drawable.map_navigation);
                map.put("paperAMap", "");
                break;
            case PaperEvent:
                map.put("paperEvent", str);
                break;
            case PaperEventDone:
                map.put("paperEventDone", str);
                break;
            case PaperEventTwo:
                map.put("paperEventTwo", str);
                break;
            case PaperEventNoBack:
                map.put("paperEventNoBack", str);
                map.put("paperInfo", str);
                map.put("right", "");
                break;
            case PaperImageEvent:
                String[] str_2 = str.split(",");
                map.put("paperImgInfo1", str_2[0]);
                map.put("paperImgInfo2", str_2[1]);
                map.put("paperImgEvent", str_2[2]);
                map.put("rightBig", "");
                break;
        }
        map.put("noRight", "");
        listItem.add(map);
    }

    public static void setDiyListItem(ArrayList<HashMap<String, Object>> listItem, int type, String... param) {
        HashMap<String, Object> map = new HashMap<>();
        switch (type) {
            case PaperTitle:
                map.put("paperTitle", param[0]);
                map.put("paperAlign", Gravity.CENTER);
                map.put("paperSize", "20");
                break;
            case PaperSubTitle:
                map.put("id", param[0]);
                map.put("paperTitle", param[1]);
                map.put("backColor", R.color.gray_white);
                map.put("paperSize", "16");
                break;
            case PaperRating:
                map.put("id", param[0]);
                map.put("paperTitle", param[1]);
                map.put("paperRating", "0");
                break;
            case PaperInput:
                map.put("id", param[0]);
                map.put("paperTitle", param[1]);
                map.put("paperInput", "");
                break;
            case PaperEvent:
                map.put("paperEvent", param[0]);
                break;
        }
        listItem.add(map);
    }

    public static void setProfileListView(final WeakReference<? extends BaseActivity> wr, boolean logout) {
        final BaseActivity activity = wr.get();
        final ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        ListView lv;
        HashMap<String, Object> profileMap;
        String ProfileRefresh = TrainNetApp.getCache().getAsString(CacheKey.ProfileRefresh);
        profileMap = TrainNetApp.getGson().fromJson(ProfileRefresh, new TypeToken<HashMap<String, Object>>() {
        }.getType());

        if (null == profileMap || 0 == profileMap.size()) {
            return;
        }
        String name = MySharedPreferences.getInstance().getName();
        String userName = MySharedPreferences.getInstance().getUserName();

        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.UserInfo), ListViewUtil.PaperSubTitle);
        String userAccount;
        if (userName.length() >= 10) {
            String postFix = userName.toUpperCase().substring(7, 10);
            userAccount = StringUtil.objectToStr1(profileMap.get("NetCode"), postFix + "/" + userName.toUpperCase());
        } else {
            userAccount = userName.toUpperCase();
        }

        String str = String.format("%s,%s,%s", userAccount, name, CameraUtil.getInstance().getCameraFilePath() + CameraUtil.getInstance().getCameraFileName());
        ListViewUtil.setListItem(listItem, str, ListViewUtil.PaperImageEvent);
        final String cellNumber = StringUtil.objectToStr(profileMap.get("CellNumber"));
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.CellNumber) + cellNumber, ListViewUtil.PaperEventNoBack);
        final String email = StringUtil.objectToStr(profileMap.get("Email"));
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.Email) + email, ListViewUtil.PaperEventNoBack);

        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.DealerInfo), ListViewUtil.PaperSubTitle);
        String DealerNo = StringUtil.objectToStr(profileMap.get("DealerNo"));
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr1(profileMap.get("NetCode"), DealerNo), ListViewUtil.PaperInfo);
        ListViewUtil.setListItem(listItem, StringUtil.objectToStr(profileMap.get("DealerName")), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.Idcard), ListViewUtil.PaperSubTitle);
        String Idcard = StringUtil.objectToStr(profileMap.get("Idcard"));
        ListViewUtil.setListItem(listItem, Idcard, ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.Post), ListViewUtil.PaperSubTitle);
        String post = StringUtil.objectToStr(profileMap.get("post"));
        ListViewUtil.setListItem(listItem, post, ListViewUtil.PaperInfo);
        String inWork = StringUtil.objectToBoolean(profileMap.get("Inwork"));
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.InWork) + inWork, ListViewUtil.PaperInfo);

        MySharedPreferences.getInstance().setStoreString("phone", cellNumber);
        MySharedPreferences.getInstance().setStoreString("email", email);
        MySharedPreferences.getInstance().setStoreString("dealerCode", StringUtil.objectToStr1(profileMap.get("NetCode"), DealerNo));
        MySharedPreferences.getInstance().setStoreString("dealerName", StringUtil.objectToStr(profileMap.get("DealerName")));

//        if (profileMap.containsKey("MembershipLevel")) {
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.SvwStar), ListViewUtil.PaperSubTitle);
        String MembershipLevel = StringUtil.objectToStr(profileMap.get("MembershipLevel"));
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.MembershipLevel) + MembershipLevel, ListViewUtil.PaperInfo);
        String Evaluation = StringUtil.objectToStr(profileMap.get("Evaluation"));
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.Evaluation) + Evaluation, ListViewUtil.PaperInfo);
        String CertifiedStatus = StringUtil.objectToStr(profileMap.get("CertifiedStatus"));
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.CertifiedStatus) + CertifiedStatus, ListViewUtil.PaperInfo);
        String Qualification = StringUtil.objectToStr(profileMap.get("Qualification"));
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.Qualification) + Qualification, ListViewUtil.PaperInfo);
        String IntegrationDate = StringUtil.objectToDecimal(profileMap.get("IntegrationDate"));
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.IntegrationDate) + MathUtil.getRound(IntegrationDate, MathUtil.RoundTwo), ListViewUtil.PaperInfo);
        String BeforeAssessment = StringUtil.objectToDecimal(profileMap.get("BeforeAssessment"));
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.BeforeAssessment) + MathUtil.getRound(BeforeAssessment, MathUtil.RoundTwo), ListViewUtil.PaperInfo);
        String AfterAssessment = StringUtil.objectToDecimal(profileMap.get("AfterAssessment"));
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.AfterAssessment) + MathUtil.getRound(AfterAssessment, MathUtil.RoundTwo), ListViewUtil.PaperInfo);
        String Tax = StringUtil.objectToDecimal(profileMap.get("Tax"));
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.Tax) + MathUtil.getRound(Tax, MathUtil.RoundTwo), ListViewUtil.PaperInfo);
        String ManagementFee = StringUtil.objectToDecimal(profileMap.get("ManagementFee"));
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.ManagementFee) + MathUtil.getRound(ManagementFee, MathUtil.RoundTwo), ListViewUtil.PaperInfo);
        String ActualPayment = StringUtil.objectToDecimal(profileMap.get("ActualPayment"));
        ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.ActualPayment) + MathUtil.getRound(ActualPayment, MathUtil.RoundTwo), ListViewUtil.PaperInfo);
        if (post.contains("总经理")) {
            ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.AllPeople), ListViewUtil.PaperEvent);
        }
//        }
        if (logout) {
            ListViewUtil.setListItem(listItem, activity.getResources().getString(R.string.Logout), ListViewUtil.PaperEvent);
        }

        final ProfileListViewAdapter mAdapter = new ProfileListViewAdapter(activity, listItem);
        lv = (ListView) activity.findViewById(R.id.moresetting).findViewById(R.id.listview_settings);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,
                                    long arg3) {
                if (listItem.get(arg2).containsKey("paperEvent")) {
                    if (listItem.get(arg2).get("paperEvent").toString().equals(activity.getResources().getString(R.string.AllPeople))) {
                        Bundle bundle = new Bundle();
                        String accessToken = MySharedPreferences.getInstance().getAccessToken();
                        bundle.putString("item", MyProperty.getCurrentValue(activity.getResources().getString(R.string.PaymentUrl, accessToken)));
                        bundle.putString("pay", activity.getResources().getString(R.string.AllPeople));
                        activity.readyGo(TvContentActivity.class, bundle);
                    }
                    if (listItem.get(arg2).get("paperEvent").toString().equals(activity.getResources().getString(R.string.Logout))) {
                        Bundle bundle = new Bundle();
                        bundle.putString("item", "1");
                        bundle.putString("reason", "已注销");
                        activity.readyGoThenKill(LoginActivity.class, bundle);
                    }
                }
                if (listItem.get(arg2).containsKey("paperEventNoBack")) {
                    if (listItem.get(arg2).get("paperEventNoBack").toString().startsWith(activity.getResources().getString(R.string.CellNumber))) {
                        DialogUtil.getInstance().showInputDialog(wr, "请输入手机号", cellNumber, 0, new DialogUtil.MyDismiss() {
                            @Override
                            public String handleDismiss(int which, String str) {
                                if (str.length() != 11) {
                                    ToastUtil.show(activity, "手机号应为11位，请检查！");
                                    return null;
                                }
                                WebserviceUtil.getInstance().profileCellNumber(activity, str);
                                str = activity.getResources().getString(R.string.CellNumber) + str;
                                listItem.get(arg2).put("paperEventNoBack", str);
                                listItem.get(arg2).put("paperInfo", str);
                                listItem.get(arg2).put("right", "");
                                mAdapter.notifyDataSetChanged();
                                return null;
                            }
                        });
                    }
                    if (listItem.get(arg2).get("paperEventNoBack").toString().startsWith(activity.getResources().getString(R.string.Email))) {
                        DialogUtil.getInstance().showInputDialog(wr, "请输入邮箱", email, 0, new DialogUtil.MyDismiss() {
                            @Override
                            public String handleDismiss(int which, String str) {
                                WebserviceUtil.getInstance().profileEmail(activity, str);
                                str = activity.getResources().getString(R.string.Email) + str;
                                listItem.get(arg2).put("paperEventNoBack", str);
                                listItem.get(arg2).put("paperInfo", str);
                                listItem.get(arg2).put("right", "");
                                mAdapter.notifyDataSetChanged();
                                return null;
                            }
                        });
                    }
                }
                if (listItem.get(arg2).containsKey("paperImgEvent")) {
                    //弹出对话选择框
                    final String[] items = {"拍照", "手机相册"};//, "上传文件"};
                    DialogUtil.getInstance().showListDialog(wr, "请选择", items, new DialogUtil.MyDismiss() {
                        @Override
                        public String handleDismiss(int which, String str) {
                            switch (which) {
                                case 0:
                                    CameraUtil.getInstance().openCamera(activity);
                                    break;
                                case 1:
                                    Crop.pickImage(activity);
                                    break;
                            }
                            return null;
                        }
                    });
                }
            }
        });
    }

    public static void setCallBackListView(final BaseActivity activity) {
        final ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        ArrayList<HashMap<String, Object>> processList;
        ListView lv;
        String ConfirmRefresh = TrainNetApp.getCache().getAsString(CacheKey.ConfirmRefresh);
        if (!StringUtil.isNullOrEmptyOrEmptySet(ConfirmRefresh)) {
            processList = TrainNetApp.getGson().fromJson(ConfirmRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
        } else {
            processList = new ArrayList<>();
        }
        CollectionUtil.sortString(processList, "StartDate", CollectionUtil.OrderDesc);
        for (HashMap<String, Object> hashMap : processList) {
            if (!"ApiApply".equals(hashMap.get(activity.getResources().getString(R.string.ParentNode)))) {
                continue;
            }
            for (HashMap<String, Object> tmpMap : processList) {
                if (!"Course".equals(tmpMap.get(activity.getResources().getString(R.string.ParentNode)))) {
                    continue;
                }
                if (hashMap.get("ApplyId").equals(tmpMap.get("parentId"))) {
                    hashMap.put("CourseName", tmpMap.get("CourseName"));
                    break;
                }
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("info", hashMap.get("CourseName"));
            map.put("infoDesc", "详情");
            map.put("info1", "培训时间：" + hashMap.get("StartDate").toString().replace("T", " ").substring(0, 16) + " 至 " + hashMap.get("EndDate").toString().replace("T", " ").substring(0, 16));
            map.put("noRight", "");
            map.put("planIdAndApplyId", hashMap.get("PlanId").toString() + ",," + hashMap.get("ApplyId").toString());
            map.put("confirmed", hashMap.get("Confirmed").toString());
            listItem.add(map);
        }
        if (listItem.size() <= 0) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("img_noData", R.drawable.trainnet_no_img);
            map.put("noLine", "");
            map.put("noRight", "");
            listItem.add(map);
        }
        BaseListViewAdapter mAdapter = new BaseListViewAdapter(activity, listItem);
        lv = (ListView) activity.findViewById(R.id.listview_callback);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.GRAY));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (listItem.get(arg2).containsKey("infoDesc")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("item", listItem.get(arg2).get("planIdAndApplyId").toString());
                    bundle.putString("confirmed", listItem.get(arg2).get("confirmed").toString());
                    activity.readyGo(NormalPlanActivity.class, bundle);
                }
            }
        });
    }
}
