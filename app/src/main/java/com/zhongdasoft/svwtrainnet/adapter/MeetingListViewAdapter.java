package com.zhongdasoft.svwtrainnet.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.HtmlUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.wizlong.VCTActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class MeetingListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private BaseActivity mActivity;
    private WeakReference<? extends BaseActivity> wr;
    private ArrayList<HashMap<String, Object>> mData;
    private HashMap<String, Object> resultMap;

    public MeetingListViewAdapter(WeakReference<? extends BaseActivity> wr,
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

            convertView = mInflater.inflate(R.layout.trainnet_item_meetinglist,
                    null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.info1 = (TextView) convertView.findViewById(R.id.info1);
            holder.info2 = (TextView) convertView.findViewById(R.id.info2);
            holder.info3 = (TextView) convertView.findViewById(R.id.info3);
            holder.line = convertView.findViewById(R.id.line);
            holder.img_noData = (ImageView) convertView.findViewById(R.id.img_noData);
            holder.select_event = (TextView) convertView.findViewById(R.id.select_event);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        setText(position, "title", holder.title);
        setText(position, "info1", holder.info1);
        setText(position, "info2", holder.info2);
        setText(position, "info3", holder.info3);
        setGone(position, "noLine", holder.line);
        setImg(position, "img_noData", holder.img_noData);
        setText(position, "select_event", holder.select_event);

        return convertView;
    }

    private void setText(int position, String key, TextView tv) {
        if (mData.get(position).containsKey(key)) {
            String info = "";
            if (mData.get(position).get(key) != null) {
                info = mData.get(position).get(key).toString();
            }
            tv.setText(HtmlUtil.fromHtml(info));
            tv.setVisibility(View.VISIBLE);
            if (key.endsWith("_event")) {
                tv.setTag(info);
                final int pos = position;
                final String playType = mData.get(pos).get("playType").toString();
                final boolean canNotBePlayed = "pc".equals(playType) || "ios".equals(playType);
                if (canNotBePlayed) {
                    tv.setTextColor(mActivity.getResources().getColor(R.color.light_grey));
                }
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String info = String.valueOf(view.getTag());
                        switch (info) {
                            case "进入直播":
                                if (canNotBePlayed) {
                                    ToastUtil.show(mActivity, mActivity.getResources().getString(R.string.playError));
                                } else {
                                    runThread(1, pos);
                                }
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
        final String applyId = mData.get(pos).get("applyId").toString();
        final String coursewareId = mData.get(pos).get("coursewareId").toString();
        Waiting.show(mActivity, mActivity.getResources().getString(R.string.Loading));
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (iStatus) {
                    case 1:
                        resultMap = TrainNetWebService.getInstance().MeetToken(mActivity, applyId);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Waiting.dismiss();
                                final String returnCode = resultMap.get(mActivity.getResources().getString(R.string.ReturnCode)).toString();
                                final String message = resultMap.get(mActivity.getResources().getString(R.string.Message)).toString();
                                if ("0".equals(returnCode)) {
                                    String result = resultMap.get("Result").toString();

                                    Bundle subBundle = new Bundle();
                                    subBundle.putString("action", "vct");
                                    subBundle.putString("meetingId", coursewareId);
                                    subBundle.putString("pData", result);

                                    Bundle bundle = new Bundle();
                                    bundle.putBundle("action", subBundle);
                                    mActivity.readyGo(VCTActivity.class, bundle, "bundle");
                                } else {
                                    ToastUtil.show(mActivity, message);
                                }
                            }
                        });
                        break;
                }
            }
        }).start();
    }

    private void setImg(int position, String key, ImageView img) {
        if (mData.get(position).containsKey(key)) {
            Integer drawableId = (Integer) mData.get(position).get(key);
            img.setImageResource(drawableId);
            img.setVisibility(View.VISIBLE);
        } else {
            img.setVisibility(View.GONE);
        }
    }

    private void setGone(int position, String key, View v) {
        if (mData.get(position).containsKey(key)) {
            v.setVisibility(View.GONE);
        } else {
            v.setVisibility(View.VISIBLE);
        }
    }

    public final class ViewHolder {
        //        public ImageView img;
        public TextView title;
        public TextView info1;
        public TextView info2;
        public TextView info3;
        public View line;
        public ImageView img_noData;
        public TextView select_event;

    }
}