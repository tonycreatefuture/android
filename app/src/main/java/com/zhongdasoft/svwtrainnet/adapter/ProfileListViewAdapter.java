package com.zhongdasoft.svwtrainnet.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.util.HtmlUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ProfileListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<HashMap<String, Object>> mData;
    private BaseActivity activity;


    public ProfileListViewAdapter(BaseActivity activity,
                                  ArrayList<HashMap<String, Object>> mData) {
        this.activity = activity;
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

            convertView = mInflater.inflate(R.layout.trainnet_item_profilelist,
                    null);
            holder.line = convertView.findViewById(R.id.line);
            holder.paperEvent = (TextView) convertView.findViewById(R.id.tv_event);
            holder.paperEventNoBack = (TextView) convertView.findViewById(R.id.tv_eventNoBack);
            holder.paperTitle = (TextView) convertView.findViewById(R.id.tv_paperTitle);
            holder.paperSubTitle = (TextView) convertView.findViewById(R.id.tv_paperSubTitle);
            holder.paperInfo = (TextView) convertView.findViewById(R.id.tv_paperInfo);
            holder.ll_profile = (LinearLayout) convertView.findViewById(R.id.ll_profile);
            holder.paperImg = (ImageView) convertView.findViewById(R.id.right);
            holder.paperImgBig = (ImageView) convertView.findViewById(R.id.rightBig);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //当前行没有下划线
        setGone(position, "noLine", holder.line);
        //当前行没有下划线
        setText(position, "paperEvent", holder.paperEvent);
        setText(position, "paperEventNoBack", holder.paperEventNoBack);
        setText(position, "paperTitle", holder.paperTitle);
        setText(position, "paperSubTitle", holder.paperSubTitle);
        setText(position, "paperInfo", holder.paperInfo);
        setBitmap(position, "paperImgEvent", holder.ll_profile);
        setRight(position, "right", holder.paperImg);
        setRight(position, "rightBig", holder.paperImgBig);

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
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    private void setRight(int position, String key, ImageView img) {
        if (mData.get(position).containsKey(key)) {
            img.setVisibility(View.VISIBLE);
        } else {
            img.setVisibility(View.GONE);
        }
    }

    private void setBitmap(int position, String key, LinearLayout ll) {
        if (mData.get(position).containsKey(key)) {
            String filePath = mData.get(position).get(key).toString();
            ImageView img = (ImageView) ll.getChildAt(0);
            File file = new File(filePath);
            if (file.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(filePath);
                img.setImageBitmap(bm);
            } else {
                img.setImageDrawable(activity.getResources().getDrawable(R.drawable.chat_avatar_def));
            }
            LinearLayout llChild = (LinearLayout) ll.getChildAt(1);
            TextView tv1 = (TextView) llChild.getChildAt(0);
            TextView tv2 = (TextView) llChild.getChildAt(1);
            tv1.setText(mData.get(position).get("paperImgInfo1").toString());
            tv2.setText(mData.get(position).get("paperImgInfo2").toString());
            ll.setVisibility(View.VISIBLE);
        } else {
            ll.setVisibility(View.GONE);
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
        public View line;
        public TextView paperEvent;
        public TextView paperEventNoBack;
        public TextView paperTitle;
        public TextView paperSubTitle;
        public TextView paperInfo;
        public LinearLayout ll_profile;
        public ImageView paperImg;
        public ImageView paperImgBig;
    }
}