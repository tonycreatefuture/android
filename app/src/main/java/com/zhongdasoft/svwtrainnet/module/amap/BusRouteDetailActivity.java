package com.zhongdasoft.svwtrainnet.module.amap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.AMapUtil;
import com.zhongdasoft.svwtrainnet.adapter.BusSegmentListAdapter;

public class BusRouteDetailActivity extends BaseActivity {

    private BusPath mBuspath;
    private BusRouteResult mBusRouteResult;
    private TextView mTitle, mTitleBusRoute, mDesBusRoute;
    private ListView mBusSegmentList;
    private BusSegmentListAdapter mBusSegmentListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        init();
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
        if (intent != null) {
            mBuspath = intent.getParcelableExtra("bus_path");
            mBusRouteResult = intent.getParcelableExtra("bus_result");
        }
    }

    private void init() {
        mTitle = (TextView) findViewById(R.id.title_center);
        mTitle.setText("公交路线详情");
        mTitleBusRoute = (TextView) findViewById(R.id.firstline);
        mDesBusRoute = (TextView) findViewById(R.id.secondline);
        String dur = AMapUtil.getFriendlyTime((int) mBuspath.getDuration());
        String dis = AMapUtil.getFriendlyTime((int) mBuspath.getDistance());
        mTitleBusRoute.setText(dur + "(" + dis + ")");
        int taxiCost = (int) mBusRouteResult.getTaxiCost();
        mDesBusRoute.setText("打车约" + taxiCost + "元");
        mDesBusRoute.setVisibility(View.VISIBLE);
        configureListView();
    }

    private void configureListView() {
        mBusSegmentList = (ListView) findViewById(R.id.bus_segment_list);
        mBusSegmentListAdapter = new BusSegmentListAdapter(
                this.getApplicationContext(), mBuspath.getSteps());
        mBusSegmentList.setAdapter(mBusSegmentListAdapter);

    }

    public void onBackClick(View view) {
        this.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
