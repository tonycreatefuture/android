package com.zhongdasoft.svwtrainnet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.CRUD;
import com.zhongdasoft.svwtrainnet.util.HtmlUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<HashMap<String, Object>> mData;

    public CheckListViewAdapter(BaseActivity activity,
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

            convertView = mInflater.inflate(R.layout.trainnet_item_checklist,
                    null);
            holder.cb = (CheckBox) convertView.findViewById(R.id.cb);
            holder.img = (ImageView) convertView.findViewById(R.id.img);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mData.get(position).containsKey("cb")) {
            final Integer i = (Integer) mData.get(position).get("cb");
            final String userName = mData.get(position).get("userName").toString();
            final String activityName = mData.get(position).get("activityName").toString();
            boolean b = (i == 1 || i == 3 ? true : false);
            if (mData.get(position).containsKey("isLeaf") && null != mData.get(position).get("isLeaf")) {
                holder.cb.setTag(mData.get(position).get("isLeaf").toString());
            }
            holder.cb.setChecked(b);
            holder.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    if (cb.isChecked()) {
                        CRUD.getInstance().UpdateUserFavorite(userName, activityName, null, true);
                    } else {
                        CRUD.getInstance().UpdateUserFavorite(userName, activityName, null, false);
                    }
                }
            });
        } else {
            holder.cb.setVisibility(View.GONE);
        }

        setImg(position, "img", holder.img);
        setText(position, "title", holder.title);

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

    private void setImg(int position, String key, ImageView img) {
        if (mData.get(position).containsKey(key)) {
            Integer drawableId = (Integer) mData.get(position).get(key);
            img.setImageResource(drawableId);
            img.setVisibility(View.VISIBLE);
        } else {
            img.setVisibility(View.GONE);
        }
    }

    public final class ViewHolder {
        public CheckBox cb;
        public ImageView img;
        public TextView title;
    }
}