package com.zhongdasoft.svwtrainnet.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserMenu;

import java.util.ArrayList;
import java.util.List;

public class MyGridViewAdapter extends BaseAdapter {
    LinearLayout.LayoutParams params;
    private List<UserMenu> userMenuList = new ArrayList<>();
    private LayoutInflater mInflater;

    private View deleteView;
    private boolean isShowDelete;//根据这个变量来判断是否显示删除图标，true是显示，false是不显示
    private int width;

    public MyGridViewAdapter(Context context, List<UserMenu> userMenuList, int width) {
        this.userMenuList = userMenuList;
        this.width = width;
        mInflater = LayoutInflater.from(context);

        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
    }

    public void setIsShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        notifyDataSetChanged();
    }

    public int getCount() {
        return userMenuList.size();
    }

    public Object getItem(int position) {
        return userMenuList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewTag viewTag;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.trainnet_item_gridview,
                    null);

            // construct an item tag
            viewTag = new ItemViewTag((ImageView) convertView.findViewById(R.id.grid_item_image),
                    (TextView) convertView.findViewById(R.id.grid_item_name));
            convertView.setTag(viewTag);
        } else {
            viewTag = (ItemViewTag) convertView.getTag();
        }
        viewTag.mImage.getLayoutParams().width = width;
        viewTag.mName.getLayoutParams().width = width;
        // set name
        Resources res = convertView.getResources();

        viewTag.mImage.setImageResource(res.getIdentifier(userMenuList.get(position).getDrawableName(), null, null));
        viewTag.mName.setText(userMenuList.get(position).getResName());
        viewTag.mName.setTag(userMenuList.get(position).getActivityName());


        deleteView = convertView.findViewById(R.id.delete_markView);
        ((FrameLayout.LayoutParams) deleteView.getLayoutParams()).leftMargin = width / 2 - 70;
        deleteView.setVisibility(isShowDelete && !userMenuList.get(position).getResName().equals(res.getString(R.string.FuncAdd)) ? View.VISIBLE : View.GONE);//设置删除按钮是否显示

        return convertView;
    }

    class ItemViewTag {
        protected ImageView mImage;
        protected TextView mName;

        /**
         * The constructor to construct a navigation view tag
         *
         * @param name the name view of the item
         */
        public ItemViewTag(ImageView mImage, TextView name) {
            this.mImage = mImage;
            this.mName = name;
        }
    }
}
