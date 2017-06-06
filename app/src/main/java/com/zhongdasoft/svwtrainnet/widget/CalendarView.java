package com.zhongdasoft.svwtrainnet.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.zhongdasoft.svwtrainnet.R;
import com.zhongdasoft.svwtrainnet.util.CalendarUtil;
import com.zhongdasoft.svwtrainnet.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日历控件 功能：获得点选的日期区间
 * Created by Administrator on 2016/4/22.
 */
public class CalendarView extends View implements View.OnTouchListener {
    private final static String TAG = "anCalendar";
    private Date selectedStartDate;
    private Date selectedEndDate;
    private Date curDate; // 当前日历显示的月
    private Date today; // 今天的日期文字显示红色
    private Date downDate; // 手指按下状态时临时日期
    private Date showFirstDate, showLastDate; // 日历显示的第一个日期和最后一个日期
    private int downIndex; // 按下的格子索引
    private Calendar calendar;
    private Surface surface;
    private int[] date = new int[42]; // 日历显示数字
    private int gridNumbers = 42;
    private int curStartIndex, curEndIndex; // 当前显示的日历起始的索引
    private boolean completed = false; // 为false表示只选择了开始日期，true表示结束日期也选择了
    private boolean isSelectMore = false;
    private ArrayList<HashMap<String, Object>> dateEventList;
    //给控件设置监听事件
    private OnItemClickListener onItemClickListener;

    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        curDate = selectedStartDate = selectedEndDate = today = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(curDate);
        surface = new Surface();
        surface.density = getResources().getDisplayMetrics().density;
        setBackgroundColor(surface.bgColor);
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        surface.width = getResources().getDisplayMetrics().widthPixels;
        surface.height = getResources().getDisplayMetrics().heightPixels * 3 / 5;
//		if (View.MeasureSpec.getMode(widthMeasureSpec) == View.MeasureSpec.EXACTLY) {
//			surface.width = View.MeasureSpec.getSize(widthMeasureSpec);
//		}
//		if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.EXACTLY) {
//			surface.height = View.MeasureSpec.getSize(heightMeasureSpec);
//		}
        widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.width,
                View.MeasureSpec.EXACTLY);
        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.height,
                View.MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
//        Log.d(TAG, "[onLayout] changed:"
//                + (changed ? "new size" : "not change") + " left:" + left
//                + " top:" + top + " right:" + right + " bottom:" + bottom);
        if (changed) {
            surface.init();
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d(TAG, "onDraw");
        // 画框
        canvas.drawPath(surface.boxPath, surface.borderPaint);
        // 年月
        //String monthText = getYearAndmonth();
        //float textWidth = surface.monthPaint.measureText(monthText);
        //canvas.drawText(monthText, (surface.width - textWidth) / 2f,
        //		surface.monthHeight * 3 / 4f, surface.monthPaint);
        // 上一月/下一月
        //canvas.drawPath(surface.preMonthBtnPath, surface.monthChangeBtnPaint);
        //canvas.drawPath(surface.nextMonthBtnPath, surface.monthChangeBtnPaint);
        // 星期
        float weekTextY = surface.monthHeight + surface.weekHeight * 3 / 4f;
        // 星期背景
//		surface.cellBgPaint.setColor(surface.textColor);
//		canvas.drawRect(surface.weekHeight, surface.width, surface.weekHeight, surface.width, surface.cellBgPaint);
        for (int i = 0; i < surface.weekText.length; i++) {
            if (i == 0) {
                drawCellBg(canvas, -1, surface.weekColor);
            } else if (i == surface.weekText.length - 1) {
                drawCellBg(canvas, -7, surface.weekColor);
            }
            float weekTextX = i
                    * surface.cellWidth
                    + (surface.cellWidth - surface.weekPaint
                    .measureText(surface.weekText[i])) / 2f;
            canvas.drawText(surface.weekText[i], weekTextX, weekTextY,
                    surface.weekPaint);
        }

        // 计算日期
        calculateDate();
        //设置周末背景色
        for (int i = 1; i <= gridNumbers; i++) {
            if (i % 7 == 0 || i % 7 == 1) {
                drawCellBg(canvas, i - 1, surface.weekColor);
            }
        }
        // 按下状态，选择状态背景色
        drawDownOrSelectedBg(canvas);
        // write date number
        // today index
        int todayIndex = -1;
        calendar.setTime(curDate);
        String curYearAndMonth = calendar.get(Calendar.YEAR) + ""
                + calendar.get(Calendar.MONTH);
        calendar.setTime(today);
        String todayYearAndMonth = calendar.get(Calendar.YEAR) + ""
                + calendar.get(Calendar.MONTH);
        if (curYearAndMonth.equals(todayYearAndMonth)) {
            int todayNumber = calendar.get(Calendar.DAY_OF_MONTH);
            todayIndex = curStartIndex + todayNumber - 1;
        }
        for (int i = 0; i < gridNumbers; i++) {
            int color = surface.textColor;
            if (isLastMonth(i) || isNextMonth(i)) {
//                if ((i + 1) % 7 == 0 || (i + 1) % 7 == 1) {
//                    color = surface.weekColor;
//                } else {
//                    color = surface.bgColor;
//                }
                color = surface.borderColor;
            }
            if (todayIndex != -1 && i == todayIndex) {
                color = surface.todayNumberColor;
            }
            drawCellText(canvas, i, date[i] + "", color);
        }
        super.onDraw(canvas);
    }

    private void calculateDate() {
        calendar.setTime(curDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);
//        Log.d(TAG, "day in week:" + dayInWeek);
        int monthStart = dayInWeek;
        if (monthStart == 1) {
            monthStart = 8;
        }
        monthStart -= 1;  //以日为开头-1，以星期一为开头-2
        curStartIndex = monthStart;
        date[monthStart] = 1;
        // last month
        if (monthStart > 0) {
            calendar.set(Calendar.DAY_OF_MONTH, 0);
            int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
            for (int i = monthStart - 1; i >= 0; i--) {
                date[i] = dayInmonth;
                dayInmonth--;
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[0]);
        }
        showFirstDate = calendar.getTime();
        // this month
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        // Log.d(TAG, "m:" + calendar.get(Calendar.MONTH) + " d:" +
        // calendar.get(Calendar.DAY_OF_MONTH));
        int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
        for (int i = 1; i < monthDay; i++) {
            date[monthStart + i] = i + 1;
        }
        curEndIndex = monthStart + monthDay;
        // next month
        for (int i = monthStart + monthDay; i < gridNumbers; i++) {
            date[i] = i - (monthStart + monthDay) + 1;
        }
        if (curEndIndex < gridNumbers) {
            // 显示了下一月的
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.DAY_OF_MONTH, date[gridNumbers - 1]);
        showLastDate = calendar.getTime();
    }

    /**
     * @param canvas
     * @param index
     * @param text
     */
    private void drawCellText(Canvas canvas, int index, String text, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.datePaint.setColor(color);
        float cellY = surface.monthHeight + surface.weekHeight + (y - 1)
                * surface.cellHeight + surface.cellHeight * 5 / 8f;
        float cellX = (surface.cellWidth * (x - 1))
                + (surface.cellWidth - surface.datePaint.measureText(text))
                / 2f;
        canvas.drawText(text, cellX, cellY, surface.datePaint);
        String[] ym = getYearAndmonth().split("-");
        int year, month, day;
        year = Integer.parseInt(ym[0]);
        month = Integer.parseInt(ym[1]);
        day = Integer.parseInt(text);
        if (index < curStartIndex || index >= curEndIndex) {
            if (index < curStartIndex) {
                if (month == 1) {
                    year -= 1;
                    month = 12;
                } else {
                    month -= 1;
                }
            } else {
                if (month == 12) {
                    year += 1;
                    month = 1;
                } else {
                    month += 1;
                }
            }
        }
        CalendarUtil c = new CalendarUtil();
        String textLunar = c.toLunarString(year, month, day);
        cellY = surface.monthHeight + surface.weekHeight + (y - 1)
                * surface.cellHeight + surface.cellHeight * 7 / 8f;
        cellX = (surface.cellWidth * (x - 1))
                + (surface.cellWidth - surface.dateLunarPaint.measureText(textLunar))
                / 2f;
        if (!isLastMonth(index) && !isNextMonth(index)) {
            surface.dateLunarPaint.setColor(surface.textColor);
        } else {
            surface.dateLunarPaint.setColor(surface.borderColor);
        }
        canvas.drawText(textLunar, cellX, cellY, surface.dateLunarPaint);
        if (dateHasEvent(index, text) && !isLastMonth(index) && !isNextMonth(index)) {
            cellY = surface.monthHeight + surface.weekHeight + (y - 1)
                    * surface.cellHeight + surface.cellHeight * 1 / 8f;
            cellX = (surface.cellWidth * (x - 1))
                    + surface.cellWidth / 2f;
            canvas.drawCircle(cellX, cellY, surface.cellHeight * 1 / 12f, surface.dateSelectPaint);
        }
    }

    /**
     * @param text
     */
    private boolean dateHasEvent(int index, String text) {
        //这里需要根据接口获取活动日程
        if (index < curStartIndex || index > curEndIndex) {
            return false;
        }
        String date;
        if (text.length() < 2) {
            date = getYearAndmonth() + "-0" + text;
        } else {
            date = getYearAndmonth() + "-" + text;
        }
        boolean hasEvent = false;
        if (null != dateEventList) {
            for (Map<String, Object> map : dateEventList) {
                if (!map.containsKey("StartTime")) {
                    continue;
                }
                String startTime;
                String finishTime;
                startTime = StringUtil.objectToStr(map.get("StartTime")).substring(0, 10);
                finishTime = StringUtil.objectToStr(map.get("FinishTime")).substring(0, 10);
                if (date.compareTo(startTime) >= 0 && date.compareTo(finishTime) <= 0) {
                    hasEvent = true;
                    break;
                }
            }
            return hasEvent;
        }
        return false;
    }

    public void setDateEventList(ArrayList<HashMap<String, Object>> dateEventList) {
        this.dateEventList = dateEventList;
        invalidate();
    }

    /**
     * @param canvas
     * @param index
     * @param color
     */
    private void drawCellBg(Canvas canvas, int index, int color) {
        int x = getXByIndex(index);
        int y = getYByIndex(index);
        surface.cellBgPaint.setColor(color);
        float left;
        float top;
        if (x == 0 || y == 0) {
            if (x == 0) {
                left = surface.borderWidth;
                top = surface.monthHeight + surface.borderWidth;
            } else {
                left = surface.cellWidth * 6 + surface.borderWidth;
                top = surface.monthHeight + surface.borderWidth;
            }
            canvas.drawRect(left, top, left + surface.cellWidth
                    - 2 * surface.borderWidth, top + surface.weekHeight
                    - 2 * surface.borderWidth, surface.cellBgPaint);
        } else {
            left = surface.cellWidth * (x - 1) + surface.borderWidth;
            top = surface.monthHeight + surface.weekHeight + (y - 1)
                    * surface.cellHeight + surface.borderWidth;
            canvas.drawRect(left, top, left + surface.cellWidth
                    - 2 * surface.borderWidth, top + surface.cellHeight
                    - 2 * surface.borderWidth, surface.cellBgPaint);
        }
    }

    private void drawDownOrSelectedBg(Canvas canvas) {
        // down and not up
        if (downDate != null && !isLastMonth(downIndex) && !isNextMonth(downIndex)) {
            drawCellBg(canvas, downIndex, surface.cellDownColor);
        }
        // selected bg color
        if (!selectedEndDate.before(showFirstDate)
                && !selectedStartDate.after(showLastDate)) {
            int[] section = new int[]{-1, -1};
            calendar.setTime(curDate);
            calendar.add(Calendar.MONTH, -1);
            findSelectedIndex(0, curStartIndex, calendar, section);
            if (section[1] == -1) {
                calendar.setTime(curDate);
                findSelectedIndex(curStartIndex, curEndIndex, calendar, section);
            }
            if (section[1] == -1) {
                calendar.setTime(curDate);
                calendar.add(Calendar.MONTH, 1);
                findSelectedIndex(curEndIndex, gridNumbers, calendar, section);
            }
            if (section[0] == -1) {
                section[0] = 0;
            }
            if (section[1] == -1) {
                section[1] = gridNumbers - 1;
            }
            for (int i = section[0]; i <= section[1]; i++) {
                if (!isLastMonth(i) && !isNextMonth(i)) {
                    drawCellBg(canvas, i, surface.cellSelectedColor);
                }
            }
        }
    }

    private void findSelectedIndex(int startIndex, int endIndex,
                                   Calendar calendar, int[] section) {
        for (int i = startIndex; i < endIndex; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, date[i]);
            Date temp = calendar.getTime();
            // Log.d(TAG, "temp:" + temp.toLocaleString());
            if (temp.compareTo(selectedStartDate) == 0) {
                section[0] = i;
            }
            if (temp.compareTo(selectedEndDate) == 0) {
                section[1] = i;
                return;
            }
        }
    }

    public Date getSelectedStartDate() {
        return selectedStartDate;
    }

    public Date getSelectedEndDate() {
        return selectedEndDate;
    }

    private boolean isLastMonth(int i) {
        return i < curStartIndex;
    }

    private boolean isNextMonth(int i) {
        return i >= curEndIndex;
    }

    private int getXByIndex(int i) {
        return i % 7 + 1; // 1 2 3 4 5 6 7
    }

    private int getYByIndex(int i) {
        return i / 7 + 1; // 1 2 3 4 5 6
    }

    // 获得当前应该显示的年月
    public String getYearAndmonth() {
        calendar.setTime(curDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        if (month < 10) {
            return year + "-0" + month;
        }
        return year + "-" + month;
    }

    //上一月
    public String clickLeftMonth() {
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, -1);
        curDate = calendar.getTime();
        invalidate();
        return getYearAndmonth();
    }

    //下一月
    public String clickRightMonth() {
        calendar.setTime(curDate);
        calendar.add(Calendar.MONTH, 1);
        curDate = calendar.getTime();
        invalidate();
        return getYearAndmonth();
    }

    public String getCurrentDay() {
        if (isLastMonth(downIndex) || isNextMonth(downIndex)) {
            return "";
        }
        return "" + date[downIndex];
    }

    //设置日历时间
    public void setCalendarData(Date date) {
        calendar.setTime(date);
        invalidate();
    }

    //获取日历时间
    public void getCalendatData() {
        calendar.getTime();
    }

    //设置是否多选
    public boolean isSelectMore() {
        return isSelectMore;
    }

    public void setSelectMore(boolean isSelectMore) {
        this.isSelectMore = isSelectMore;
    }

    private void setSelectedDateByCoor(float x, float y) {
        // change month
//		if (y < surface.monthHeight) {
//			// pre month
//			if (x < surface.monthChangeWidth) {
//				calendar.setTime(curDate);
//				calendar.add(Calendar.MONTH, -1);
//				curDate = calendar.getTime();
//			}
//			// next month
//			else if (x > surface.width - surface.monthChangeWidth) {
//				calendar.setTime(curDate);
//				calendar.add(Calendar.MONTH, 1);
//				curDate = calendar.getTime();
//			}
//		}
        // cell click down
        if (y > surface.monthHeight + surface.weekHeight) {
            int m = (int) (Math.floor(x / surface.cellWidth) + 1);
            int n = (int) (Math
                    .floor((y - (surface.monthHeight + surface.weekHeight))
                            / Float.valueOf(surface.cellHeight)) + 1);
            downIndex = (n - 1) * 7 + m - 1;
//            Log.d(TAG, "downIndex:" + downIndex);
            calendar.setTime(curDate);
            if (isLastMonth(downIndex)) {
                calendar.add(Calendar.MONTH, -1);
            } else if (isNextMonth(downIndex)) {
                calendar.add(Calendar.MONTH, 1);
            }
            calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
            downDate = calendar.getTime();
        }
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setSelectedDateByCoor(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                if (downDate != null) {
                    if (isSelectMore) {
                        if (!completed) {
                            if (downDate.before(selectedStartDate)) {
                                selectedEndDate = selectedStartDate;
                                selectedStartDate = downDate;
                            } else {
                                selectedEndDate = downDate;
                            }
                            completed = true;
                            //响应监听事件
                            onItemClickListener.OnItemClick(selectedStartDate, selectedEndDate, downDate);
                        } else {
                            selectedStartDate = selectedEndDate = downDate;
                            completed = false;
                        }
                    } else {
                        selectedStartDate = selectedEndDate = downDate;
                        //响应监听事件
                        onItemClickListener.OnItemClick(selectedStartDate, selectedEndDate, downDate);
                    }
                    invalidate();
                }

                break;
        }
        return true;
    }

    //给控件设置监听事件
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public boolean isCurrentMonth(String dateStr) {
        return dateStr.substring(0, 7).equals(getYearAndmonth());
    }

    //监听接口
    public interface OnItemClickListener {
        void OnItemClick(Date selectedStartDate, Date selectedEndDate, Date downDate);
    }

    /**
     * 1. 布局尺寸 2. 文字颜色，大小 3. 当前日期的颜色，选择的日期颜色
     */
    private class Surface {
        public float density;
        public int width; // 整个控件的宽度
        public int height; // 整个控件的高度
        public float monthHeight; // 显示月的高度
        //public float monthChangeWidth; // 上一月、下一月按钮宽度
        public float weekHeight; // 显示星期的高度
        public float cellWidth; // 日期方框宽度
        public float cellHeight; // 日期方框高度
        public float borderWidth;
        public int bgColor = getResources().getColor(R.color.white);
        public int todayNumberColor = Color.RED;
        public int cellDownColor = getResources().getColor(R.color.calendar_down_color);
        public int cellSelectedColor = getResources().getColor(R.color.calendar_selected_color);
        public Paint borderPaint;
        public Paint monthPaint;
        public Paint weekPaint;
        public Paint datePaint;
        public Paint dateLunarPaint;
        public Paint dateSelectPaint;
        public Paint monthChangeBtnPaint;
        public Paint cellBgPaint;
        public Path boxPath; // 边框路径
        //public Path preMonthBtnPath; // 上一月按钮三角形
        //public Path nextMonthBtnPath; // 下一月按钮三角形
        //public String[] weekText = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        public String[] weekText = {"日", "一", "二", "三", "四", "五", "六"};
        private int textColor = Color.BLACK;
        //private int textColorUnimportant = getResources().getColor(R.color.dark_gray);
        private int btnColor = getResources().getColor(R.color.dark_gray);
        private int borderColor = getResources().getColor(R.color.light_gray);
        private int weekColor = getResources().getColor(R.color.white1);
        //public String[] monthText = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        public void init() {
            float temp = height / 7f;
            monthHeight = 0;//(float) ((temp + temp * 0.3f) * 0.6);
            //monthChangeWidth = monthHeight * 1.5f;
            weekHeight = (float) ((temp + temp * 0.3f) * 0.7);
            cellHeight = (height - monthHeight - weekHeight) / 6f;
            cellWidth = width / 7f;
            borderPaint = new Paint();
            borderPaint.setColor(borderColor);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderWidth = (float) (0.5 * density);
            // Log.d(TAG, "borderwidth:" + borderWidth);
            borderWidth = borderWidth < 1 ? 1 : borderWidth;
            borderPaint.setStrokeWidth(borderWidth);
            monthPaint = new Paint();
            monthPaint.setColor(textColor);
            monthPaint.setAntiAlias(true);
            float textSize = cellHeight * 0.4f;
            //Log.d(TAG, "text size:" + textSize);
            monthPaint.setTextSize(textSize);
            monthPaint.setTypeface(Typeface.DEFAULT_BOLD);
            weekPaint = new Paint();
            weekPaint.setColor(textColor);
            weekPaint.setAntiAlias(true);
            float weekTextSize = weekHeight * 0.6f;
            weekPaint.setTextSize(weekTextSize);
            weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
            datePaint = new Paint();
            datePaint.setColor(textColor);
            datePaint.setAntiAlias(true);
            float cellTextSize = cellHeight * 0.5f;
            datePaint.setTextSize(cellTextSize);
            //datePaint.setTypeface(Typeface.DEFAULT_BOLD);
            dateLunarPaint = new Paint();
            dateLunarPaint.setColor(textColor);
            dateLunarPaint.setAntiAlias(true);
            dateLunarPaint.setTextSize(cellHeight * 0.2f);
            dateSelectPaint = new Paint();
            dateSelectPaint.setColor(getResources().getColor(R.color.app_blue));
            dateSelectPaint.setAntiAlias(true);
            boxPath = new Path();
            //boxPath.addRect(0, 0, width, height, Direction.CW);
            //boxPath.moveTo(0, monthHeight);
            boxPath.rLineTo(width, 0);
            boxPath.moveTo(0, monthHeight + weekHeight);
            boxPath.rLineTo(width, 0);
            for (int i = 1; i < 6; i++) {
                boxPath.moveTo(0, monthHeight + weekHeight + i * cellHeight);
                boxPath.rLineTo(width, 0);
                boxPath.moveTo(i * cellWidth, monthHeight);
                boxPath.rLineTo(0, height - monthHeight);
            }
            boxPath.moveTo(6 * cellWidth, monthHeight);
            boxPath.rLineTo(0, height - monthHeight);
            //preMonthBtnPath = new Path();
            //int btnHeight = (int) (monthHeight * 0.6f);
            //preMonthBtnPath.moveTo(monthChangeWidth / 2f, monthHeight / 2f);
            //preMonthBtnPath.rLineTo(btnHeight / 2f, -btnHeight / 2f);
            //preMonthBtnPath.rLineTo(0, btnHeight);
            //preMonthBtnPath.close();
            //nextMonthBtnPath = new Path();
            //nextMonthBtnPath.moveTo(width - monthChangeWidth / 2f,
            //		monthHeight / 2f);
            //nextMonthBtnPath.rLineTo(-btnHeight / 2f, -btnHeight / 2f);
            //nextMonthBtnPath.rLineTo(0, btnHeight);
            //nextMonthBtnPath.close();
            monthChangeBtnPaint = new Paint();
            monthChangeBtnPaint.setAntiAlias(true);
            monthChangeBtnPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            monthChangeBtnPaint.setColor(btnColor);
            cellBgPaint = new Paint();
            cellBgPaint.setAntiAlias(true);
            cellBgPaint.setStyle(Paint.Style.FILL);
            cellBgPaint.setColor(cellSelectedColor);
        }
    }
}

