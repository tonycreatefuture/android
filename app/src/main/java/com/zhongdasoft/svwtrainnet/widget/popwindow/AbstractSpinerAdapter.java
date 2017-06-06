package com.zhongdasoft.svwtrainnet.widget.popwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSpinerAdapter<T> extends BaseAdapter {

    // private Context mContext;
    private List<T> mObjects = new ArrayList<>();
    private LayoutInflater mInflater;
    private boolean isSingle;
    // private int mSelectItem = 0;

    public AbstractSpinerAdapter(Context context, boolean isSingle) {
        init(context);
        this.isSingle = isSingle;
    }

    public void refreshData(List<T> objects, int selIndex) {
        mObjects = objects;
        if (selIndex < 0) {
            selIndex = 0;
        }
        if (selIndex >= mObjects.size()) {
            selIndex = mObjects.size() - 1;
        }

        // mSelectItem = selIndex;
    }

    private void init(Context context) {
        // mContext = context;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        return mObjects.size();
    }

    @Override
    public Object getItem(int pos) {
        return mObjects.get(pos).toString();
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;

        if (convertView == null) {
            if (isSingle) {
                convertView = mInflater.inflate(R.layout.spiner_item_layout, null);
            } else {
                convertView = mInflater.inflate(R.layout.spiner_item_layout2, null);
            }
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView
                    .findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Object item = getItem(pos);
        viewHolder.mTextView.setText(item.toString());

        return convertView;
    }

    public interface IOnItemSelectListener {
        void onItemClick(int pos);
    }

    public static class ViewHolder {
        public TextView mTextView;
    }

}
