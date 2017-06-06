package com.zhongdasoft.svwtrainnet.module.home;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zhongdasoft.svwtrainnet.base.BaseActivity;
import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.greendao.Cache.CacheKey;
import com.zhongdasoft.svwtrainnet.util.CalendarUtil;
import com.zhongdasoft.svwtrainnet.util.StringUtil;
import com.zhongdasoft.svwtrainnet.adapter.BaseListViewAdapter;
import com.zhongdasoft.svwtrainnet.widget.CalendarView;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/4/22.
 */
public class CalendarActivity extends BaseActivity {
    private CalendarView calendar;
    private ImageButton calendarLeft;
    private TextView calendarCenter;
    private ImageButton calendarRight;
    private SimpleDateFormat format;
    private ListView eventContent;

    private String beginDate;
    private String endDate;
    private int beginDay;
    private int endDay;
    private Calendar c;
    private ArrayList<HashMap<String, Object>> dateList;
    private WeakReference<? extends BaseActivity> wr;
    private ArrayList<HashMap<String, Object>> listItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wr = new WeakReference<>(this);

        format = new SimpleDateFormat("yyyy-MM-dd");
        //获取日历控件对象
        calendar = (CalendarView) findViewById(R.id.calendar);
        calendar.setSelectMore(false); //多选

        calendarLeft = (ImageButton) findViewById(R.id.calendarLeft);
        calendarCenter = (TextView) findViewById(R.id.calendarCenter);
        calendarRight = (ImageButton) findViewById(R.id.calendarRight);
        eventContent = (ListView) findViewById(R.id.eventContent);

        c = Calendar.getInstance();
        try {
            //设置日历日期
            c.setTime(new Date());
            Date date = format.parse(c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE));
            setDate(c);
            calendar.setCalendarData(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //获取日历中年月 ya[0]为年，ya[1]为月（格式大家可以自行在日历控件中改）
        String[] ya = calendar.getYearAndmonth().split("-");
        calendarCenter.setText(ya[0] + "年" + ya[1] + "月");
        calendarLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //点击上一月 同样返回年月
                String leftYearAndmonth = calendar.clickLeftMonth();
                String[] ya = leftYearAndmonth.split("-");
                calendarCenter.setText(ya[0] + "年" + ya[1] + "月");
//                if (!StringUtil.isNullOrEmpty(calendar.getCurrentDay())) {
//                    setEventContent(ya[0] + "-" + ya[1] + "-" + calendar.getCurrentDay());
//                }
            }
        });

        calendarRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //点击下一月
                String rightYearAndmonth = calendar.clickRightMonth();
                String[] ya = rightYearAndmonth.split("-");
                calendarCenter.setText(ya[0] + "年" + ya[1] + "月");
//                if (!StringUtil.isNullOrEmpty(calendar.getCurrentDay())) {
//                    setEventContent(ya[0] + "-" + ya[1] + "-" + calendar.getCurrentDay());
//                }
            }
        });

        //设置控件监听，可以监听到点击的每一天（大家也可以在控件中根据需求设定）
        calendar.setOnItemClickListener(new CalendarView.OnItemClickListener() {

            @Override
            public void OnItemClick(Date selectedStartDate,
                                    Date selectedEndDate, Date downDate) {
                if (calendar.isSelectMore()) {
                    //eventContent.setText(format.format(selectedStartDate) + "到" + format.format(selectedEndDate));
                } else {
                    String dateStr = format.format(downDate);
                    setEventContent(dateStr);
                }
            }
        });

        String CalendarRefresh = getCache().getAsString(CacheKey.CalendarRefresh);
        if (!StringUtil.isNullOrEmptyOrEmptySet(CalendarRefresh)) {
            dateList = getGson().fromJson(CalendarRefresh, new TypeToken<ArrayList<HashMap<String, Object>>>() {
            }.getType());
        } else {
            dateList = new ArrayList<>();
        }

        calendar.setDateEventList(dateList);
        setEventContent(beginDate);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.trainnet_calendar;
    }

    @Override
    protected int getMTitle() {
        return R.string.calendar;
    }

    private void setEventContent(String dateStr) {
        if (listItem == null) {
            listItem = new ArrayList<>();
        } else {
            listItem.clear();
        }
        HashMap<String, Object> newMap;
        if (!calendar.isCurrentMonth(dateStr)) {
            setListView();
            return;
        }
        String startTime;
        String finishTime;
        if (null != dateList) {
            for (Map<String, Object> map : dateList) {
                if (!map.containsKey("Subject")) {
                    continue;
                }
                startTime = StringUtil.objectToStr(map.get("StartTime")).substring(0, 16);
                finishTime = StringUtil.objectToStr(map.get("FinishTime")).substring(0, 16);
                if (dateStr.compareTo(startTime.substring(0, 10)) >= 0 && dateStr.compareTo(finishTime.substring(0, 10)) <= 0) {
                    newMap = new HashMap<>();
                    newMap.put("info1", CalendarUtil.getDate(startTime, finishTime, map.get("ScheduleType").toString()));
                    newMap.put("info2", "类别：" + CalendarUtil.getTypeName(map.get("ScheduleType").toString()));
                    newMap.put("info3", "名称：" + map.get("Subject").toString());
                    String place = (null == map.get("Place") ? "" : map.get("Place").toString());
                    newMap.put("info4", CalendarUtil.getPlaceName(place, map.get("ScheduleType").toString()));
                    newMap.put("noRight", "");
                    listItem.add(newMap);
                }
            }
        }
        if (listItem.size() > 0) {
            listItem.get(listItem.size() - 1).put("noLine", "");
        }
        setListView();
    }

    private void setDate(Calendar c) {
        beginDay = c.get(Calendar.DATE);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        c.add(Calendar.DATE, 30);
        int nextYear = c.get(Calendar.YEAR);
        int nextMonth = c.get(Calendar.MONTH) + 1;
        endDay = c.getActualMaximum(Calendar.DATE);
        beginDate = year + "-" + (month < 10 ? "0" + month : month) + "-" + (beginDay < 10 ? "0" + beginDay : beginDay);
        endDate = nextYear + "-" + (nextMonth < 10 ? "0" + nextMonth : nextMonth) + "-" + endDay;
    }

    private void setListView() {
        BaseListViewAdapter mAdapter = new BaseListViewAdapter(this, listItem);
        eventContent.setDivider(null);
        eventContent.setAdapter(mAdapter);
        eventContent.setSelector(getResources().getDrawable(R.color.light_gray));
        eventContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

