package com.zhongdasoft.svwtrainnet.adapter;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.module.traininner.InnerPlanCreateUpdateActivity;
import com.zhongdasoft.svwtrainnet.module.traininner.InnerPlanDispatchActivity;
import com.zhongdasoft.svwtrainnet.module.traininner.InnerPlanScoreActivity;
import com.zhongdasoft.svwtrainnet.module.traininner.InnerPlanSummaryActivity;
import com.zhongdasoft.svwtrainnet.module.traininner.TrainInnerActivity;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.DialogUtil;
import com.zhongdasoft.svwtrainnet.util.HtmlUtil;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.Waiting;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class InnerListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private BaseActivity mActivity;
    private WeakReference<? extends BaseActivity> wr;
    private ArrayList<HashMap<String, Object>> mData;
    private HashMap<String, Object> resultMap;

    public InnerListViewAdapter(WeakReference<? extends BaseActivity> wr,
                                ArrayList<HashMap<String, Object>> mData) {
        this.wr = wr;
        this.mActivity = wr.get();
        this.mInflater = LayoutInflater.from(mActivity);
        this.mData = mData;
    }

    public ArrayList<HashMap<String, Object>> getData() {
        return this.mData;
    }

    public void setData(ArrayList<HashMap<String, Object>> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {

            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.trainnet_item_innerlist,
                    null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.subTitle = (TextView) convertView.findViewById(R.id.subTitle);
            int[] imgId = {R.id.img_op1, R.id.img_op2, R.id.img_op3};
            holder.img_op = new ImageView[imgId.length];
            for (int i = 1; i < holder.img_op.length + 1; i++) {
                holder.img_op[i - 1] = (ImageView) convertView.findViewById(imgId[i - 1]);
            }
            holder.infoDate = (TextView) convertView.findViewById(R.id.infoDate);
            holder.infoTitle = (TextView) convertView.findViewById(R.id.infoTitle);
            holder.infoTitle_event = (TextView) convertView.findViewById(R.id.infoTitle_event);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            int[] eventId = {R.id.tv_event1, R.id.tv_event2, R.id.tv_event3, R.id.tv_event4};
            holder.event = new TextView[eventId.length];
            for (int i = 1; i < holder.event.length + 1; i++) {
                holder.event[i - 1] = (TextView) convertView.findViewById(eventId[i - 1]);
            }
            holder.eventLine = convertView.findViewById(R.id.line3);
            holder.line = convertView.findViewById(R.id.line4);
            holder.img_noData = (ImageView) convertView.findViewById(R.id.img_noData);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        setText(position, "title", holder.title);
        setText(position, "subTitle", holder.subTitle);
        for (int i = 1; i < holder.img_op.length + 1; i++) {
            setImg(position, "img_op" + i, holder.img_op[i - 1]);
        }
        setText(position, "infoDate", holder.infoDate);
        setText(position, "infoTitle", holder.infoTitle);
        setText(position, "infoTitle_event", holder.infoTitle_event);
        setText(position, "info", holder.info);
        for (int i = 1; i < holder.event.length + 1; i++) {
            setText(position, "tv_event" + i, holder.event[i - 1]);
        }
        setGone(position, "noLine", holder.line);
        setGone(position, "noEvent", holder.eventLine);
        setImg(position, "img_noData", holder.img_noData);
        return convertView;
    }

    private void setGone(int position, String key, View v) {
        if (mData.get(position).containsKey(key)) {
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.VISIBLE);
        }
    }

    private void setText(int position, String key, TextView tv) {
        if (mData.get(position).containsKey(key)) {
            String info = "";
            if (mData.get(position).get(key) != null) {
                info = mData.get(position).get(key).toString();
            }
            tv.setText(HtmlUtil.fromHtml(info));
            tv.setVisibility(View.VISIBLE);
            if (key.startsWith("tv_event")) {
                final String value = info;
                final int pos = position;
                tv.setText(HtmlUtil.fromHtml(value));
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("item", mData.get(pos).get("planId").toString());
                        String summaryStatus = mData.get(pos).get("summaryStatus").toString();
                        if ("待审核".equals(summaryStatus)) {
                            bundle.putString("planStatus", "3");
                        } else if ("请修改".equals(summaryStatus)) {
                            bundle.putString("planStatus", "4");
                        } else {
                            bundle.putString("planStatus", mData.get(pos).get("planStatus").toString());
                        }
                        MySharedPreferences.getInstance().setStoreString("InnerPlanPos", pos + "");
                        switch (value) {
                            case "人员":
                                bundle.putInt("approveNumber", Integer.parseInt(mData.get(pos).get("approveNumber").toString()));
                                bundle.putInt("planNumber", Integer.parseInt(mData.get(pos).get("planNumber").toString()));
                                mActivity.readyGo(InnerPlanDispatchActivity.class, bundle);
                                break;
                            case "成绩":
                                bundle.putInt("approveNumber", Integer.parseInt(mData.get(pos).get("approveNumber").toString()));
                                mActivity.readyGo(InnerPlanScoreActivity.class, bundle);
                                break;
                            case "小结":
                                mActivity.readyGo(InnerPlanSummaryActivity.class, bundle);
                                break;
                            case "发布":
                                runThread(1, pos);
                                break;
                            case "提交":
                                runThread(2, pos);
                                break;
                            case "取消":
                                runThread(-1, pos);
                                break;
                            case "详情":
                                String msg = mData.get(pos).get("infoMsg").toString();
                                DialogUtil.getInstance().showDialog(wr, mActivity.getResources().getString(R.string.tips), msg, null, null);
                                break;
                        }
                    }
                });
            }
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    private void runThread(final int iStatus, final int pos) {
        final String planId = mData.get(pos).get("planId").toString();
        Waiting.show(mActivity, mActivity.getResources().getString(R.string.Loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (iStatus) {
                    case 1:
                        resultMap = TrainNetWebService.getInstance().InternalPublishPlan(mActivity, planId);
                        break;
                    case 2:
                        ArrayList<HashMap<String, Object>> resultList = TrainNetWebService.getInstance().InternalSummary(mActivity, planId);
                        String summary = null;
                        StringBuilder sbMsg = new StringBuilder();
                        int attachmentCount = 0;
                        for (HashMap<String, Object> map : resultList) {
                            if ("Result".equals(map.get(mActivity.getResources().getString(R.string.ParentNode)))) {
                                summary = map.get("Summary").toString();
                                if (summary.length() < 10) {
                                    sbMsg.append(mActivity.getResources().getString(R.string.errPlanSubmit1));
                                }
                            }
                            if ("ApiInternalSummaryAnnex".equals(map.get(mActivity.getResources().getString(R.string.ParentNode)))) {
                                attachmentCount++;
                            }
                        }
                        if (null == summary) {
                            sbMsg.append(mActivity.getResources().getString(R.string.errPlanSubmit1));
                        }
                        if (0 == attachmentCount) {
                            sbMsg.append(mActivity.getResources().getString(R.string.errPlanSubmit2));
                        }
                        if ("0".equals(mData.get(pos).get("approveNumber").toString())) {
                            sbMsg.append(mActivity.getResources().getString(R.string.errPlanSubmit3));
                        }
                        if (sbMsg.length() == 0) {
                            resultMap = TrainNetWebService.getInstance().InternalSubmit(mActivity, planId);
                        } else {
                            resultMap = new HashMap<>();
                            resultMap.put(mActivity.getResources().getString(R.string.Message), sbMsg.toString());
                        }
                        break;
                    case -1:
                        resultMap = TrainNetWebService.getInstance().InternalCancelPlan(mActivity, planId);
                        break;
                }
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Waiting.dismiss();
                        final String message = resultMap.get(mActivity.getResources().getString(R.string.Message)).toString();
                        DialogUtil.getInstance().showDialog(wr, mActivity.getResources().getString(R.string.tips), message, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog alertDialog = (AlertDialog) v.getTag();
                                alertDialog.dismiss();
                                mActivity.readyGo(TrainInnerActivity.class);
                            }
                        }, null);
                    }
                });
            }
        }).start();
    }

    private void setImg(int position, String key, ImageView img) {
        if (mData.get(position).containsKey(key)) {
//            Integer drawableId = (Integer) mData.get(position).get(key);
//            img.setImageResource(drawableId);
            img.setVisibility(View.VISIBLE);
            String info = "";
            if (mData.get(position).get(key) != null) {
                info = mData.get(position).get(key).toString();
            }
            if (key.contains("img_op")) {
                if ("编辑".equals(info)) {
                    img.setImageResource(R.drawable.trainnet_edit);
                }
                final String value = info;
                final int pos = position;
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("item", mData.get(pos).get("planId").toString());
                        bundle.putString("type", mData.get(pos).get("type").toString());
                        bundle.putString("planStatus", mData.get(pos).get("planStatus").toString());
                        bundle.putString("startDate", mData.get(pos).get("startDate").toString());
                        bundle.putString("finishDate", mData.get(pos).get("finishDate").toString());
                        bundle.putString("courseNo", mData.get(pos).get("courseNo").toString());
                        bundle.putString("courseName", mData.get(pos).get("courseName").toString());
                        bundle.putString("term", mData.get(pos).get("term").toString());
                        bundle.putString("planNumber", mData.get(pos).get("planNumber").toString());
                        bundle.putString("teacher", mData.get(pos).get("teacher").toString());
                        switch (value) {
                            case "编辑":
                                mActivity.readyGo(InnerPlanCreateUpdateActivity.class, bundle);
                                break;
                        }
                    }
                });
            } else {
                Integer drawableId = (Integer) mData.get(position).get(key);
                img.setImageResource(drawableId);
            }
        } else {
            img.setVisibility(View.GONE);
        }
    }

    public final class ViewHolder {
        public TextView title;
        public TextView subTitle;
        public ImageView[] img_op;
        public TextView infoDate;
        public TextView infoTitle;
        public TextView infoTitle_event;
        public TextView info;
        public TextView[] event;
        public View line;
        public View eventLine;
        public ImageView img_noData;
    }
}