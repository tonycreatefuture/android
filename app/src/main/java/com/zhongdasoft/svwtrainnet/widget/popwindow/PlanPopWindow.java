package com.zhongdasoft.svwtrainnet.widget.popwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;

public class PlanPopWindow extends PopupWindow {

    private Context mContext;
    private ListView mListView;
    private PlanSpinerAdapter.IOnItemSelectListener mItemSelectListener;
    private PlanSpinerAdapter mAdapter;

    public PlanPopWindow(Context context) {
        super(context);
        mContext = context;
        init();
    }

    @SuppressWarnings("rawtypes")
    public void setAdatper(PlanSpinerAdapter adapter) {
        mAdapter = adapter;
        mListView.setAdapter(mAdapter);
    }

    public void setItemListener(PlanSpinerAdapter.IOnItemSelectListener listener) {
        mItemSelectListener = listener;
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.spiner_window_layout_plan, null);
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setDivider(null);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                if (pos != 5) {
                    return;
                } else {
                    MySharedPreferences.getInstance().removeString("InnerPlanClick");
                    dismiss();
                    if (mItemSelectListener != null) {
                        mItemSelectListener.onItemClick(pos);
                    }
                }
            }
        });
        setBackgoundColor();
    }

    private void setBackgoundColor() {
        for (int i = 0; i < mListView.getChildCount(); i++) {
            mListView.getChildAt(i).setBackgroundColor(mContext.getResources().getColor(R.color.light_gray));
        }
    }
}
