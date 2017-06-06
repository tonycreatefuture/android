package com.zhongdasoft.svwtrainnet.module.trainnormal;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.MenuListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ActivityKey;
import com.zhongdasoft.svwtrainnet.greendao.DaoQuery;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserMenu;
import com.zhongdasoft.svwtrainnet.util.ClassUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrainNormalActivity extends BaseActivity {
    private ListView lv;
    private List<UserMenu> userMenuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        userMenuList = DaoQuery.getInstance().getUserMenuListByActivityName(ActivityKey.Normal, true);
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

        MenuListViewAdapter mAdapter = new MenuListViewAdapter(this,
                listItem);
        lv = (ListView) findViewById(R.id.listview_train_normal);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.GRAY));
        lv.setDivider(null);
        lv.setOnItemClickListener(new OnItemClickListener() {
            @SuppressWarnings("rawtypes")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                TextView tv = (TextView) arg1.findViewById(R.id.title);
                Class clazz = ClassUtil.getInstance().getClass(tv.getTag().toString());
                readyGo(clazz);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_train_normal;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_trainnormal;
    }
}
