package com.zhongdasoft.svwtrainnet.module.favorite;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.CheckListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.CRUD;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ActivityKey;
import com.zhongdasoft.svwtrainnet.greendao.DaoQuery;
import com.zhongdasoft.svwtrainnet.greendao.entity.UserMenu;
import com.zhongdasoft.svwtrainnet.util.DialogUtil;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FuncAddActivity extends BaseActivity {
    private ListView lv;
    private int level = 0;
    private boolean isChecked = false;
    private CheckListViewAdapter mAdapter;
    private Button rightBtn;
    private WeakReference<? extends BaseActivity> wr;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);
        userName = MySharedPreferences.getInstance().getUserName(this);

        mAdapter = new CheckListViewAdapter(this, getData(ActivityKey.MainFavorite));
        lv = (ListView) findViewById(R.id.listview_func_add);
        lv.setAdapter(mAdapter);
        lv.setDivider(null);
        lv.setOnItemClickListener(
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {
                        CheckBox cb = (CheckBox) arg1.findViewById(R.id.cb);
                        isChecked = cb.isChecked();
                        String isLeaf = cb.getTag().toString();
                        if ("0".equals(isLeaf)) {
                            level = level + 1;
                            rightBtn.setVisibility(View.INVISIBLE);
                            String activityName = (String) mAdapter.getData().get(arg2).get("activityName");
                            mAdapter.setData(getData(activityName));
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

        TextView leftBtn = (TextView) findViewById(R.id.trainnet_button_left);
        leftBtn.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (level > 0) {
                            level = level - 1;
                            if (level > 0) {
                                rightBtn.setVisibility(View.INVISIBLE);
                            } else {
                                rightBtn.setVisibility(View.VISIBLE);
                            }
                            mAdapter.setData(getData(ActivityKey.MainFavorite));
                            mAdapter.notifyDataSetChanged();
                        } else {
                            String userName = MySharedPreferences.getInstance().getUserName(FuncAddActivity.this);
                            boolean existUnsaved = DaoQuery.getInstance().existUnsavedUserFavorite(userName);
                            if (existUnsaved) {
                                final String tmpUserName = userName;
                                DialogUtil.getInstance().showDialog(wr, getResources().getString(R.string.tips), "您确定不保存当前菜单？", new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog alertDialog = (AlertDialog) v.getTag();
                                        alertDialog.dismiss();
                                        CRUD.getInstance().UpdateUserFavorite(tmpUserName, false);
                                        onBackPressed();
                                    }
                                }, new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog alertDialog = (AlertDialog) v.getTag();
                                        alertDialog.dismiss();
                                    }
                                });
                            } else {
                                onBackPressed();
                            }
                        }
                    }
                }
        );

        rightBtn = (Button) findViewById(R.id.trainnet_button_right);
        rightBtn.setText(R.string.save);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        CRUD.getInstance().UpdateUserFavorite(userName, true);
                        onBackPressed();
                    }
                }
        );

    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_train_funcadd;
    }

    @Override
    protected int getMTitle() {
        return R.string.title_funcadd;
    }

    private ArrayList<HashMap<String, Object>> getData(String pActivityName) {
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        /* 为动态数组添加数据 */
        List<UserMenu> userMenuList = DaoQuery.getInstance().getUserMenuListOfFavorite(userName, level);
        for (UserMenu um : userMenuList) {
            if (um.getActivityName().equals(ActivityKey.Add)) {
                continue;
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("userName", userName);
            map.put("activityName", um.getActivityName());
            map.put("isLeaf", um.getIsLeaf());
            if (level > 0) {
                CRUD.getInstance().UpdateUserFavorite(userName, pActivityName, null, false);
                if (isChecked) {
                    CRUD.getInstance().UpdateUserFavorite(userName, um.getActivityName(), null, true);
                } else {
                    CRUD.getInstance().UpdateUserFavorite(userName, um.getActivityName(), null, false);
                }
                map.put("cb", isChecked ? 1 : 0);
            } else {
                map.put("cb", um.getState());
            }
            map.put("img", getResources().getIdentifier(um.getDrawableName(), null, null));
            map.put("title", um.getResName());
//            map.put("info", getResources().getString(funcContent.getResId()) + "文字说明");
            listItem.add(map);
        }
        return listItem;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
