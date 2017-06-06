package com.zhongdasoft.svwtrainnet.module.home;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.module.more.TvContentActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class NewsActivity extends BaseActivity {
    /* 定义一个动态数组 */
    ArrayList<HashMap<String, Object>> listItem;
    private ArrayList<HashMap<String, Object>> newsList;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String HomeRefreshNews = getCache().getAsString(CacheKey.HomeRefreshNews);
        if (!StringUtil.isNullOrEmptyOrEmptySet(HomeRefreshNews)) {
            newsList = getGson().fromJson(HomeRefreshNews, new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
        } else {
            newsList = new ArrayList<>();
        }
        setListView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_news;
    }

    @Override
    protected int getMTitle() {
        return R.string.news;
    }

    private void setListView() {
        if (null == listItem) {
            listItem = new ArrayList<>();
        } else {
            listItem.clear();
        }
        for (int i = 0; i < newsList.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", newsList.get(i).get("Id").toString());
            map.put("info", newsList.get(i).get("Title").toString());
            map.put("info1", "<strong>发布日期：</strong>" + newsList.get(i).get("AddDate").toString().substring(0, 19).replace("T", " "));
            map.put("noRight", "");
            listItem.add(map);
        }
        if (listItem.size() <= 0) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("img_noData", R.drawable.trainnet_no_img);
            map.put("noLine", "");
            map.put("noRight", "");
            listItem.add(map);
        }

        BaseListViewAdapter mAdapter = new BaseListViewAdapter(this, listItem);
        lv = (ListView) findViewById(R.id.listview_news);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.GRAY));
        lv.setDivider(null);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (listItem.size() > 0 && listItem.get(arg2).containsKey("id")) {
                    String id = listItem.get(arg2).get("id").toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("item", id);
                    bundle.putString("news", getResources().getString(R.string.title_newscontent));
                    readyGo(TvContentActivity.class, bundle);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
