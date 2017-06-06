package com.zhongdasoft.svwtrainnet.module.traininner;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.ChineseToEnglish;
import com.zhongdasoft.svwtrainnet.util.CollectionUtil;
import com.zhongdasoft.svwtrainnet.util.DialogUtil;
import com.zhongdasoft.svwtrainnet.util.PhoneInfo;
import com.zhongdasoft.svwtrainnet.util.Scale;
import com.zhongdasoft.svwtrainnet.util.Screen;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class InnerPlanScoreActivity extends BaseActivity {
    private WeakReference<? extends BaseActivity> wr;
    private String planId;
    private int approveNumber;
    private String planStatus;
    private ArrayList<HashMap<String, Object>> traineeList;
    private ArrayList<HashMap<String, Object>> listItem;
    private LinearLayout ll_parent;
    private RelativeLayout rl_parent;
    private RelativeLayout rl_child;
    private TextView tv_title;
    private TextView tv_grade;
    private TextView innerPlanTraineeOk;
    private View include;
    private HashMap<String, Object> resultMap;
    //    private TextView tv_Grade;
//    private TextView tv_CertificateNo;
//    private TextView tv_Attendance;
//    private TextView tv_Assessment;
    private int[] ids = {R.id.innerPlanTraineeScore, R.id.innerPlanTraineeCertificate, R.id.innerPlanTraineeAttend, R.id.innerPlanTraineeRating};
    private int[] texts = {R.string.TraineeScore, R.string.TraineeCertificate, R.string.TraineeAttend, R.string.TraineeRating};
    private EditText input;
    private int scoreWidth;
    private boolean isReadOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        ll_parent = (LinearLayout) findViewById(R.id.ll_parent);
        planId = getIntent().getStringExtra("item");
        approveNumber = getIntent().getIntExtra("approveNumber", 0);
        planStatus = getIntent().getStringExtra("planStatus");
        if ("2".equals(planStatus) || "3".equals(planStatus) || "4".equals(planStatus)) {
            isReadOnly = true;
        }
        Screen screen = Scale.getScreen(wr);
        int screenWidth = screen.getDipWidth();
        scoreWidth = Scale.Dp2Px(this, screenWidth - 120);

        Waiting.show(this, getResources().getString(R.string.Loading));
        if (approveNumber > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    String InnerPlanScoreRefresh = getCache().getAsString(CacheKey.InnerPlanScoreRefresh + planId);
//                    if (!StringUtil.isNullOrEmpty(InnerPlanScoreRefresh)) {
//                        listItem = getGson().fromJson(InnerPlanScoreRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
//                        }.getType());
//                    } else {
                    traineeList = TrainNetWebService.getInstance().InternalQueryTrainees(InnerPlanScoreActivity.this, planId);
                    setData();
                    getCache().put(CacheKey.InnerPlanScoreRefresh + planId, getGson().toJson(listItem));
//                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setLayout();
                            Waiting.dismiss();
                        }
                    });
                }
            }).start();
        } else {
            ImageView img = new ImageView(this);
            img.setImageResource(R.drawable.trainnet_no_img);
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ll_parent.addView(img);
            Waiting.dismiss();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_innerplan_score;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_innerplan_score;
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
                if (!getStatus(map.get("Status").toString())) {
                    hashMap = null;
                    continue;
                }
                hashMap = new HashMap<>();
                hashMap.put("Id", map.get("Id").toString());
                hashMap.put("Idcard", map.get("Idcard").toString());
                hashMap.put("StaffId", map.get("StaffId").toString());
                String Grade = StringUtil.objectToStr(map.get("Grade"));
                hashMap.put("Grade", "0".equals(Grade) ? "" : Grade);
                hashMap.put("Attendance", StringUtil.objectToStr(map.get("Attendance")));
                hashMap.put("Assessment", StringUtil.objectToStr(map.get("Assessment")));
                hashMap.put("CertificateNo", StringUtil.objectToStr(map.get("CertificateNo")));
                title = map.get("Name").toString();
                hashMap.put("title", title);
                hashMap.put("pinYin", ChineseToEnglish.getPingYin(title));
                listItem.add(hashMap);
            } else {
                if (null == hashMap) {
                    continue;
                }
                if (title.equals(hashMap.get("title").toString())) {
                    hashMap.put("title", getFixLength(title) + map.get("Name").toString());
                } else {
                    hashMap.put("title", hashMap.get("title").toString() + "," + map.get("Name").toString());
                }
            }
        }
        CollectionUtil.sortString(listItem, "pinYin", CollectionUtil.OrderAsc, "Grade", CollectionUtil.OrderDesc);
    }

    private void setLayout() {
        View view;
        HashMap<String, Object> map;
        for (int i = 0; i < listItem.size(); i++) {
            map = listItem.get(i);
            view = LayoutInflater.from(this).inflate(
                    R.layout.trainnet_item_expand, null);
            setDefaultValue(view, map.get("title").toString(), i);
            int[] includes = {R.id.include_traineeScore, R.id.include_traineeCertificate, R.id.include_traineeAttend, R.id.include_traineeRating};
            for (int j = 0; j < includes.length; j++) {
                include = view.findViewById(includes[j]);
                TextView tv = (TextView) include.findViewById(R.id.innerPlanLineDesc);
                tv.setText(texts[j]);
            }
            ll_parent.addView(view);
        }
    }

    private void setDefaultValue(final View view, String title, final int pos) {
        rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
        rl_child = (RelativeLayout) view.findViewById(R.id.rl_child);
        rl_child.setVisibility(View.GONE);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.getLayoutParams().width = scoreWidth;
        tv_title.setText(title);
        tv_grade = (TextView) view.findViewById(R.id.tv_grade);
        String[] keys = {"Grade", "CertificateNo", "Attendance", "Assessment"};
        for (int i = 0; i < ids.length; i++) {
            input = (EditText) view.findViewById(ids[i]);
            input.setEnabled(!isReadOnly);
            input.setText(listItem.get(pos).get(keys[i]).toString());
            if (i == 0) {
                if (!StringUtil.isNullOrEmpty(listItem.get(pos).get(keys[i]).toString())) {
                    tv_grade.setVisibility(View.VISIBLE);
                    tv_grade.setText(listItem.get(pos).get(keys[i]).toString() + "分");
                } else {
                    tv_grade.setVisibility(View.GONE);
                }
                if (pos == 0) {
                    input.requestFocus();
                }
            }
            if (i == keys.length - 1) {
                input.setTag(pos);
                input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT) {
                            setFinished(v);
                        }
                        return false;
                    }
                });
            }
        }
        innerPlanTraineeOk = (TextView) view.findViewById(R.id.innerPlanTraineeOk);
        innerPlanTraineeOk.setEnabled(!isReadOnly);
        innerPlanTraineeOk.setTag(pos);
        innerPlanTraineeOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setFinished(v);
            }
        });
        rl_parent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_drop = (TextView) ((RelativeLayout) v).getChildAt(2);
                rl_child = (RelativeLayout) ((RelativeLayout) v.getParent()).getChildAt(1);
                if (rl_child.getVisibility() == View.VISIBLE) {
                    rl_child.setVisibility(View.GONE);
                    tv_drop.setText(getResources().getString(R.string.dropDown));
                    TextView tv_OK = (TextView) rl_child.getChildAt(rl_child.getChildCount() - 1);
                    int pos = Integer.parseInt(tv_OK.getTag().toString());
                    setNextFocus(pos);
                } else {
                    closeAll();
                    rl_child.setVisibility(View.VISIBLE);
                    tv_drop.setText(getResources().getString(R.string.dropUp));
                    EditText tv_Grade = (EditText) rl_child.getChildAt(1);
                    tv_Grade.requestFocus();
                }
            }
        });
    }

    private void closeAll() {
        RelativeLayout root;
        RelativeLayout rl_child;
        TextView tv_drop;
        for (int i = 0; i < ll_parent.getChildCount(); i++) {
            root = (RelativeLayout) ll_parent.getChildAt(i);
            rl_child = (RelativeLayout) root.getChildAt(1);
            rl_child.setVisibility(View.GONE);
            tv_drop = (TextView) ((RelativeLayout) root.getChildAt(0)).getChildAt(2);
            tv_drop.setText(getResources().getString(R.string.dropDown));
        }
    }

    private void setFinished(View v) {
        RelativeLayout rl_parent = (RelativeLayout) v.getParent();
        int pos = Integer.parseInt(v.getTag().toString());
        String[] values = new String[4];
        for (int i = 0; i < ids.length; i++) {
            input = (EditText) rl_parent.findViewById(ids[i]);
            values[i] = input.getText().toString();
        }
        if (checkInput(values)) {
            if (StringUtil.isNullOrEmpty(values[0])) {
                values[0] = "0";
            }
            String trainee = PhoneInfo.getInstance().getTraineeXML(listItem.get(pos).get("Id").toString(), values[0], values[1], values[2], values[3]);
            RelativeLayout root = (RelativeLayout) rl_parent.getParent();
            runThread(trainee, values[0], root, pos);
        }
    }

    private void setNextFocus(int pos) {
        if (pos < ll_parent.getChildCount() - 1) {
            RelativeLayout nextRoot = (RelativeLayout) ll_parent.getChildAt(pos + 1);
            rl_child = (RelativeLayout) nextRoot.getChildAt(1);
            EditText tv_Grade = (EditText) rl_child.getChildAt(1);
            tv_Grade.requestFocus();
        }
    }

    private void runThread(final String trainee, final String score, final RelativeLayout root, final int pos) {
        Waiting.show(this, getResources().getString(R.string.LoadingUpdateScore));
        new Thread(new Runnable() {
            @Override
            public void run() {
                resultMap = TrainNetWebService.getInstance().InternalUpdateTrainee(InnerPlanScoreActivity.this, trainee);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Waiting.dismiss();
                        String returnCode = resultMap.get(getResources().getString(R.string.ReturnCode)).toString();
                        if ("0".equals(returnCode)) {
//                            getCache().remove(CacheKey.InnerPlanScoreRefresh + planId);
                        }
                        String message = resultMap.get(getResources().getString(R.string.Message)).toString();
                        DialogUtil.getInstance().showDialog(wr, getResources().getString(R.string.tips), message, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog alertDialog = (AlertDialog) v.getTag();
                                alertDialog.dismiss();
                                RelativeLayout rl_parent = (RelativeLayout) root.getChildAt(0);
                                RelativeLayout rl_child = (RelativeLayout) root.getChildAt(1);
                                TextView tv_grade = (TextView) rl_parent.getChildAt(1);
                                TextView tv_drop = (TextView) rl_parent.getChildAt(2);
                                rl_child.setVisibility(View.GONE);
                                tv_grade.setVisibility(View.VISIBLE);
                                tv_grade.setText(score + "分");
                                tv_drop.setText(getResources().getString(R.string.dropDown));
                                setNextFocus(pos);
                            }
                        }, null);
                    }
                });
            }
        }).start();
    }

    private boolean checkInput(String[] s) {
//        for (int i = 0; i < s.length; i++) {
//            if (StringUtil.isNullOrEmpty(s[i])) {
//                ToastUtil.show(this, String.format("%s未输入，请检查", getResources().getString(texts[i])));
//                return false;
//            }
//        }
//        int score = Integer.parseInt(s[0]);
//        if (score < 0 || score > 100) {
//            ToastUtil.show(this, "成绩输入错误，请检查");
//            return false;
//        }
        return true;
    }

    private boolean getStatus(String status) {
        return "Registered".equals(status);
    }

    private String getFixLength(String title) {
        StringBuilder sbStr = new StringBuilder(title);
        for (int i = 0; i < 4 - title.length(); i++) {
            sbStr.append("    ");
        }
        sbStr.append("    ");
        return sbStr.toString();
    }

}
