package com.zhongdasoft.svwtrainnet.module.home;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netease.nim.uikit.cache.DataCacheManager;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.TrainNetApp;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.greendao.Cache.ACache;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.imdemo.DemoCache;
import com.zhongdasoft.svwtrainnet.imdemo.config.preference.Preferences;
import com.zhongdasoft.svwtrainnet.imdemo.config.preference.UserPreferences;
import com.zhongdasoft.svwtrainnet.module.more.TvContentActivity;
import com.zhongdasoft.svwtrainnet.network.TrainNetWebService;
import com.zhongdasoft.svwtrainnet.util.CalendarUtil;
import com.zhongdasoft.svwtrainnet.util.HtmlUtil;
import com.zhongdasoft.svwtrainnet.util.ListViewUtil;
import com.zhongdasoft.svwtrainnet.util.MySharedPreferences;
import com.zhongdasoft.svwtrainnet.util.NetManager;
import com.zhongdasoft.svwtrainnet.util.NotifyUtil;
import com.zhongdasoft.svwtrainnet.util.Scale;
import com.zhongdasoft.svwtrainnet.util.Screen;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.util.ToastUtil;
import com.zhongdasoft.svwtrainnet.util.Waiting;
import com.zhongdasoft.svwtrainnet.util.WebserviceUtil;
import com.zhongdasoft.svwtrainnet.widget.ImageCycleView;
import com.zhongdasoft.svwtrainnet.widget.ImageCycleView.ImageCycleViewListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class HomeHandler implements SwipeRefreshLayout.OnRefreshListener {
    private static final int REFRESH_COMPLETE = 0X110;
    private static ArrayList<HashMap<String, Object>> newsList;
    private final int showLines = 3;
    private ArrayList<HashMap<String, Object>> sliderList;
    private ImageCycleView mAdView;
    private ViewFlipper viewFlipper = null;
    private AbortableFuture<LoginInfo> loginRequest;
    /**
     * 抽屉布局
     */
    private DrawerLayout mDrawerLayout;
    private BaseActivity activity;
    private MainActivity mActivity;
    private WeakReference<? extends BaseActivity> myActivity;
    private ImageButton btnPerson;
    //    private Button btnLoginOut;
    private SwipeRefreshLayout mSwipeLayout;

    private ACache mCache;
    private Gson gson;
    private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {

        @Override
        public void onImageClick(HashMap<String, Object> info, int position, View imageView) {
            //此处添加跳转到新的activity
            Bundle bundle = new Bundle();
            bundle.putString("item", info.get("ContentUrl").toString());
            bundle.putString("slide", activity.getResources().getString(R.string.title_newscontent));
            activity.readyGo(TvContentActivity.class, bundle);
        }

        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            if (imageURL.startsWith("http") || imageURL.startsWith("chat_file")) {
                Glide.with(activity).load(imageURL).placeholder(R.drawable.icon_empty).error(R.drawable.icon_error).into(imageView);
            } else {
                Glide.with(activity).load(Integer.parseInt(imageURL)).placeholder(R.drawable.icon_empty).error(R.drawable.icon_error).into(imageView);
            }
        }
    };
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    if (NetManager.isNetworkConnected(activity)) {
                        mCache.remove(CacheKey.HomeRefreshNews);
                        mCache.remove(CacheKey.HomeTrainCount);
                        mCache.remove(CacheKey.HomeAuditCount);
                        mCache.remove(CacheKey.HomeConfirmCount);
                        mCache.remove(CacheKey.HomeStudyCount);
                        mCache.remove(CacheKey.HomeEvaluateCount);
                        mCache.remove(CacheKey.EvaluateRefresh);
                        mCache.remove(CacheKey.CalendarRefresh);
                        mCache.remove(CacheKey.HomeRefreshSlide);
                        mCache.remove(CacheKey.ProfileRefresh);
                        mCache.remove(CacheKey.NimAccountRefresh);
                        new Thread(new HomeThread()).start();
                    } else {
                        ToastUtil.show(activity, activity.getResources().getString(R.string.refreshByNetError));
                    }
                    mSwipeLayout.setRefreshing(false);
                    break;
            }
        }
    };

    public HomeHandler(WeakReference<? extends BaseActivity> myActivity, MainActivity mainActivity) {
        this.myActivity = myActivity;
        this.activity = myActivity.get();
        this.mActivity = mainActivity;
        mSwipeLayout = (SwipeRefreshLayout) activity.findViewById(R.id.id_swipe_ly);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        mSwipeLayout.setProgressViewOffset(true, 0, 50);
        mCache = TrainNetApp.getCache();
        gson = TrainNetApp.getGson();

        initDrawerLayout();
        btnPerson = (ImageButton) activity
                .findViewById(R.id.trainnet_button_right);
        btnPerson.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                activity.readyGo(MoreSettingsActivity.class);
                setDrawerOpenClose();
            }
        });

        ImageView iv_news_more = (ImageView) activity.findViewById(R.id.iv_news_more);
        iv_news_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                activity.readyGo(NewsActivity.class, null);
            }
        });

        View v_body2 = activity.findViewById(R.id.home_body2);
        v_body2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.readyGo(CalendarActivity.class, null);
            }
        });
        setUiContent();
        Waiting.show(activity, activity.getResources().getString(R.string.Loading));
        new Thread(new HomeThread()).start();
    }

    private void setDrawerOpenClose() {
        View v = activity.findViewById(R.id.trainnet_drawer_rightlayout);
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(v);
        if (drawerOpen) {
            mDrawerLayout.closeDrawer(v);
        } else {
            mDrawerLayout.openDrawer(v);
        }
    }

    private void initDrawerLayout() {
        mDrawerLayout = (DrawerLayout) activity
                .findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.trainnet_drawer_shadow,
                GravityCompat.START);
    }

    private void setNumbers(int i) {
        switch (i) {
            case 0:
                View audit = activity.findViewById(R.id.include_audit);
                if (null != audit) {
                    ((TextView) audit.findViewById(R.id.tv_count)).setText(mCache.getAsString(CacheKey.HomeAuditCount));
                }
                break;
            case 1:
                View confirm = activity.findViewById(R.id.include_confirm);
                if (null != confirm) {
                    ((TextView) activity.findViewById(R.id.include_confirm).findViewById(R.id.tv_count)).setText(mCache.getAsString(CacheKey.HomeConfirmCount));
                }
                break;
            case 2:
                View study = activity.findViewById(R.id.include_study);
                if (null != study) {
                    ((TextView) activity.findViewById(R.id.include_study).findViewById(R.id.tv_count)).setText(mCache.getAsString(CacheKey.HomeStudyCount));
                }
                break;
            case 3:
                View evaluate = activity.findViewById(R.id.include_evaluate);
                if (null != evaluate) {
                    ((TextView) activity.findViewById(R.id.include_evaluate).findViewById(R.id.tv_count)).setText(mCache.getAsString(CacheKey.HomeEvaluateCount));
                }
                break;
            default:
                break;
        }
    }

    private void setCalendar() {
        String subjectType = mCache.getAsString(CacheKey.HomeTrainType);
        String startTime = mCache.getAsString(CacheKey.HomeTrainStartTime);
        String finishTime = mCache.getAsString(CacheKey.HomeTrainFinishTime);
        if (!StringUtil.isNullOrEmpty(subjectType)) {
            String type = "类别：" + CalendarUtil.getTypeName(subjectType);
            String time = CalendarUtil.getDate(startTime, finishTime, subjectType);
            ((TextView) activity.findViewById(R.id.lesson_date)).setText(time);
            ((TextView) activity.findViewById(R.id.lesson_type)).setText(type);
            ((TextView) activity.findViewById(R.id.lesson_content)).setText(mCache.getAsString(CacheKey.HomeTrainSubject));
            ((TextView) activity.findViewById(R.id.lesson_count)).setText(mCache.getAsString(CacheKey.HomeTrainCount));
        }
    }

    private void setUiContent() {
        Screen screen = Scale.getScreen(myActivity);
        //int width = (int) (screen.getPxWidth() / 3f);
        //int height = (int) (screen.getPxHeight() * 0.12f);
        ImageView iv_schedule = (ImageView) activity.findViewById(R.id.iv_schedule);
        iv_schedule.getLayoutParams().width = (int) (screen.getPxHeight() * 0.12f * 1.2f);

        ImageView iv_news = (ImageView) activity.findViewById(R.id.iv_news);
        iv_news.getLayoutParams().width = (int) (screen.getPxHeight() * 0.07f);
        ViewFlipper viewFlipper = (ViewFlipper) activity.findViewById(R.id.viewflipper);
        ((RelativeLayout.LayoutParams) viewFlipper.getLayoutParams()).setMargins(0, (int) (screen.getPxHeight() * 0.025f), 0, 0);
        ImageView iv_news_more = (ImageView) activity.findViewById(R.id.iv_news_more);
        iv_news_more.getLayoutParams().height = 80;
        iv_news_more.setPadding(screen.getPxWidth() - (int) (screen.getPxHeight() * 0.025f) - 2 * iv_news_more.getLayoutParams().height - 20, 10, 20, 20);
        viewFlipper.getLayoutParams().height = (int) (screen.getPxHeight() * 0.2f) - (int) (screen.getPxHeight() * 0.025f) - iv_news_more.getLayoutParams().height;


        int[] include = {R.id.include_audit, R.id.include_confirm, R.id.include_study, R.id.include_evaluate};
        int[] resource = {R.drawable.trainnet_home_audit, R.drawable.trainnet_home_confirm, R.drawable.trainnet_home_study, R.drawable.trainnet_home_evaluate};
        int[] widths = {(int) (screen.getPxWidth() * 0.30f), (int) (screen.getPxWidth() * 0.35f), (int) (screen.getPxWidth() * 0.70f), (int) (screen.getPxWidth() * 0.35f)};
        int[] heights = {(int) (screen.getPxHeight() * 0.24f), (int) (screen.getPxHeight() * 0.16f), (int) (screen.getPxHeight() * 0.08f), (int) (screen.getPxHeight() * 0.16f)};
        //final String[] ui = {"待审核", "待确认", "待学习", "待评估"};
        final Class[] clazz = {AwaitAuditActivity.class, AwaitConfirmActivity.class, AwaitStudyActivity.class, AwaitEvaluateActivity.class};
        int[] content = {R.string.NeedAudit, R.string.NeedConfirm, R.string.NeedStudy, R.string.NeedEvaluate};
        ImageView iv;
        TextView tv_content;
        for (int i = 0; i < include.length; i++) {
            activity.findViewById(include[i]).getLayoutParams().width = widths[i];
            activity.findViewById(include[i]).getLayoutParams().height = heights[i];
            final int j = i;
            activity.findViewById(include[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //此处填写跳转到相关菜单界面
                    activity.readyGo(clazz[j], null);
                }
            });
            iv = (ImageView) activity.findViewById(include[i]).findViewById(R.id.iv_state);
            iv.setBackgroundResource(resource[i]);
            tv_content = (TextView) activity.findViewById(include[i]).findViewById(R.id.tv_content);
            tv_content.setText(content[i]);
            tv_content.setPadding((int) (iv.getBackground().getIntrinsicWidth() / 4f), 0, 0, 0);
        }

    }

    private void onLoginDone() {
        loginRequest = null;
    }

    private void setChatLogin(String nimAccount) {
        if (StringUtil.isNullOrEmpty(nimAccount) || !nimAccount.contains("-")) {
            return;
        }
        String[] accounts = nimAccount.split("-");
        final String account = accounts[0];
        final String token = accounts[1];
        MySharedPreferences.getInstance().setStoreString("NimAccount", account);
        final String accessToken = MySharedPreferences.getInstance().getString("AccessToken");
        loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo(account, token));
        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                onLoginDone();
                DemoCache.setAccount(account);
                DemoCache.setToken(accessToken);
                saveLoginInfo(account, token);

                // 初始化消息提醒
                NIMClient.toggleNotification(UserPreferences.getNotificationToggle());

                // 初始化免打扰
                if (UserPreferences.getStatusConfig() == null) {
                    UserPreferences.setStatusConfig(DemoCache.getNotificationConfig());
                }
                NIMClient.updateStatusBarNotificationConfig(UserPreferences.getStatusConfig());

                // 构建缓存
                DataCacheManager.buildDataCacheAsync();

                mActivity.unreadNumChanged(true);
            }

            @Override
            public void onFailed(int code) {
                onLoginDone();
                if (code == 302 || code == 404) {
//                    ToastUtil.show(activity, R.string.login_failed);
                } else {
//                    ToastUtil.show(activity, "登录失败: " + code);
                }
            }

            @Override
            public void onException(Throwable exception) {
//                ToastUtil.show(activity, R.string.login_exception);
                onLoginDone();
            }
        });
    }

    private void saveLoginInfo(final String account, final String token) {
        Preferences.saveUserAccount(account);
        Preferences.saveUserToken(token);
    }

    @Override
    public void onRefresh() {
        mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }

    public class HomeThread implements Runnable {
        public HomeThread() {
        }

        @Override
        public void run() {
            //image
            String HomeRefreshNews = mCache.getAsString(CacheKey.HomeRefreshNews);
            if (!NetManager.isNetworkConnected(activity) && StringUtil.isNullOrEmptyOrEmptySet(HomeRefreshNews)) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Waiting.dismiss();
                        ToastUtil.show(activity, activity.getResources().getString(R.string.showByNetError));
                    }
                });
                return;
            }
            if (!StringUtil.isNullOrEmptyOrEmptySet(HomeRefreshNews)) {
                newsList = gson.fromJson(HomeRefreshNews, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
            } else {
                newsList = TrainNetWebService.getInstance().GetNewsList(activity);
                mCache.put(CacheKey.HomeRefreshNews, gson.toJson(newsList));
            }
            if (null == newsList) {
                newsList = new ArrayList<>();
            }
            //news
            String HomeRefreshSlide = mCache.getAsString(CacheKey.HomeRefreshSlide);
            if (!StringUtil.isNullOrEmptyOrEmptySet(HomeRefreshSlide)) {
                sliderList = gson.fromJson(HomeRefreshSlide, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
            } else {
                sliderList = TrainNetWebService.getInstance().GetAppSlider(activity);
                mCache.put(CacheKey.HomeRefreshSlide, gson.toJson(sliderList));
            }
            if (null == sliderList) {
                sliderList = new ArrayList<>();
            }
            if (sliderList.size() <= 0) {
                String[] imageUrls = {"" + R.drawable.trainnet_image1, "" + R.drawable.trainnet_image2, "" + R.drawable.trainnet_image3};
                for (int i = 0; i < imageUrls.length; i++) {
                    HashMap<String, Object> slider = new HashMap<>();
                    slider.put("PictureUrl", imageUrls[i]);
                    slider.put("ContentUrl", "http://www.zhongdasoft.com");
                    sliderList.add(slider);
                }
            }
            //nim account login
            String NimAccountRefresh = mCache.getAsString(CacheKey.NimAccountRefresh);
            if (StringUtil.isNullOrEmptyOrEmptySet(NimAccountRefresh)) {
                HashMap<String, Object> nimAccountMap = TrainNetWebService.getInstance().ProfileNimAccount(activity);
                mCache.put(CacheKey.NimAccountRefresh, gson.toJson(nimAccountMap));
                if (nimAccountMap.size() > 0) {
                    if ("0".equals(nimAccountMap.get(activity.getResources().getString(R.string.ReturnCode)).toString())) {
                        String result = nimAccountMap.get(activity.getResources().getString(R.string.Result)).toString();
//                        result = MySharedPreferences.getInstance().getUserName(activity)+"-1234567890";
                        setChatLogin(result);
                    }
                }
            }


            //train calendar
            if (StringUtil.isNullOrEmptyOrEmptySet(mCache.getAsString(CacheKey.HomeTrainCount))) {
                Calendar c = Calendar.getInstance();
                c.setTime(new Date());
                int beginDay = c.get(Calendar.DATE);
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                c.add(Calendar.DATE, 30);
                int nextYear = c.get(Calendar.YEAR);
                int nextMonth = c.get(Calendar.MONTH) + 1;
                int endDay = c.get(Calendar.DATE);
                String beginDate = year + "-" + (month < 10 ? "0" + month : month) + "-" + (beginDay < 10 ? "0" + beginDay : beginDay);
                String endDate = nextYear + "-" + (nextMonth < 10 ? "0" + nextMonth : nextMonth) + "-" + (endDay < 10 ? "0" + endDay : endDay);
                final ArrayList<HashMap<String, Object>> dateList = TrainNetWebService.getInstance().GetSchedule(activity, beginDate, endDate);
                String processCount = "0";
                if (dateList.size() > 0) {
                    HashMap<String, Object> map = dateList.get(0);
                    String startTime = StringUtil.objectToStr(map.get("StartTime"));
                    String finishTime = "";
                    String type = "";
                    //String place = "";
                    String subject = "";
                    String tmpStartTime;
                    int k = -1;
                    int count = 0;
                    ArrayList<String> notifyList = new ArrayList<>();
                    for (int i = 0; i < dateList.size(); i++) {
                        map = dateList.get(i);
                        if (!map.containsKey("Subject")) {
                            continue;
                        }
                        notifyList.add(CalendarUtil.getTypeName(map.get("ScheduleType").toString()));
                        notifyList.add(map.get("Subject").toString());
                        notifyList.add(map.get("StartTime").toString().replace("T", " "));
                        String place = StringUtil.objectToStr(map.get("Place"));
                        notifyList.add(CalendarUtil.getPlaceName(place, map.get("ScheduleType").toString()));
                        count++;
                        tmpStartTime = StringUtil.objectToStr(map.get("StartTime"));
                        if (startTime.compareTo(tmpStartTime) >= 0) {
                            k = i;
                            startTime = tmpStartTime;
                        }
                    }
                    //发送通知
                    NotifyUtil.sendNotify(activity, notifyList);
                    if (k >= 0) {
                        map = dateList.get(k);
                        if (map.containsKey("StartTime")) {
                            startTime = StringUtil.objectToStr(map.get("StartTime")).substring(0, 16);
                            finishTime = StringUtil.objectToStr(map.get("FinishTime")).substring(0, 16);
                            type = map.get("ScheduleType").toString();
                            subject = "名称：" + map.get("Subject");
                            //place = CalendarUtil.getPlaceName(dateList.get(k).get("Place").toString(), dateList.get(k).get("ScheduleType").toString());
                        }
                    }
                    processCount = count > 99 ? "99+" : count + "";
                    mCache.put(CacheKey.HomeTrainStartTime, startTime);
                    mCache.put(CacheKey.HomeTrainFinishTime, finishTime);
                    mCache.put(CacheKey.HomeTrainType, type);
                    mCache.put(CacheKey.HomeTrainSubject, subject);
                }
                mCache.put(CacheKey.HomeTrainCount, processCount);
                mCache.put(CacheKey.CalendarRefresh, gson.toJson(dateList));
            }
            if (StringUtil.isNullOrEmptyOrEmptySet(mCache.getAsString(CacheKey.HomeAuditCount))) {
                //audit
                final ArrayList<HashMap<String, Object>> auditList = TrainNetWebService.getInstance().GetPendingAuditList(activity);
                int count = 0;
                String processCount;
                for (HashMap<String, Object> map : auditList) {
                    if ("ApiPendingAudit".equals(map.get(activity.getResources().getString(R.string.ParentNode)))) {
                        count++;
                    }
                }
                processCount = count > 99 ? "99+" : count + "";
                mCache.put(CacheKey.HomeAuditCount, processCount);
                mCache.put(CacheKey.AuditRefresh, gson.toJson(auditList));
            }
            if (StringUtil.isNullOrEmptyOrEmptySet(mCache.getAsString(CacheKey.HomeConfirmCount))) {
                //confirm
                final ArrayList<HashMap<String, Object>> confirmList = TrainNetWebService.getInstance().GetConfirmList(activity);
                int count = 0;
                String processCount;
                for (HashMap<String, Object> map : confirmList) {
                    if ("ApiApply".equals(map.get(activity.getResources().getString(R.string.ParentNode)))) {
                        count++;
                    }
                }
                processCount = count > 99 ? "99+" : count + "";
                mCache.put(CacheKey.HomeConfirmCount, processCount);
                mCache.put(CacheKey.ConfirmRefresh, gson.toJson(confirmList));
            }
            if (StringUtil.isNullOrEmptyOrEmptySet(mCache.getAsString(CacheKey.HomeStudyCount))) {
                //study
                ArrayList<HashMap<String, Object>> studyList = TrainNetWebService.getInstance().OnlineCourseList(activity, "-1");
                ArrayList<HashMap<String, Object>> listItem = WebserviceUtil.getInstance().onlineCourseList(activity, studyList, true);
                String processCount;
                int count = listItem.size();
                processCount = count > 99 ? "99+" : count + "";
                mCache.put(CacheKey.HomeStudyCount, processCount);
                mCache.put(CacheKey.StudyRefresh, gson.toJson(listItem));
            }
            if (StringUtil.isNullOrEmptyOrEmptySet(mCache.getAsString(CacheKey.HomeEvaluateCount))) {
                //evaluate
                final ArrayList<HashMap<String, Object>> evaluateList = TrainNetWebService.getInstance().EvaluationList(activity);
                int count = 0;
                String processCount;
                for (HashMap<String, Object> map : evaluateList) {
                    if ("ApiEvaluation".equals(map.get(activity.getResources().getString(R.string.ParentNode)))) {
                        count++;
                    }
                }
                processCount = count > 99 ? "99+" : count + "";
                TrainNetApp.getCache().put(CacheKey.HomeEvaluateCount, processCount);
                TrainNetApp.getCache().put(CacheKey.EvaluateRefresh, TrainNetApp.getGson().toJson(evaluateList));
            }

            if (StringUtil.isNullOrEmptyOrEmptySet(mCache.getAsString(CacheKey.ProfileRefresh))) {
                WebserviceUtil.getInstance().profileRefresh(activity);
                WebserviceUtil.getInstance().profilePhotoRefresh(activity);
            }
            //setui
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setCalendar();
                    ListViewUtil.setProfileListView(myActivity, true);
                    for (int i = 0; i < 4; i++) {
                        setNumbers(i);
                    }
                    int count;
                    mAdView = (ImageCycleView) activity.findViewById(R.id.ad_view);
                    mAdView.setImageResources(sliderList, mAdCycleViewListener);

                    viewFlipper = (ViewFlipper) activity.findViewById(R.id.viewflipper);
                    LinearLayout ll;
                    LinearLayout ll_line;
                    TextView tv_date;
                    TextView tv_title;
                    int[] date = {R.id.tv_date1, R.id.tv_date2, R.id.tv_date3};
                    int[] title = {R.id.tv_title1, R.id.tv_title2, R.id.tv_title3};
                    int[] line = {R.id.ll_line_flipper1, R.id.ll_line_flipper2, R.id.ll_line_flipper3};
                    LayoutInflater inflater = LayoutInflater.from(activity);
                    count = newsList.size();
                    int lCount = count % showLines == 0 ? count / showLines : count / showLines + 1;
                    for (int i = 0; i < lCount; i++) {
                        ll = (LinearLayout) inflater.inflate(R.layout.trainnet_item_flipper, null);
                        for (int j = 0; j < showLines && i * showLines + j < count; j++) {
                            tv_date = (TextView) ll.findViewById(date[j]);
                            tv_title = (TextView) ll.findViewById(title[j]);
                            tv_date.setText(newsList.get(i * 3 + j).get("AddDate").toString().substring(0, 10) + " ");
                            tv_title.setText(HtmlUtil.fromHtml(newsList.get(i * 3 + j).get("Title").toString()));
                            ll_line = (LinearLayout) ll.findViewById(line[j]);
                            ll_line.setTag(newsList.get(i * 3 + j).get("Id").toString());
                            ll_line.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    Object tag = arg0.getTag();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("item", (String) tag);
                                    bundle.putString("news", activity.getResources().getString(R.string.title_newscontent));
                                    activity.readyGo(TvContentActivity.class, bundle);
                                }
                            });
                        }
                        viewFlipper.addView(ll, i);
                    }
                    viewFlipper.showNext();
                    viewFlipper.startFlipping();
                    Waiting.dismiss();
                }
            });
        }
    }
}
