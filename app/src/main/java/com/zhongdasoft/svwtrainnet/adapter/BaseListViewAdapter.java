package com.zhongdasoft.svwtrainnet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.util.HtmlUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class BaseListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<HashMap<String, Object>> mData;


    public BaseListViewAdapter(BaseActivity activity,
                               ArrayList<HashMap<String, Object>> mData) {
        this.mInflater = LayoutInflater.from(activity);
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

            convertView = mInflater.inflate(R.layout.trainnet_item_baselist,
                    null);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            holder.infoDesc = (TextView) convertView.findViewById(R.id.infoDesc);
            int[] id = {R.id.info1, R.id.info2, R.id.info3, R.id.info4,
                    R.id.info5};
            holder.infoDetail = new TextView[id.length];
            for (int i = 1; i < holder.infoDetail.length + 1; i++) {
                holder.infoDetail[i - 1] = (TextView) convertView.findViewById(id[i - 1]);
            }
            holder.line = convertView.findViewById(R.id.line);
            holder.img_noData = (ImageView) convertView.findViewById(R.id.img_noData);
            holder.title_noData = (TextView) convertView.findViewById(R.id.tv_title_noData);
            holder.paperEvent = (TextView) convertView.findViewById(R.id.tv_event);
            holder.paperEventDone = (TextView) convertView.findViewById(R.id.tv_eventDone);
            holder.paperEventTwo = (TextView) convertView.findViewById(R.id.tv_eventTwo);
            holder.paperTitle = (TextView) convertView.findViewById(R.id.tv_paperTitle);
            holder.paperSubTitle = (TextView) convertView.findViewById(R.id.tv_paperSubTitle);
            holder.paperInfo = (TextView) convertView.findViewById(R.id.tv_paperInfo);
            holder.paperImg = (ImageView) convertView.findViewById(R.id.img_paperImg);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setText(position, "info", holder.info);
        setText(position, "infoDesc", holder.infoDesc);
        setTextColor(position, "infoColor", holder.info);
        for (int i = 1; i < holder.infoDetail.length + 1; i++) {
            setText(position, "info" + i, holder.infoDetail[i - 1]);
        }
        setImg(position, "img_noData", holder.img_noData);
        setText(position, "title_noData", holder.title_noData);

        //当前行没有下划线
        setGone(position, "noLine", holder.line);
        //当前行没有下划线
        setText(position, "paperEvent", holder.paperEvent);
        setText(position, "paperEventDone", holder.paperEventDone);
        setText(position, "paperEventTwo", holder.paperEventTwo);
        setText(position, "paperTitle", holder.paperTitle);
        setText(position, "paperSubTitle", holder.paperSubTitle);
        setText(position, "paperInfo", holder.paperInfo);
        setImg(position, "paperImg", holder.paperImg);


        if (mData.get(position).containsKey("infoDesc")) {
            int len = mData.get(position).get("infoDesc").toString().length();
            ((RelativeLayout.LayoutParams) holder.info.getLayoutParams()).rightMargin = 30 * len + 50;
        }

        if (holder.paperImg.getVisibility() == View.VISIBLE) {
            ((RelativeLayout.LayoutParams) holder.paperInfo.getLayoutParams()).leftMargin = holder.paperImg.getDrawable().getIntrinsicWidth() + 40;
        }

        return convertView;
    }

    private void setTextColor(int position, String key, TextView tv) {
        if (mData.get(position).containsKey(key)) {
            int color = Integer.parseInt(mData.get(position).get(key).toString());
            tv.setTextColor(color);
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
        } else {
            tv.setVisibility(View.GONE);
        }
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
        public TextView info;
        public TextView infoDesc;
        public TextView[] infoDetail;
        public View line;
        public ImageView img_noData;
        public TextView title_noData;
        public TextView paperEvent;
        public TextView paperEventDone;
        public TextView paperEventTwo;
        public TextView paperTitle;
        public TextView paperSubTitle;
        public TextView paperInfo;
        public ImageView paperImg;
    }
}