package com.zhongdasoft.svwtrainnet.module.home.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.MyGridViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.base.BaseLazyFragment;
import com.zhongdasoft.svwtrainnet.greendao.CRUD;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ActivityKey;
import com.zhongdasoft.svwtrainnet.greendao.DaoQuery;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserMenu;
import com.zhongdasoft.svwtrainnet.util.ClassUtil;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.Scale;
import com.zhongdasoft.svwtrainnet.widget.DragGridView;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * 项目名称：TrainNet
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/28 16:20
 * 修改人：Administrator
 * 修改时间：2016/11/28 16:20
 * 修改备注：
 */

public class FavoriteFragment extends BaseLazyFragment {
    private WeakReference<? extends BaseActivity> wr;
    private boolean isShowDelete = false;
    private MyGridViewAdapter adapter;
    private DragGridView grid;
    private List<UserMenu> userMenuList;
    private String userName;
    private int width;

    public static FavoriteFragment newInstance(WeakReference<? extends BaseActivity> wr) {
        FavoriteFragment fragment = new FavoriteFragment();
        fragment.wr = wr;
        fragment.userName = MySharedPreferences.getInstance().getUserName(wr.get());
        fragment.width = Scale.getScreen(wr).getPxWidth() / 40;
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_favorite;
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
        setGrid();
        grid.setSelector(new ColorDrawable(Color.TRANSPARENT));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("rawtypes")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                TextView tv = (TextView) arg1.findViewById(R.id.grid_item_name);
                Bundle bundle = new Bundle();
                bundle.putInt("item", arg2);
                String resName = tv.getText().toString();
                if (isShowDelete && !resName.equals(ActivityKey.MainFavorite)) {
                    CRUD.getInstance().UpdateUserFavorite(userName, resName, false, false);
                    setDelete();
                    setGrid();
                    adapter.notifyDataSetChanged();
                } else {
                    wr.get().readyGo(ClassUtil.getInstance().getClass(tv.getTag().toString()), bundle);
                }
            }
        });
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                setDelete();
                return true;
            }
        });
        grid.setOnChangeListener(new DragGridView.OnChanageListener() {
            @Override
            public void onChange(int from, int to) {
                //FuncContent temp = funcList.get(from);
                //这里的处理需要注意下
                if (from < to) {
                    for (int i = from; i < to; i++) {
                        Collections.swap(userMenuList, i, i + 1);
                    }
                } else if (from > to) {
                    for (int i = from; i > to; i--) {
                        Collections.swap(userMenuList, i, i - 1);
                    }
                }
                int width = Scale.getScreen(wr).getPxWidth() / 40;
                adapter = new MyGridViewAdapter(getActivity(), userMenuList, width * 10);
                grid.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setDelete() {
        isShowDelete = !isShowDelete;
        adapter.setIsShowDelete(isShowDelete);
    }

    private void setGrid() {
        try {
            if (null == userName) {
                return;
            }
            userMenuList = DaoQuery.getInstance().getSavedUserMenuListOfFavorite(userName);
            grid = (DragGridView) getActivity().findViewById(R.id.function_gridview);
            grid.setPadding(width, 0, width, 0);
            adapter = new MyGridViewAdapter(wr.get(), userMenuList, width * 10);
            grid.setAdapter(adapter);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setGrid();
    }
}
