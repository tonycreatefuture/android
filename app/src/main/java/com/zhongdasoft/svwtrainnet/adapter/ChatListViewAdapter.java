package com.zhongdasoft.svwtrainnet.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.util.BitmapUtil;
import com.zhongdasoft.svwtrainnet.util.HtmlUtil;
import com.zhongdasoft.svwtrainnet.util.Scale;
import com.zhongdasoft.svwtrainnet.util.Screen;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<HashMap<String, Object>> mData;
    private WeakReference<? extends BaseActivity> wr;

    public ChatListViewAdapter(WeakReference<? extends BaseActivity> wr,
                               ArrayList<HashMap<String, Object>> mData) {
        this.wr = wr;
        this.mInflater = LayoutInflater.from(wr.get());
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

            convertView = mInflater.inflate(R.layout.trainnet_item_chatlist,
                    null);
            holder.timeText = (TextView) convertView.findViewById(R.id.timeText);

            holder.headLeft = (ImageView) convertView.findViewById(R.id.headLeft);
            holder.nameLeft = (TextView) convertView.findViewById(R.id.nameLeft);
            holder.contentLeft = (TextView) convertView.findViewById(R.id.contentLeft);
            holder.picLeft = (ImageView) convertView.findViewById(R.id.picLeft);
            holder.soundLeft = (TextView) convertView.findViewById(R.id.soundLeft);

            holder.headRight = (ImageView) convertView.findViewById(R.id.headRight);
            holder.nameRight = (TextView) convertView.findViewById(R.id.nameRight);
            holder.contentRight = (TextView) convertView.findViewById(R.id.contentRight);
            holder.picRight = (ImageView) convertView.findViewById(R.id.picRight);
            holder.soundRight = (TextView) convertView.findViewById(R.id.soundRight);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        setText(position, "timeText", holder.timeText);

        setImg(position, "headLeft", holder.headLeft);
        setText(position, "nameLeft", holder.nameLeft);
        setText(position, "contentLeft", holder.contentLeft);
        setImgSrc(position, "picLeft", holder.picLeft);
        setText(position, "soundLeft", holder.soundLeft);

        setImg(position, "headRight", holder.headRight);
        setText(position, "nameRight", holder.nameRight);
        setText(position, "contentRight", holder.contentRight);
        setImgSrc(position, "picRight", holder.picRight);
        setText(position, "soundRight", holder.soundRight);

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

    private void setImgSrc(int position, String key, ImageView img) {
        if (mData.get(position).containsKey(key)) {
            String path = (String) mData.get(position).get(key);
            Screen screen = Scale.getScreen(wr);
            Bitmap bitmap = BitmapUtil.getBitmapByCompress(path, screen.getPxWidth()/4, screen.getPxHeight()/4);
            img.setImageBitmap(bitmap);
            img.setVisibility(View.VISIBLE);
        } else {
            img.setVisibility(View.GONE);
        }
    }

    public final class ViewHolder {
        public TextView timeText;

        public ImageView headLeft;
        public TextView nameLeft;
        public TextView contentLeft;
        public ImageView picLeft;
        public TextView soundLeft;

        public ImageView headRight;
        public TextView nameRight;
        public TextView contentRight;
        public ImageView picRight;
        public TextView soundRight;
    }
}