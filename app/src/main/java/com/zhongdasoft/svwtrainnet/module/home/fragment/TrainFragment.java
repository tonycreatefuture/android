package com.zhongdasoft.svwtrainnet.module.home.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.MyGridViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.base.BaseLazyFragment;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ActivityKey;
import com.zhongdasoft.svwtrainnet.greendao.DaoQuery;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserMenu;
import com.zhongdasoft.svwtrainnet.util.ClassUtil;
import com.zhongdasoft.svwtrainnet.util.Scale;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/28 16:21
 * 修改人：Administrator
 * 修改时间：2016/11/28 16:21
 * 修改备注：
 */

public class TrainFragment extends BaseLazyFragment {
    private WeakReference<? extends BaseActivity> wr;
    private GridView grid;

    public static TrainFragment newInstance(WeakReference<? extends BaseActivity> wr) {
        TrainFragment fragment = new TrainFragment();
        fragment.wr = wr;
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_train;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }

//        initRefreshLayout();
        initRecyclerView();
        isPrepared = false;
    }

    @Override
    protected void initRecyclerView() {
        final List<UserMenu> userMenuList = DaoQuery.getInstance().getUserMenuListByActivityName(ActivityKey.MainTrain, true);
        grid = (GridView) wr.get().findViewById(R.id.train_gridview);
        int width = Scale.getScreen(wr).getPxWidth() / 40;
        grid.setPadding(width, 0, width, 0);
        grid.setAdapter(new MyGridViewAdapter(wr.get(), userMenuList, width * 10));
        grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("rawtypes")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                TextView tv = (TextView) arg1.findViewById(R.id.grid_item_name);
                Bundle bundle = new Bundle();
                bundle.putInt("item", arg2);
                wr.get().readyGo(ClassUtil.getInstance().getClass(tv.getTag().toString()), bundle);
            }
        });
    }
}
