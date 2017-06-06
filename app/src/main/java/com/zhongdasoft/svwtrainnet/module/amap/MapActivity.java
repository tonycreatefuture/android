package com.zhongdasoft.svwtrainnet.module.amap;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.adapter.BusResultListAdapter;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.module.amap.overlay.DrivingRouteOverlay;
import com.zhongdasoft.svwtrainnet.module.amap.overlay.WalkRouteOverlay;
import com.zhongdasoft.svwtrainnet.module.trainnormal.NormalPlanActivity;
import com.zhongdasoft.svwtrainnet.util.AMapUtil;
import com.zhongdasoft.svwtrainnet.util.NetManager;
import com.zhongdasoft.svwtrainnet.util.Scale;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;

import java.lang.ref.WeakReference;

public class MapActivity extends BaseActivity implements OnMapClickListener,
        OnMarkerClickListener, OnInfoWindowClickListener, InfoWindowAdapter,
        OnRouteSearchListener, OnGeocodeSearchListener, LocationSource,
        AMapLocationListener {
    private AMap aMap;
    private MapView mapView;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private BusRouteResult mBusRouteResult;
    private WalkRouteResult mWalkRouteResult;
    private String mCurrentCityName = "上海";
    private String mCurrentCityCode = "021";
    private final int ROUTE_TYPE_BUS = 1;
    private final int ROUTE_TYPE_DRIVE = 2;
    private final int ROUTE_TYPE_WALK = 3;
    private ImageView mBus;
    private ImageView mDrive;
    private ImageView mWalk;
    private LinearLayout mBusResultLayout;
    private ListView mBusResultList;
    private TextView mBusResultReturn;

    private ProgressDialog progDialog = null;// 搜索时进度条
    private GeocodeSearch geocoderSearch;
    private String addressName;
    private GeocodeAddress address;
    private LatLonPoint latLonPoint;
    private Marker geoMarker;
    private Marker regeoMarker;

    private LatLonPoint mStartPoint; // 起点，
    private LatLonPoint mEndPoint;// 终点，
    private RelativeLayout mBottomLayout;
    private LinearLayout mBottomDetailLayout;
    private LinearLayout mBottomReturnLayout;
    private LinearLayout mBottomCenterLayout;
    private TextView mRouteTimeDes, mRouteDetailDes;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation startLocation;

    private boolean isFirstLocation = true;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mContext = this.getApplicationContext();
        mapView = (MapView) findViewById(R.id.route_map);
        mapView.onCreate(bundle);// 此方法必须重写

        int type = NetManager.getConnectedType(mContext);
        if (ConnectivityManager.TYPE_MOBILE == type) {
            ToastUtil.show(this, getResources().getString(R.string.notWifiMap));
        }

        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.map_route_activity;
    }

    @Override
    protected int getMTitle() {
        return 0;
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
            geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            regeoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        registerListener();
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mBottomDetailLayout = (LinearLayout) findViewById(R.id.ll_detail);
        mBottomReturnLayout = (LinearLayout) findViewById(R.id.ll_return);
        mBottomCenterLayout = (LinearLayout) findViewById(R.id.ll_center);
        mBusResultLayout = (LinearLayout) findViewById(R.id.bus_result);
        mBusResultReturn = (TextView) findViewById(R.id.bus_result_return);
        int width = Scale.getScreen(new WeakReference<>(this)).getPxWidth();
        mBottomDetailLayout.getLayoutParams().width = (int) (width * 0.2f);
        mBottomReturnLayout.getLayoutParams().width = (int) (width * 0.2f);
        mBottomCenterLayout.getLayoutParams().width = (int) (width * 0.6f);
        mRouteTimeDes = (TextView) findViewById(R.id.firstline);
        mRouteDetailDes = (TextView) findViewById(R.id.secondline);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        mapView.setVisibility(View.VISIBLE);
        mDrive = (ImageView) findViewById(R.id.route_drive);
        mBus = (ImageView) findViewById(R.id.route_bus);
        mWalk = (ImageView) findViewById(R.id.route_walk);
        mBusResultList = (ListView) findViewById(R.id.bus_result_list);
        mBusResultReturn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                readyGo(NormalPlanActivity.class);
            }
        });
        mBottomReturnLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                readyGo(NormalPlanActivity.class);
            }
        });
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    /**
     * 注册监听
     */
    private void registerListener() {
        aMap.setOnMapClickListener(MapActivity.this);
        aMap.setOnMarkerClickListener(MapActivity.this);
        aMap.setOnInfoWindowClickListener(MapActivity.this);
        aMap.setInfoWindowAdapter(MapActivity.this);
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
    }

//    /**
//     * 在地图上添加marker
//     */
//    private void addMarkersToMap() {
//        Marker marker = aMap.addMarker(new MarkerOptions().icon(
//                BitmapDescriptorFactory
//                        .fromResource(R.drawable.map_offline_back)).draggable(
//                true));
//        marker.setPositionByPixels(40, 40 + Scale.getStatusHeight(this));
//    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            ToastUtil.show(mContext, "定位中，稍后再试...");
            return;
        }
        if (mEndPoint == null) {
            ToastUtil.show(mContext, "终点未设置");
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_BUS) {// 公交路径规划
            BusRouteQuery query = new BusRouteQuery(fromAndTo, mode,
                    mCurrentCityName, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        } else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    public void onBusClick(View view) {
        searchRouteResult(ROUTE_TYPE_BUS, RouteSearch.BusDefault);
        mDrive.setImageResource(R.drawable.map_route_drive_normal);
        mBus.setImageResource(R.drawable.map_route_bus_select);
        mWalk.setImageResource(R.drawable.map_route_walk_normal);
        mapView.setVisibility(View.GONE);
        mBusResultReturn.setVisibility(View.VISIBLE);
        mBusResultLayout.setVisibility(View.VISIBLE);
    }

    public void onDriveClick(View view) {
        searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
        mDrive.setImageResource(R.drawable.map_route_drive_select);
        mBus.setImageResource(R.drawable.map_route_bus_normal);
        mWalk.setImageResource(R.drawable.map_route_walk_normal);
        mapView.setVisibility(View.VISIBLE);
        mBusResultReturn.setVisibility(View.GONE);
        mBusResultLayout.setVisibility(View.GONE);
    }

    public void onWalkClick(View view) {
        searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
        mDrive.setImageResource(R.drawable.map_route_drive_normal);
        mBus.setImageResource(R.drawable.map_route_bus_normal);
        mWalk.setImageResource(R.drawable.map_route_walk_select);
        mapView.setVisibility(View.VISIBLE);
        mBusResultReturn.setVisibility(View.GONE);
        mBusResultLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {
        dissmissProgressDialog();
        mBottomLayout.setVisibility(View.GONE);
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mBusRouteResult = result;
                    BusResultListAdapter mBusResultListAdapter = new BusResultListAdapter(
                            mContext, mBusRouteResult);
                    mBusResultList.setAdapter(mBusResultListAdapter);
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }
            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
//        dissmissProgressDialog();
//        aMap.clear();// 清理地图上的所有覆盖物
//        if (errorCode == 1000) {
//            if (result != null && result.getPaths() != null) {
//                if (result.getPaths().size() > 0) {
//                    mBusRouteResult = result;
//                    final BusPath busPath = mBusRouteResult.getPaths().get(0);
//                    BusRouteOverlay busRouteOverlay = new BusRouteOverlay(this,
//                            aMap, busPath, mBusRouteResult.getStartPos(),
//                            mBusRouteResult.getTargetPos());
//                    busRouteOverlay.removeFromMap();
//                    busRouteOverlay.addToMap();
//                    busRouteOverlay.zoomToSpan();
//                    mBottomLayout.setVisibility(View.VISIBLE);
//                    int dis = (int) busPath.getDistance();
//                    int dur = (int) busPath.getDuration();
//                    String des = AMapUtil.getFriendlyTime(dur) + "("
//                            + AMapUtil.getFriendlyLength(dis) + ")";
//                    mRouteTimeDes.setText(des);
//                    mRouteDetailDes.setVisibility(View.VISIBLE);
//                    int taxiCost = (int) mBusRouteResult.getTaxiCost();
//                    mRouteDetailDes.setText("打车约" + taxiCost + "元");
//                    mBottomDetailLayout.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(mContext,
//                                    BusRouteDetailActivity.class);
//                            intent.putExtra("bus_path", busPath);
//                            intent.putExtra("bus_result", mBusRouteResult);
//                            startActivity(intent);
//                        }
//                    });
//                    mBottomReturnLayout.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(mContext,
//                                    NormalPlanActivity.class);
//                            startActivity(intent);
//                        }
//                    });
////                    addMarkersToMap();// 往地图上添加marker
//                } else if (result != null && result.getPaths() == null) {
//                    ToastUtil.show(mContext, R.string.no_result);
//                }
//            } else {
//                ToastUtil.show(mContext, R.string.no_result);
//            }
//        } else {
//            ToastUtil.showerror(this.getApplicationContext(), errorCode);
//        }
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            this, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "("
                            + AMapUtil.getFriendlyLength(dis) + ")";
                    mRouteTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.VISIBLE);
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                    mRouteDetailDes.setText("打车约" + taxiCost + "元");
                    mBottomDetailLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext,
                                    DriveRouteDetailActivity.class);
                            intent.putExtra("drive_path", drivePath);
                            intent.putExtra("drive_result", mDriveRouteResult);
                            startActivity(intent);
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }

            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "("
                            + AMapUtil.getFriendlyLength(dis) + ")";
                    mRouteTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.GONE);
                    mBottomDetailLayout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext,
                                    WalkRouteDetailActivity.class);
                            intent.putExtra("walk_path", walkPath);
                            intent.putExtra("walk_result", mWalkRouteResult);
                            startActivity(intent);
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }

            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (aMap != null) {
            onBackPressed();
        }
        return false;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    /**
     * 响应地理编码
     */
    public void getLatlon(final String name) {
        showProgressDialog();

        GeocodeQuery query = new GeocodeQuery(name, mCurrentCityCode);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        showProgressDialog();
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 1000) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                address = result.getGeocodeAddressList().get(0);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
                geoMarker.setPosition(AMapUtil.convertToLatLng(address
                        .getLatLonPoint()));
                addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
                        + address.getFormatAddress();
                ToastUtil.show(MapActivity.this, addressName);
                // 上海火车站 31.2494627527, 121.458175580
                // 上海南站 31.1530696574,121.4294551575
                // 上海虹桥站 31.1941848846,121.3205848111
                // 上海浦东机场 31.1499731179,121.8060818419
                if (null == startLocation) {
                    mStartPoint = new LatLonPoint(31.2494627527, 121.4581755806);
                } else {
                    mStartPoint = new LatLonPoint(startLocation.getLatitude(), startLocation.getLongitude());
                }
                mEndPoint = address.getLatLonPoint();
                onDriveClick(null);
            } else {
                ToastUtil.show(MapActivity.this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }

    /**
     * 逆地理编码回调
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress()
                        + "附近";
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(latLonPoint), 15));
                regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
                ToastUtil.show(MapActivity.this, addressName);
            } else {
                ToastUtil.show(MapActivity.this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                //mLocationErrText.setVisibility(View.GONE);
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                mCurrentCityCode = amapLocation.getCityCode();
                mCurrentCityName = amapLocation.getCity();
                startLocation = amapLocation;
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                ToastUtil.show(this, errText);
                //Log.e("AmapErr",errText);
                //mLocationErrText.setVisibility(View.VISIBLE);
                //mLocationErrText.setText(errText);
            }
            if (isFirstLocation) {
                isFirstLocation = false;
                addressName = getIntent().getStringExtra("item");
                getLatlon(addressName);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }
}
