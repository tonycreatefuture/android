package com.zhongdasoft.svwtrainnet.widget.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.widget.popwindow.AbstractSpinerAdapter.IOnItemSelectListener;
import com.zhongdasoft.svwtrainnet.util.Scale;

import java.lang.ref.WeakReference;
import java.util.List;

public class SpinerPopWindow extends PopupWindow {

    private Context mContext;
    private boolean mSingle = true;
    private ListView mListView;
    private ListView mListView2;
    @SuppressWarnings("rawtypes")
    private AbstractSpinerAdapter mAdapter;
    private AbstractSpinerAdapter mAdapter2;
    private IOnItemSelectListener mItemSelectListener;
    private IOnItemSelectListener mItemSelectListener2;

    private boolean singleDismiss = true;

    public SpinerPopWindow(Context context, boolean isSingle) {
        super(context);
        mContext = context;
        mSingle = isSingle;
        init();
    }

    public void setDismiss(boolean singleDismiss){
        this.singleDismiss = singleDismiss;
    }

    public void setItemListener(IOnItemSelectListener listener) {
        mItemSelectListener = listener;
    }

    public void setItemListener2(IOnItemSelectListener listener) {
        mItemSelectListener2 = listener;
    }

    @SuppressWarnings("rawtypes")
    public void setAdatper(AbstractSpinerAdapter adapter) {
        mAdapter = adapter;
        mListView.setAdapter(mAdapter);
    }

    @SuppressWarnings("rawtypes")
    public void setAdatper2(AbstractSpinerAdapter adapter) {
        mAdapter2 = adapter;
        mListView2.setAdapter(mAdapter2);
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.spiner_window_layout, null);
        if (!mSingle) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.spiner_window_layout2, null);
        }
        setContentView(view);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);

        mListView = (ListView) view.findViewById(R.id.listview);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                if(singleDismiss) {
                    dismiss();
                }
                if (mItemSelectListener != null) {
                    mItemSelectListener.onItemClick(pos);
                }
            }
        });
        setBackgoundColor();
        if (!mSingle) {
            mListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                        long arg3) {
                    if (mItemSelectListener != null) {
                        mItemSelectListener.onItemClick(pos);
                    }
                    setBackgoundColor();
                    arg1.setBackgroundColor(mContext.getResources().getColor(R.color.dark_light_gray));
                }
            });

            mListView2 = (ListView) view.findViewById(R.id.listview2);
            setBackgoundColor2();
            int halfWidth = Scale.getScreen(new WeakReference<>((BaseActivity) mContext)).getPxWidth() / 2;
            mListView.getLayoutParams().width = halfWidth;
            mListView2.getLayoutParams().width = halfWidth;

            mListView2.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                        long arg3) {
                    dismiss();
                    if (mItemSelectListener2 != null) {
                        mItemSelectListener2.onItemClick(pos);
                    }
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void refreshData(List<T> list, int selIndex) {
        if (list != null && selIndex != -1) {
            if (mAdapter != null) {
                mAdapter.refreshData(list, selIndex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> void refreshData2(List<T> list, int selIndex) {
        if (list != null && selIndex != -1) {
            if (mAdapter2 != null) {
                mAdapter2.refreshData(list, selIndex);
            }
        }
    }

    private void setBackgoundColor() {
        for (int i = 0; i < mListView.getChildCount(); i++) {
            mListView.getChildAt(i).setBackgroundColor(mContext.getResources().getColor(R.color.light_gray));
        }
    }

    private void setBackgoundColor2() {
        for (int i = 0; i < mListView2.getChildCount(); i++) {
            mListView2.getChildAt(i).setBackgroundColor(mContext.getResources().getColor(R.color.gray_white));
        }
    }
}
