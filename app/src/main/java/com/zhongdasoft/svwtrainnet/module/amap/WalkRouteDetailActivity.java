package com.zhongdasoft.svwtrainnet.module.amap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.WalkPath;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.AMapUtil;
import com.zhongdasoft.svwtrainnet.adapter.WalkSegmentListAdapter;

public class WalkRouteDetailActivity extends BaseActivity {
    private WalkPath mWalkPath;
    private TextView mTitle, mTitleWalkRoute;
    private ListView mWalkSegmentList;
    private WalkSegmentListAdapter mWalkSegmentListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        mTitle = (TextView) findViewById(R.id.title_center);
        mTitle.setText("步行路线详情");
        mTitleWalkRoute = (TextView) findViewById(R.id.firstline);
        String dur = AMapUtil.getFriendlyTime((int) mWalkPath.getDuration());
        String dis = AMapUtil
                .getFriendlyLength((int) mWalkPath.getDistance());
        mTitleWalkRoute.setText(dur + "(" + dis + ")");
        mWalkSegmentList = (ListView) findViewById(R.id.bus_segment_list);
        mWalkSegmentListAdapter = new WalkSegmentListAdapter(
                this.getApplicationContext(), mWalkPath.getSteps());
        mWalkSegmentList.setAdapter(mWalkSegmentListAdapter);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.map_route_detail;
    }

    @Override
    protected int getMTitle() {
        return 0;
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mWalkPath = intent.getParcelableExtra("walk_path");
    }

    public void onBackClick(View view) {
        this.finish();
    }

}
