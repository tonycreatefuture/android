package com.zhongdasoft.svwtrainnet.module.trainextra;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ListView;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.util.ListViewUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class ExtraPlanCounselingActivity extends BaseActivity {
    private ListView lv;
    private BaseActivity activity;
    private ArrayList<HashMap<String, Object>> listItem;
    private WeakReference<? extends BaseActivity> wr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        Bundle bundle = this.getIntent().getExtras();
        wr = new WeakReference<>(this);

        if (null == listItem) {
            listItem = new ArrayList<>();
        } else {
            listItem.clear();
        }

        ListViewUtil.setListItem(listItem, bundle.get("CourseName") == null ? "" : bundle.get("CourseName").toString(), ListViewUtil.PaperTitle);

//        ListViewUtil.setListItem(listItem, "辅导课程", ListViewUtil.PaperSubTitle);
//        ListViewUtil.setListItem(listItem, bundle.get("Content") == null ? "" : bundle.get("Content").toString(), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, "辅导时间", ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, bundle.get("StartDate") == null ? "" : bundle.get("StartDate").toString().replace("T", " ").substring(0, 16) + (bundle.get("EndDate") == null ? "" : " 至 " + bundle.get("EndDate").toString().replace("T", " ").substring(0, 16)), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, "辅导地点", ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, bundle.get("PlaceName") == null ? "" : bundle.get("PlaceName").toString(), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, "辅导老师", ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, bundle.get("Teachers") == null ? "" : bundle.get("Teachers").toString(), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, "课程介绍", ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, bundle.get("Content") == null ? "" : bundle.get("Content").toString(), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, "必修岗位", ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, bundle.get("RequiredWorkTypes") == null ? "" : bundle.get("RequiredWorkTypes").toString(), ListViewUtil.PaperInfo);

        ListViewUtil.setListItem(listItem, "选修岗位", ListViewUtil.PaperSubTitle);
        ListViewUtil.setListItem(listItem, bundle.get("OptionalWorkTypes") == null ? "" : bundle.get("OptionalWorkTypes").toString(), ListViewUtil.PaperInfo);

        BaseListViewAdapter mAdapter = new BaseListViewAdapter(activity, listItem);
        lv = (ListView) activity.findViewById(R.id.listview_coursecontent);
        lv.setAdapter(mAdapter);
        lv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        lv.setDivider(null);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_extra_plancounseling;
    }

    @Override
    protected int getMTitle() {
        return R.string.extraDetail;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

}
