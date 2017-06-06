package com.zhongdasoft.svwtrainnet.module.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.ListViewUtil;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.util.WebserviceUtil;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class EvaluationPaperActivity extends BaseActivity {
    private ArrayList<HashMap<String, Object>> listItem;
    private WeakReference<? extends BaseActivity> wr;
    private EditText etChild;
    private String activityId;
    private String info1;
    private String info2;
    private String info3;
    private String info4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        info1 = getIntent().getStringExtra("info1");
        info2 = getIntent().getStringExtra("info2");
        info3 = getIntent().getStringExtra("info3");
        info4 = getIntent().getStringExtra("info4");
        activityId = getIntent().getStringExtra("item");
        Waiting.show(this, getResources().getString(R.string.Loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<HashMap<String, Object>> evaluationPaperList = TrainNetWebService.getInstance().EvaluationPaper(EvaluationPaperActivity.this, activityId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setData(evaluationPaperList);
                        Waiting.dismiss();
                    }
                });
            }
        }).start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_evaluation_paper;
    }

    @Override
    protected int getMTitle() {
        String type = getIntent().getStringExtra("type");
        if ("OfflineCourse".equals(type)) {
            return R.string.title_evaluationpaper;
        } else {
            return R.string.title_evaluationextrapaper;
        }
    }

    private void setData(ArrayList<HashMap<String, Object>> evaluationPaperList) {
        if (null == listItem) {
            listItem = new ArrayList<>();
        } else {
            listItem.clear();
        }
        int iCount = 0;
        int index = -1;
        for (HashMap<String, Object> hashMap : evaluationPaperList) {
            if ("Result".equals(hashMap.get(getResources().getString(R.string.ParentNode)))) {
                ListViewUtil.setDiyListItem(listItem, ListViewUtil.PaperSubTitle, "", String.format("%s\n%s\n%s\n%s", info1, info2, info3, info4));
            } else if ("ApiEvaluationSection".equals(hashMap.get(getResources().getString(R.string.ParentNode)))) {
                iCount = 0;
                index = -1;
                if ("Rating".equals(hashMap.get("Type").toString())) {
                    ListViewUtil.setDiyListItem(listItem, ListViewUtil.PaperSubTitle, hashMap.get("Id").toString(), StringUtil.objectToStr(hashMap.get("Name")));
                    index = listItem.size() - 1;
                } else if ("Essay".equals(hashMap.get("Type").toString())) {
                    ListViewUtil.setDiyListItem(listItem, ListViewUtil.PaperSubTitle, "", StringUtil.objectToStr(hashMap.get("Name")));
                    ListViewUtil.setDiyListItem(listItem, ListViewUtil.PaperInput, hashMap.get("Id").toString(), "");
                }
            } else if ("ApiEvaluationRatingItem".equals(hashMap.get(getResources().getString(R.string.ParentNode)))) {
                ListViewUtil.setDiyListItem(listItem, ListViewUtil.PaperRating, hashMap.get("Id").toString(), StringUtil.objectToStr(hashMap.get("Name")));
            } else if ("ApiEvaluationRatingValue".equals(hashMap.get(getResources().getString(R.string.ParentNode)))) {
                if (-1 != index) {
                    listItem.get(index).put("ratingValue", ++iCount);
                }
            }
        }
        if (listItem.size() <= 0) {
            View v = findViewById(R.id.tv_background);
            v.setVisibility(View.VISIBLE);
        } else {
            ListViewUtil.setDiyListItem(listItem, ListViewUtil.PaperEvent, "提交");
            setView();
        }
    }

    private void setView() {
        LinearLayout parent = (LinearLayout) findViewById(R.id.ll_content);
        TextView tvChild;
        RatingBar rbChild;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 20;
        lp.rightMargin = 20;
        lp.topMargin = 10;
        lp.bottomMargin = 10;
        HashMap<String, Object> map;
        int ratingValue = 10;
        for (int i = 0; i < listItem.size(); i++) {
            map = listItem.get(i);
            if (map.containsKey("paperRating")) {
                LayoutInflater mInflater = LayoutInflater.from(this);
                View v = mInflater.inflate(R.layout.trainnet_item_rating, null);
                rbChild = (RatingBar) v.findViewById(R.id.rating);
                TextView tv_rating = (TextView) v.findViewById(R.id.tv_rating);
                tv_rating.setText(map.get("paperTitle").toString());
                rbChild.setNumStars(ratingValue);
                rbChild.setTag(i);
                rbChild.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        int pos = Integer.parseInt(ratingBar.getTag().toString());
                        listItem.get(pos).put("paperRating", String.valueOf(rating));
                    }
                });
                parent.addView(v);
            } else if (map.containsKey("paperInput")) {
                LayoutInflater mInflater = LayoutInflater.from(this);
                View v = mInflater.inflate(R.layout.trainnet_item_fillin, null);
                TextView tv_fill = (TextView) v.findViewById(R.id.tv_fill);
                tv_fill.setText(map.get("paperTitle").toString());
                etChild = (EditText) v.findViewById(R.id.fill);
                etChild.setTag(i);
                etChild.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int pos = Integer.parseInt(etChild.getTag().toString());
                        String text = etChild.getText().toString();
                        listItem.get(pos).put("paperInput", text);
                    }
                });
                parent.addView(v);
            } else if (map.containsKey("paperEvent")) {
                tvChild = new TextView(this);
                tvChild.setLayoutParams(lp);
                tvChild.setTextSize(20);
                tvChild.setPadding(5, 5, 5, 5);
                tvChild.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                tvChild.setClickable(true);
                tvChild.setText(map.get("paperEvent").toString());
                tvChild.setTextColor(getResources().getColor(R.color.white));
                tvChild.setBackgroundColor(getResources().getColor(R.color.app_blue));
                tvChild.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            JSONArray answerList = new JSONArray();
                            JSONObject answer;
                            for (HashMap<String, Object> mapResult : listItem) {
                                if (mapResult.containsKey("paperRating") || mapResult.containsKey("paperInput")) {
                                    answer = new JSONObject();
//                                    Log.e("---id---", mapResult.get("id").toString());
                                    if (mapResult.containsKey("paperRating")) {
                                        Float rating = Float.parseFloat(mapResult.get("paperRating").toString());
                                        if (rating == 0.0f) {
                                            ToastUtil.show(EvaluationPaperActivity.this, String.format("问题“%s”尚未评分，请检查", mapResult.get("paperTitle").toString()));
                                            return;
                                        }
                                        answer.put("Id", mapResult.get("id").toString());
                                        answer.put("Answer", mapResult.get("paperRating").toString());
                                        answer.put("Type", "Rating");
                                    } else {
                                        answer.put("Id", mapResult.get("id").toString());
                                        answer.put("Answer", mapResult.get("paperInput").toString());
                                        answer.put("Type", "Essay");
                                    }
                                    answerList.put(answer);
                                }
                            }
                            WebserviceUtil.getInstance().submitEvaluation(wr, activityId, answerList.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                parent.addView(tvChild);
            } else {
                if (map.containsKey("ratingValue")) {
                    ratingValue = Integer.parseInt(map.get("ratingValue").toString());
                }
                tvChild = new TextView(this);
                tvChild.setLayoutParams(lp);
                tvChild.setTextColor(getResources().getColor(R.color.app_blue));
                tvChild.setText(map.get("paperTitle").toString());
                tvChild.setPadding(5, 5, 5, 5);
                if (map.containsKey("paperSize")) {
                    float size = Float.parseFloat(map.get("paperSize").toString());
                    tvChild.setTextSize(size);
                } else {
                    tvChild.setTextSize(16);
                }
                if (map.containsKey("backColor")) {
                    int color = Integer.parseInt(map.get("backColor").toString());
                    tvChild.setBackgroundColor(getResources().getColor(color));
                }
                if (map.containsKey("paperAlign")) {
                    int gravity = Integer.parseInt(map.get("paperAlign").toString());
                    tvChild.setGravity(gravity);
                }
                parent.addView(tvChild);
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
