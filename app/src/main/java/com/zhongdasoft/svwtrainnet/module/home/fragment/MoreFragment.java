package com.zhongdasoft.svwtrainnet.module.home.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.MenuListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.base.BaseLazyFragment;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ActivityKey;
import com.zhongdasoft.svwtrainnet.greendao.DaoQuery;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserMenu;
import com.zhongdasoft.svwtrainnet.util.ClassUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
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

public class MoreFragment extends BaseLazyFragment {
    private ListView lv;
    private WeakReference<? extends BaseActivity> wr;
    private List<UserMenu> userMenuList;

    public static MoreFragment newInstance(WeakReference<? extends BaseActivity> wr) {
        MoreFragment fragment = new MoreFragment();
        fragment.wr = wr;
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_more;
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
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        userMenuList = DaoQuery.getInstance().getUserMenuListByActivityName(ActivityKey.MainMore, true);
        for (UserMenu um : userMenuList) {
            HashMap<String, Object> map = new HashMap<>();
            // map.put("cb", 1);
            map.put("img", getResources().getIdentifier(um.getDrawableName(), null, null));
            map.put("title", um.getResName());
            map.put("activityName", um.getActivityName());
            // map.put("info", newsDate[i]);
            // map.put("id", newsId[i]);
            listItem.add(map);
        }

        MenuListViewAdapter mAdapter = new MenuListViewAdapter(wr.get(),
                listItem);
        lv = (ListView) wr.get().findViewById(R.id.listview_moreservice);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.GRAY));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressWarnings("rawtypes")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                TextView tv = (TextView) arg1.findViewById(R.id.title);
                Class clazz = ClassUtil.getInstance().getClass(tv.getTag().toString());
                wr.get().readyGo(clazz);
            }
        });
    }
}
